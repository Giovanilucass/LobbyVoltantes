package server;
import compartilhado.mensagens.*;
import compartilhado.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable{ //Runnable permite ser executado em threads
    private Socket clientSocket; //Socket do servidor conectado ao cliente
    private static LinkedList<ClientHandler> clientHandlerList = new LinkedList<>(); //Cria uma lista com todos os sockets do clientes ativos, que sera utilizado para mandar as mensagens de broadcast 
    
    public long ultimaMensagem; //Tempo em segundos da ultima mensagem recebida do Cliente
    
    private Conta conta; //Jogador relacionado a esse cliente, em que podemos pegar seu username e senha
    private int idConta;
    private ArrayList<Conta> bancoDeContas; //Referencia para a lista de todos os jogadores

    private Map<Integer, Cobra> estadosGlobais; //Possui os status dos Pinguins na rede
    
    private ObjectOutputStream oos; //OOS eh onde iremos enviar nosso Output de Objetos para os clientes
    private ObjectInputStream ois; //OIS eh onde recebemmos o Input de Objetos dos clientes

    public static String[] cores = {"VERMELHO", "AZUL", "VERDE", "ROSA", "AMARELO", "ROXO"};
    
    public ClientHandler(Socket clientSocket, Map<Integer, Cobra> estadosGlobais, ArrayList<Conta> bancoDeContas){
        this.ultimaMensagem = System.currentTimeMillis()/1000;
        try {
            this.clientSocket = clientSocket; //Atribui ao ClientHandler o novo socket criado
            this.oos = new ObjectOutputStream(clientSocket.getOutputStream()); // Cria ObjectOutPutStream, "é oq que vai ser utilizado para mandar objetos ao cliente".
            oos.flush();
            this.ois = new ObjectInputStream(clientSocket.getInputStream()); //Cria OIS para ler objetos enviados pelo cliente
    
            // clientHandlerList.add(this); // Adiciona a lista de Threads o Socket funcional
            this.estadosGlobais = estadosGlobais; //Coloca uma referencia ao estados globais
            this.bancoDeContas = bancoDeContas;   //Coloca uma referencia banco de jogadores
        }catch (IOException e) {e.printStackTrace();}
        
    }
    // Interacoes entre Servidor e Cliente
    public boolean login() {
        try {
            MsgLogin mensagem = (MsgLogin) ois.readObject(); // Recebe mensagem Login (que possui tipo, username e senha)
            synchronized (bancoDeContas) {
                for (Conta j : bancoDeContas) {
                    if (j.getUsername().equalsIgnoreCase(mensagem.getUsername())) { //Procura um jogador em especifico, se nao acha pede para criar uma conta nova
                        if(j.getOnline()){
                            oos.writeObject(new MsgRespostaLogin("COBRA JA CONECTADO! FECHANDO CONEXAO...", false, 0)); //Senha diferente
                            oos.flush();
                            return false;
                        }
                        else if (j.compareSenha(mensagem.getSenha())) {
                            conta = bancoDeContas.get(bancoDeContas.indexOf(j));//Atribui ao Client Handler os valores do jogador
                            this.conta.setOnline(true);
                            this.idConta = bancoDeContas.indexOf(j); //Atribui o seu id ao Index
                            oos.writeObject(new MsgRespostaLogin("ITS SNAKE TIME...", true, idConta)); //Senha diferente
                            oos.flush();
                            return true; //Retorna True se encontrou o usuario e sua senha esta correta
                            //BROADCAST DE USUÁRIO LOGOU
                        }
                        oos.writeObject(new MsgRespostaLogin("SENHA INCORRETA! VOCE ESTA SENDO DESCONECTADO...", false, 0)); //Senha diferente
                        oos.flush();
                        return false;
                    }
                }
            
                conta = new Conta(mensagem.getUsername(), mensagem.getSenha());
                this.idConta = bancoDeContas.size(); //Define o Id do novo jogador com base em quantos jogadores existem
                bancoDeContas.add(conta);
            }
            oos.writeObject(new MsgRespostaLogin("CONTA CRIADA! ITS SNAKE TIME...", true, idConta)); //Senha diferente
            oos.flush();
            return true;
        } catch (Exception e) {
            System.out.println("Problema no login");
            return false;

            // closeGeral(clientSocket, oos, ois);
            //talvez criar um closeAlternativo mais simples
        }
    }

    public void removeClientHandler() {
        synchronized (clientHandlerList){
            clientHandlerList.remove(this);
        }
        //ACREDITO QUE PODEMOS MANDAR A MENSAGEM QUE ALGUM USUÁRIO SE DESCONECTOU AQUI PELO BROADCAST
    }

    public void closeAlternativo(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        try {  //Fecha de qualquer jeito
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
       
        
        if(this.conta != null && conta.getOnline()){ //Faz Broadcast se o penguin estava online (concluiu o login) e saiu do servidor
            synchronized (estadosGlobais) {
                Cobra cobraParaSalvar = estadosGlobais.get(idConta);
                if (cobraParaSalvar != null) {
                    conta.salvarPosicao(cobraParaSalvar.getXAlvo(), cobraParaSalvar.getYAlvo());
                    estadosGlobais.remove(idConta);
                }
                else {
                    System.out.println("Aviso: Cobra ID " + idConta + " já removido do estado global.");
                }
            }
            System.err.println(conta.getUsername() + " saiu do servidor");
            broadcast(new MsgFluxo(idConta, conta.getUsername(), "SAIDA"));
            broadcast(new MsgAtualizacaoEstado(estadosGlobais));
            conta.setOnline(false);
            
        }

        removeClientHandler();
        try {  //Fecha de qualquer jeito
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public void broadcast(MensagemBase mensagem) {
        List<ClientHandler> remover = new LinkedList<ClientHandler>();
        
        synchronized (clientHandlerList) {
            for (ClientHandler clientHandler : clientHandlerList) { //Para cado jogador enviar:
                if (!(clientHandler.idConta==this.idConta)) { //Não envia para o mesmo jogador
                try {
                        // System.out.println(clientHandler.idConta);

                        clientHandler.oos.reset();

                        clientHandler.oos.writeObject(mensagem);
                        clientHandler.oos.flush();
                } catch (Exception e) {
                    System.err.println("Cliente " + clientHandler.conta.getUsername() + " caiu. Fechando conexão.");
                    remover.add(clientHandler);
                }
                
                }
            }
        }
        for (ClientHandler handlerCaido : remover) {
            handlerCaido.closeGeral(handlerCaido.clientSocket, handlerCaido.oos, handlerCaido.ois);
        }
        
    }

    public void recebeMensagem(){
        
        while (!clientSocket.isClosed())
        {
            try{
                
                MensagemBase mensagem = (MensagemBase) ois.readObject(); // Recebe mensagem generica 
                this.ultimaMensagem = System.currentTimeMillis()/1000;

                if(mensagem instanceof MsgPosicaoAlvo){
                    MsgPosicaoAlvo msgAlvo = (MsgPosicaoAlvo) mensagem; //Transforma a mensagem recebida no tipo PosicaoAlvo
                    synchronized (estadosGlobais){
                        Ponto2D posicaoAlvo = msgAlvo.getAlvo(); //Pega as coordenadas do Alvo(clique) do cliente
                        Cobra cobraAlvo = estadosGlobais.get(idConta); //Pega do Map com todas as cobras, a cobra que enviou a mensagem
                        if (cobraAlvo == null) {
                            System.err.println("Erro: Cobra do ID " + idConta + " não encontrado no Map Global.");
                        }
                        cobraAlvo.setXAlvo(posicaoAlvo.getX());
                        cobraAlvo.setYAlvo(posicaoAlvo.getY()); //Atualiza as coordenadas de alvo dele
                        estadosGlobais.put(idConta, cobraAlvo); //Insere a cobra com as novas coordenadas alvo no Map
                        System.out.println(cobraAlvo.getId() + "-" + conta.getUsername() + " andou para: " + cobraAlvo.getXAlvo() + "x" + cobraAlvo.getYAlvo());
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia o novo Map com as coordenadas alvo alteradas para que os Clientes movimentem os pinguins ate essas coordenadas
                    }
                }

                else if (mensagem instanceof MsgDanca) {
                    MsgDanca msgDanca = (MsgDanca) mensagem; //Transforma a mensagem recebida no tipo Danca
                    synchronized(estadosGlobais) {
                        Cobra cobraDanca = estadosGlobais.get(idConta); //Pega do Map com todas as cobras, a cobra que enviou a mensagem
                        cobraDanca.dancar(); //Atualiza o estado de Danca da cobra
                        estadosGlobais.put(idConta, cobraDanca); //Atualiza no Map a cobra com o estado alterado
                        if(cobraDanca.getDancando()) System.out.println(cobraDanca.getId() + "-" + conta.getUsername() + " dançou.");
                        else System.out.println(cobraDanca.getId() + "-" + conta.getUsername() + " parou de dançar.");
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia para todos os outros Clientes o Map com os estados de Danca atualizados 
                    }
                }
                
                else if (mensagem instanceof MsgChat) {
                    System.out.println("Recebi mensagem de chat!");
                    MsgChat chat = (MsgChat) mensagem; //Recebe Mensagem de texto do cliente
                    String texto = chat.getTexto();
                    System.out.println(texto);
                    broadcast(new MsgChat(texto, conta.getUsername()));
                }

                else if(mensagem instanceof MsgFluxo) {
                    MsgFluxo msgFluxo = (MsgFluxo) mensagem;
                    if(msgFluxo.getTipo().equals("SAIDA")) {
                        System.out.println("msg saida");        
                        closeGeral(clientSocket, oos, ois);
                    }
                }
            
            }catch (Exception e) {
                System.out.println("Problema no recebe");
                closeGeral(clientSocket, oos, ois);
                
            }
        }
            
    }

    public void keepAlive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!clientSocket.isClosed()) {
                    try {
                        if((System.currentTimeMillis()/1000) - ultimaMensagem > 30.00){
                            System.out.println("keepalive morreu");
                            closeGeral(clientSocket, oos, ois);
                        }
                        Thread.sleep(1000);
                    } catch(Exception e) {
                        break;
                    }
                }
            }
        }).start();
    }

    //Run eh a Main para Threads
    public void run(){
        if (login()) {
            System.out.println(idConta + "-" + conta.getUsername() + " Fez login no servidor");
            
            synchronized (clientHandlerList) {
                clientHandlerList.add(this); // Adiciona a lista de Threads o Socket funcional
            }

            
            synchronized (estadosGlobais) {
                Cobra cobra = new Cobra(idConta, conta.getUsername(), conta.getXSalvo(), conta.getYSalvo(), false, cores[idConta%6]); //Cria nova cobra com o id gerado
                estadosGlobais.put(idConta, cobra); //Adiciona no Map a cobra criada
            }

            try{
                oos.writeObject(new MsgAtualizacaoEstado(estadosGlobais));
                oos.flush();

            }catch(Exception e){
                System.out.println("não conseguiu mandar msg inicial");
                closeGeral(clientSocket, oos, ois);
            }
            
            broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia para todos os jogadores o Map atual com a nova cobra adicionada
            broadcast(new MsgFluxo(idConta, conta.getUsername(), "ENTRADA"));

            keepAlive();
            recebeMensagem(); //Comeca a receber as mensagens do Cliente
        }
        else {
            System.out.println("Tentativa de login inválida.");
            closeAlternativo(clientSocket, oos, ois); //Faz login, caso nao consiga fecha conexao para uma nova tentativa de login
        }
    }
}