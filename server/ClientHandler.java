package server;
import compartilhado.mensagens.*;
import compartilhado.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable{
    private Socket clientSocket; //Socket do servidor relacionado ao Cliente
    private static LinkedList<ClientHandler> clientHandlerList = new LinkedList<>(); //Cria uma lista com todos os sockets do clientes ativos 
    
    private Jogador jogador;
    private int idJogador;
    private ArrayList<Jogador> bancoDeJogadores; //Referencia para a lista de todos os jogadores

    private Map<Integer, Pinguim> estadosGlobais; //Possui os status dos Pinguins na rede
    
    private ObjectOutputStream oos; //OOS eh onde iremos enviar nosso Output de Objetos para os clientes
    private ObjectInputStream ois; //OIS eh onde recebemmos o Input de Objetos dos clientes

    public static String[] cores = {"VERMELHO", "AZUL", "VERDE", "ROSA", "AMARELO", "ROXO"};
    
    public ClientHandler(Socket clientSocket, Map<Integer, Pinguim> estadosGlobais, ArrayList<Jogador> bancoDeJogadores){
        try {
            this.clientSocket = clientSocket;
            this.oos = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream())); // Cria ObjectOutPutStream, "é oq que vai ser utilizado para mandar objetos ao cliente".
            oos.flush();
            this.ois = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream())); //Cria OIS para ler objetos enviados pelo cliente
    
            clientHandlerList.add(this); // Adiciona a lista de Threads o Socket funcional
            this.estadosGlobais = estadosGlobais;
            this.bancoDeJogadores = bancoDeJogadores;
        }catch (IOException e) {e.printStackTrace();}
        
    }
    // Interacoes entre Servidor e Cliente
    public boolean login() {
        try {
            MsgLogin mensagem = (MsgLogin) ois.readObject(); // Recebe mensagem Login (que possui tipo, username e senha)
            for (Jogador j : bancoDeJogadores) {
                if (j.getUsername().equalsIgnoreCase(mensagem.getUsername())) { //Procura um jogador em especifico, se não acha pede para criar uma conta nova
                    if (j.compareSenha(mensagem.getSenha())) {
                        jogador = bancoDeJogadores.get(bancoDeJogadores.indexOf(j));//Atribui ao Client Handler os valores do jogador
                        oos.writeObject(new MensagemBase("ENTRANDO NO JOGO..."));
                        oos.flush();
                        return true; //Retorna True se encontrou o usuário e sua senha está correta
                    }
                    oos.writeObject(new MensagemBase("SENHA INCORRETA! VOCÊ ESTÁ SENDO DESCONECTADO..."));
                    oos.flush();
                    return false;
                }
            }
            jogador = new Jogador(mensagem.getUsername(), mensagem.getSenha());
            bancoDeJogadores.add(jogador);
            oos.writeObject(new MensagemBase("CONTA CRIADA COM SUCESSO! ENTRANDO NO JOGO..."));
            oos.flush();
            return true;
        } catch (Exception e) {
        }
        // Scanner scanner = new Scanner(System.in); //Solicita o nome do usuario
        return true;
    }

    public void removeClientHandler() {
        clientHandlerList.remove(this);
    }

    public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        removeClientHandler();
        bancoDeJogadores.remove(jogador);
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
                if (!clientHandler.jogador.getUsername().equalsIgnoreCase(jogador.getUsername())) { //Não envia para o mesmo jogador
                    oos.writeObject(mensagem);
                    oos.flush();
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
                MensagemBase mensagem = (MensagemBase) ois.readObject(); // Recebe mensagem genérica 
                switch(mensagem.getTipo()){
                    case "ALVO":
                        MsgPosicaoAlvo msgAlvo = (MsgPosicaoAlvo) mensagem;
                        Ponto2D posicaoAlvo = msgAlvo.getAlvo();
                        int idAlvo = msgAlvo.getID();
                        Pinguim pinguimAlvo = estadosGlobais.get(idAlvo);
                        pinguimAlvo.setXAlvo(posicaoAlvo.getX());
                        pinguimAlvo.setYAlvo(posicaoAlvo.getY());
                        estadosGlobais.put(idAlvo, pinguimAlvo);
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais));
                        break;
                    case "DANCA":
                        MsgDanca msgDanca = (MsgDanca) mensagem;
                        int idDanca = msgDanca.getID();
                        Pinguim pinguimDanca = estadosGlobais.get(idDanca);
                        pinguimDanca.setDancando();
                        estadosGlobais.put(idDanca, pinguimDanca);
                        broadcast(new MsgAtualizacaoEstado(estadosGlobais));
                        break;
                    case "CHAT":
                        MsgChat chat = (MsgChat) mensagem; //Recebe Mensagem de texto do cliente
                        String texto = chat.getTexto();
                        broadcast(chat);
                        //oos.writeObject(new MensagemBase(jogador.getUsername()+":"+ texto)); 
                        //oos.flush();
                    
                    
                        
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
        if (!login()) closeGeral(clientSocket, oos, ois);
        idJogador = estadosGlobais.size();
        Pinguim pinguim = new Pinguim(idJogador, 0, 0, 0, 0, false, cores[idJogador]);
        estadosGlobais.put(idJogador, pinguim);
        MsgAtualizacaoEstado msgAtualizacaoEstado = new MsgAtualizacaoEstado(estadosGlobais);
        broadcast(msgAtualizacaoEstado);
        recebeMensagem();
    }
}
    
