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

    private PainelJogo interfaceJogo;

    private String username;

    private ChatCliente chatCliente;

    private Map<Integer, Pinguim> estadosGlobais;
    
    public Cliente(Socket socket) {
        this.socket = socket;
        try{
            this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            oos.flush();
        }catch(IOException e) { 
        }
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
            
        }catch(Exception e) {
            System.exit(0);
            return false;
        }
    }

    public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            System.out.println("Saindo do servidor...");
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        try{
            Socket socket = new Socket("127.0.0.1", 9775);
            Cliente cliente = new Cliente(socket);
             //A gente tem algum tratamento se o login do usuário n for valido? Tipo tentar reconectar denovo
            if(cliente.login()){
                cliente.interfaceJogo = new PainelJogo();
                cliente.chatCliente = new ChatCliente(cliente.oos, cliente.username);
                LeitorDoCliente leitorDoCliente = new LeitorDoCliente(cliente.socket, cliente.ois, cliente.interfaceJogo, cliente.chatCliente);
                Thread threadLeitor = new Thread(leitorDoCliente);
                threadLeitor.start(); //Starta Thread da Leitura
                while(true){
                    try{
                        cliente.chatCliente.enviaChat();
                    }catch(Exception e){
                        System.exit(0);//Fecha socket
                    }
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}