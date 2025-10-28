package cliente;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import compartilhado.mensagens.*;


//Primeiramente 
//Assim que o Cliente conectar havera a requisicao de Login
//Entao imediatamente ele tera que enviar o seu nome (com um print)

//Depois sera pedido a criacao/ insercao da senha baseado se ele logou no server.

public class Cliente{
    private Socket socket; //Socket do servidor relacionado ao Cliente
    
    private ObjectOutputStream oos; //OOS eh onde iremos enviar nosso Output de Objetos para os clientes
    private ObjectInputStream ois; //OIS eh onde recebemmos o Input de Objetos dos clientes

    private GerenciadorDeEstados gerenciadorDeEstados; //VAI SER RESPONSAVEL POR DESENHAR A INTERFACE (e possivelmente atualizar atributos)

    private String username;

    private MensageiroCliente mensageiroCliente; //Responsável por fazer a comunicação do chat

    private Map<Integer, CobraRender> estadosGlobaisCliente; //Pega os estados das cobras online

    private long ultimaMensagem; //tempo em segundos da ultima mensagem enviada ao Servidor

    private int idCliente;
    
    public Cliente(Socket socket) {
        this.socket = socket;
        this.ultimaMensagem = System.currentTimeMillis()/1000;
        try{
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            this.ois = new ObjectInputStream(socket.getInputStream());
           
        }catch(IOException e) { 
            throw new RuntimeException("Falha na inicialização do stream", e);
        }
    }

    public void setUltimaMensagem(long ultimaMensagem){
        this.ultimaMensagem = ultimaMensagem;
    }
    
    public boolean login() {
        try{
            //Recebemos informacoes de login do usuario
            Scanner scanner = new Scanner(System.in);
            System.out.print("Nome de usuario: ");
            this.username = scanner.nextLine(); //Atribui nome ao usuário
            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            
            MsgLogin msgLogin = new MsgLogin(username, senha); //Prepara a mensagem (que nesse caso eh sempre do tipo Login)
            oos.writeObject(msgLogin);
            oos.flush(); //Manda mensagem do login

            //Recebe Resposta do ClientHandler em relacao ao login
            MsgRespostaLogin resposta = (MsgRespostaLogin) ois.readObject();
            if (resposta.getLogin()) { //Se o login foi aprovado
                System.out.println(resposta.getMensagem());
                idCliente = resposta.getId();
                return true;
            }
            else{
                System.out.println(resposta.getMensagem());
                return false;
            }
            
        }catch(IOException e) {
            System.out.println("Problemas com Socket...");
            return false;
        }catch(ClassNotFoundException e) {
            System.out.println("Mensagem não pode ser interpretada... ClassNotFound");
            return false;
        }
    }

    public void keepAlive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    //KEEP ALIVE
                    // Tempo atual - Tempo da última mensagem > 15: (Manda Keep Alive caso tenha passado 15 segundos sem mensagem)
                    try {
                        if(((System.currentTimeMillis()/1000) - ultimaMensagem)>15.00){
                            oos.writeObject(new MensagemBase());
                            oos.flush();
                            setUltimaMensagem(System.currentTimeMillis()/1000);
                        }
                        Thread.sleep(1000); 
                    } catch (Exception e) {
                        break;
                    }   
                }   
                System.out.println("KeepAlive: Conexão perdida. Encerrando cliente.");
                System.exit(0); // Força o encerramento se a conexão cair
            }
        }).start();
    }

    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        try{
            System.out.print("Informe o IP do Servidor: ");
            String IP = sc.nextLine();
            System.out.print("Informe a porta do Servidor: ");
            int port = sc.nextInt();

            Socket socket = new Socket(IP, port);
            Cliente cliente = new Cliente(socket);
            if(cliente.login()){
                cliente.mensageiroCliente = new MensageiroCliente(cliente.oos, cliente.username); //Responsável pelo gerenciamento do chat via terminal
                cliente.estadosGlobaisCliente = new ConcurrentHashMap<Integer, CobraRender>(); //Responsável pela renderização das cobras
                cliente.gerenciadorDeEstados = new GerenciadorDeEstados(cliente.estadosGlobaisCliente, cliente.idCliente);
                
                LeitorDoCliente leitorDoCliente = new LeitorDoCliente(cliente.ois, cliente.gerenciadorDeEstados, cliente.mensageiroCliente);
                Thread threadLeitor = new Thread(leitorDoCliente);
                threadLeitor.start(); //Starta Thread da Leitura
                
                Jogo.iniciaJogo(cliente.idCliente, cliente.estadosGlobaisCliente, cliente.mensageiroCliente);

                cliente.keepAlive();

            }
        }catch(IOException e) {
            System.out.println("Problema na main");
            e.printStackTrace();
        }
    }
    
}