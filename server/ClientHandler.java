package server;
import compartilhado.mensagens.*;
import compartilhado.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable{ //Runnable permite ser executado em threads
    private Socket clientSocket; //Socket do servidor conectado ao cliente
    private static LinkedList<ClientHandler> clientHandlerList = new LinkedList<>(); //Cria uma lista com todos os sockets do clientes ativos, que sera utilizado para mandar as mensagens de broadcast 
    
    
    private Conta conta; //Jogador relacionado a esse cliente, em que podemos pegar seu username e senha
    private int idConta;
    private ArrayList<Conta> bancoDeContas; //Referencia para a lista de todos os jogadores

    private Map<Integer, Pinguim> estadosGlobais; //Possui os status dos Pinguins na rede
    
    private ObjectOutputStream oos; //OOS eh onde iremos enviar nosso Output de Objetos para os clientes
    private ObjectInputStream ois; //OIS eh onde recebemmos o Input de Objetos dos clientes

    public static String[] cores = {"VERMELHO", "AZUL", "VERDE", "ROSA", "AMARELO", "ROXO"};
    
    public ClientHandler(Socket clientSocket, Map<Integer, Pinguim> estadosGlobais, ArrayList<Conta> bancoDeContas){
        try {
            this.clientSocket = clientSocket; //Atribui ao ClientHandler o novo socket criado
            this.oos = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream())); // Cria ObjectOutPutStream, "é oq que vai ser utilizado para mandar objetos ao cliente".
            oos.flush();
            this.ois = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream())); //Cria OIS para ler objetos enviados pelo cliente
    
            clientHandlerList.add(this); // Adiciona a lista de Threads o Socket funcional
            this.estadosGlobais = estadosGlobais; //Coloca uma referencia ao estados globais
            this.bancoDeContas = bancoDeContas;   //Coloca uma referencia banco de jogadores
        }catch (IOException e) {e.printStackTrace();}
        
    }
    // Interacoes entre Servidor e Cliente
    public boolean login() {
        try {
            MsgLogin mensagem = (MsgLogin) ois.readObject(); // Recebe mensagem Login (que possui tipo, username e senha)
            for (Conta j : bancoDeContas) {
                if (j.getUsername().equalsIgnoreCase(mensagem.getUsername())) { //Procura um jogador em especifico, se nao acha pede para criar uma conta nova
                    if(j.getOnline()){
                        oos.writeObject(new MsgRespostaLogin("PINGUIM JA CONECTADO! FECHANDO CONEXAO...", false)); //Senha diferente
                        oos.flush();
                        return false;
                    }
                    else if (j.compareSenha(mensagem.getSenha())) {
                        conta = bancoDeContas.get(bancoDeContas.indexOf(j));//Atribui ao Client Handler os valores do jogador
                        this.conta.setOnline(true);
                        this.idConta = bancoDeContas.indexOf(j); //Atribui o seu id ao Index
                        oos.writeObject(new MsgRespostaLogin("ITS PENGUIN TIME...", true)); //Senha diferente
                        oos.flush();
                        return true; //Retorna True se encontrou o usuario e sua senha esta correta
                        //BROADCAST DE USUÁRIO LOGOU
                    }
                    oos.writeObject(new MsgRespostaLogin("SENHA INCORRETA! VOCE ESTA SENDO DESCONECTADO...", false)); //Senha diferente
                    oos.flush();
                    return false;
                }
            }
            conta = new Conta(mensagem.getUsername(), mensagem.getSenha());
            this.idConta = bancoDeContas.size(); //Define o Id do novo jogador com base em quantos jogadores existem
            bancoDeContas.add(conta);
            oos.writeObject(new MsgRespostaLogin("CONTA CRIADA! ITS PENGUIN TIME...", true)); //Senha diferente
            oos.flush();
            return true;
        } catch (Exception e) {
        }
        // Scanner scanner = new Scanner(System.in); //Solicita o nome do usuario
        return true;
    }

    public void removeClientHandler() {
        clientHandlerList.remove(this);
        //ACREDITO QUE PODEMOS MANDAR A MENSAGEM QUE ALGUM USUÁRIO SE DESCONECTOU AQUI PELO BROADCAST
    }

    public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
       
        removeClientHandler();
        if(this.conta != null && conta.getOnline()){
            estadosGlobais.remove(idConta);
            broadcast(new MsgAtualizacaoEstado(estadosGlobais));
            System.err.println(conta.getUsername() + " saiu do servidor");
            broadcast(new MsgChat(conta.getUsername() + " SAIU DO SERVIDOR.", "Server"));
            conta.setOnline(false);
        }
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(MensagemBase mensagem) {
        for (ClientHandler clientHandler : clientHandlerList) { //Para cado jogador enviar:
            try {
                if (!(clientHandler.idConta==this.idConta)) { //Não envia para o mesmo jogador
                    // System.out.println(clientHandler.idConta);
                    clientHandler.oos.writeObject(mensagem);
                    clientHandler.oos.flush();
                }
            } catch (IOException e) {
                closeGeral(clientSocket, oos, ois);
            }
        }
        
    }

    public void recebeMensagem(){
        
        while (!clientSocket.isClosed())
        {
            try{
                MensagemBase mensagem = (MensagemBase) ois.readObject(); // Recebe mensagem generica 
                switch(mensagem.getTipo()){
                    case "ALVO":
                        MsgPosicaoAlvo msgAlvo = (MsgPosicaoAlvo) mensagem; //Transforma a mensagem recebida no tipo PosicaoAlvo
                        Ponto2D posicaoAlvo = msgAlvo.getAlvo(); //Pega as coordenadas do Alvo(clique) do cliente
                        Pinguim pinguimAlvo = estadosGlobais.get(idConta); //Pega do Map com todos os pinguins, o pinguim que enviou a mensagem
                        pinguimAlvo.setXAlvo(posicaoAlvo.getX());
                        pinguimAlvo.setYAlvo(posicaoAlvo.getY()); //Atualiza as coordenadas de alvo dele
                        estadosGlobais.put(idConta, pinguimAlvo); //Insere o pinguim com as novas coordenadas alvo no Map
                        System.out.println(pinguimAlvo.getId() + "-" + conta.getUsername() + " andou para: " + pinguimAlvo.getXAlvo() + "x" + pinguimAlvo.getYAlvo());
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia o novo Map com as coordenadas alvo alteradas para que os Clientes movimentem os pinguins ate essas coordenadas
                        break;
                    case "DANCA":
                        MsgDanca msgDanca = (MsgDanca) mensagem; //Transforma a mensagem recebida no tipo Danca
                        Pinguim pinguimDanca = estadosGlobais.get(idConta); //Pega do Map com todos os pinguins, o pinguim que enviou a mensagem
                        pinguimDanca.setDancando(); //Atualiza o estado de Danca do pinguim
                        estadosGlobais.put(idConta, pinguimDanca); //Atualiza no Map o pinguim com o estado alterado
                        System.out.println(pinguimDanca.getId() + "-" + conta.getUsername() + " dancou.");
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia para todos os outros Clientes o Map com os estados de Danca atualizados 
                        break;
                    case "CHAT":
                        System.out.println("Recebi mensagem de chat!");
                        MsgChat chat = (MsgChat) mensagem; //Recebe Mensagem de texto do cliente
                        String texto = chat.getTexto();
                        System.out.println(texto);
                        broadcast(new MsgChat(texto, conta.getUsername()));
                        break;
                    case "SAIDA":
                        closeGeral(clientSocket, oos, ois);
                        break;
                    default:
                        break;  
                }
            }catch (Exception e) {
            }
        }
            
    }

    //Run eh a Main para Threads
    public void run(){
        if (login()) {
            System.out.println(idConta + "-" + conta.getUsername() + " Fez login no servidor");
            Pinguim pinguim = new Pinguim(idConta, 0, 0, 0, 0, false, cores[idConta]); //Cria novo pinguim com o id gerado
            estadosGlobais.put(idConta, pinguim); //Adiciona no Map o pinguim criado

            try{
                oos.writeObject(new MsgAtualizacaoEstado(estadosGlobais));
                oos.flush();

            }catch(Exception e){
                closeGeral(clientSocket, oos, ois);
            }
            
            broadcast(new MsgAtualizacaoEstado(estadosGlobais)); //Envia para todos os jogadores o Map atual com o novo pinguim adicionado
            recebeMensagem(); //Comeca a receber as mensagens do Cliente
        }
        else {
            System.out.println("Tentativa de login inválida.");
            closeGeral(clientSocket, oos, ois); //Faz login, caso nao consiga fecha conexao para uma nova tentativa de login
        }
    }
}