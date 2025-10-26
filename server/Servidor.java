package server;
import compartilhado.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor{

    private ServerSocket serverSocket;
    private Map<Integer, Pinguim> estadosGlobais;

    private ArrayList<Jogador> bancoDeJogadores;

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.estadosGlobais = new HashMap<>();
        this.bancoDeJogadores = new ArrayList<>();
    }

    public void abreServidor() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("FOIIIIIIIIIIIIIIIIII");
                ClientHandler clientHandler = new ClientHandler(socket, estadosGlobais, bancoDeJogadores); //Cria ClientHandler
                Thread thread = new Thread(clientHandler); //Joga ele em uma thread
                thread.start();
            }
            
        } catch (IOException e) {e.printStackTrace();}
    }

    public void fechaServidor() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) throws IOException{
        
        ServerSocket serverSocketInit = new ServerSocket(9775);
        Servidor servidor = new Servidor(serverSocketInit);
        servidor.abreServidor();

    //     try{
    //         ServerSocket serverSocket = new ServerSocket(12345); //Cria Welcome socket
    //         Socket socket = serverSocket.accept();//Welcome socket espera receber socket de cliente.
            
    //         ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream())); //Cria Buffer capaz de ler objetos enviados pelo socket do cliente
    //         MensagemBase mensagem = (MensagemBase) ois.readObject(); //Realizamos um TypeCast para transformar o objeto recebido pelo buffer  em uma MensagemBase
            
    //     } catch (IOException | ClassNotFoundException e) {
    //         e.printStackTrace();
    //     }
    }
    
}