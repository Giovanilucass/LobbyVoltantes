package cliente;
import java.io.*;
import java.net.*;
import java.util.*;
import compartilhado.mensagens.*;
import compartilhado.*;

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

    private ChatCliente chatCliente; //Responsável por fazer a comunicação do chat

    private Map<Integer, PinguimRender> estadosGlobaisCliente; //Pega os estados do penguins online

    private long ultimaMensagem; //tempo em segundos da ultima mensagem enviada ao Servidor
    
    public Cliente(Socket socket) {
        this.socket = socket;
        this.ultimaMensagem = System.currentTimeMillis()/1000;
        try{
            this.ois = new ObjectInputStream(socket.getInputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
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

    // public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) { //
    //     try {
    //         System.out.println("Saindo do servidor...");
    //         if(oos != null) oos.close();
    //         if(ois != null) ois.close();
    //         if(socket != null) socket.close();
    //     } catch(IOException e) {
    //         e.printStackTrace();
    //     }
    // }

    public void keepAlive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    //KEEP ALIVE
                    // Tempo atual - Tempo da última mensagem > 15: (Manda Keep Alive caso tenha passado 15 segundos sem mensagem)
                    if(((System.currentTimeMillis()/1000) - ultimaMensagem)>15.00){
                        try{
                            oos.writeObject(new MensagemBase("KEEP_ALIVE"));
                            oos.flush();
                            setUltimaMensagem(System.currentTimeMillis()/1000);
                        }catch(Exception e){
                            System.exit(0);
                        }
                    }
                }
            }
        }).start();
    }

    public static void main(String args[]){
        try{
            Socket socket = new Socket("127.0.0.1", 9775);
            Cliente cliente = new Cliente(socket);
            if(cliente.login()){
                cliente.chatCliente = new ChatCliente(cliente.oos, cliente.username); //Responsável pelo gerenciamento do chat via terminal
                cliente.estadosGlobaisCliente = new HashMap<Integer, PinguimRender>(); //Responsável pela renderização dos penguins
                cliente.gerenciadorDeEstados = new GerenciadorDeEstados(cliente.estadosGlobaisCliente);

                
                LeitorDoCliente leitorDoCliente = new LeitorDoCliente(cliente.ois, cliente.gerenciadorDeEstados, cliente.chatCliente);
                Thread threadLeitor = new Thread(leitorDoCliente);
                threadLeitor.start(); //Starta Thread da Leitura
                
                cliente.keepAlive();

                while(true){
                    try{
                        cliente.chatCliente.enviaChat(); // Sempre tenta mandar mensagem
                    }catch(Exception e){
                        System.exit(0);
                    }
                }

            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}