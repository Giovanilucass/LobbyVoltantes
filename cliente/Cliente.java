package cliente;
import java.io.*;
import java.net.*;
import java.util.*;
import compartilhado.mensagens.*;

//Primeiramente 
//Assim que o Cliente conectar haverá a requisição de Login
//Então imediatamente ele terá que enviar o seu nome (com um print)

//Depois será pedido a criação/ inserção da senha baseado se ele logou no server.

public class Cliente{
    private Socket socket; //Socket do servidor relacionado ao Cliente
    
    private ObjectOutputStream oos; //OOS eh onde iremos enviar nosso Output de Objetos para os clientes
    private ObjectInputStream ois; //OIS eh onde recebemmos o Input de Objetos dos clientes
    
    public Cliente(Socket socket) {
        this.socket = socket;
        try{
            this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            this.oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            oos.flush();
        }catch(IOException e) {
            closeGeral(socket, oos, ois);
        }
    }
    
    public void login() {
        try{
            //Recebemos informações de login do usuário
            Scanner scanner = new Scanner(System.in);
            System.out.println("Nome de usuário: ");
            String username = scanner.nextLine();
            System.out.println("Senha: ");
            String senha = scanner.nextLine();
            
            MsgLogin msgLogin = new MsgLogin(username, senha); //Prepara a mensagem
            oos.writeObject(msgLogin);
            oos.flush(); //Manda mensagem do login

            MensagemBase resposta = (MensagemBase) ois.readObject();
            System.out.println(resposta.getTipo());

        }catch(IOException | ClassNotFoundException e) {
            closeGeral(socket, oos, ois);
        }
    }
    
    public void Enviachat() //Envia mensagem Requisão
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Chat: ");
            String texto = scanner.nextLine(); //Pega a mensagem do cliente

            MsgChat msgChat = new MsgChat(texto);
            oos.writeObject(msgChat);
            oos.flush(); // Envia a mensagem do cliente para o servidor
        }
        catch(IOException e) 
        {
            closeGeral(socket, oos, ois);
        }
    }

    // public void sendMessage(String Texto)
    // {
    //     try
    //     {
    //         bufferedWriter.write(Texto);
    //         bufferedWriter.newLine();
    //         bufferedWriter.flush();
            
    //     }catch(IOException e) {
    //         closeGeral(socket, oos, ois);
    //     }
    // }

    public void closeGeral(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            if(oos != null) oos.close();
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void esperaEstados() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MensagemBase mensagem

            }
        }
        )
    }

    public void esperaChat() { //Deixa função em Thread
        new Thread(new Runnable() {
            @Override
            public void run() { //roda conteudo
                MensagemBase mensagemDaSala; //Me
                while (socket.isConnected()) {
                    try {
                        mensagemDaSala = (MensagemBase) ois.readObject(); // Lê a mensgem recebida
                        if(mensagemDaSala.getTipo().equals("CHAT")){ //Confere se a mensagem recebida é do tipo CHAT
                            MsgChat msgChat = (MsgChat) mensagemDaSala; //Transforma em tipo CHAT
                            System.out.println(msgChat.getTexto()); // Printa conteudo enviado pela SALA
                        }
                        
                    } catch(Exception e) {
                        closeGeral(socket, oos, ois);
                    }
                }
            }
        }).start();
    }

    public static void main(String args[]){
        try{
            Socket socket = new Socket("127.0.0.1", 9775);
            Cliente cliente = new Cliente(socket);
            cliente.login();
        
        //Parte do Chat
        while(socket.isConnected())
        {
            cliente.chat();
            
        }
        cliente.esperaChat();

        }catch(IOException e) {
            e.printStackTrace();
        }
    
    }
    
}