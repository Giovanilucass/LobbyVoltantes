package server;
import compartilhado.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Servidor{

    private ServerSocket serverSocket; 
    private Map<Integer, Pinguim> estadosGlobais; // Associa o ID com penguim

    private ArrayList<Conta> bancoDeContas; //Possui informacoes does players

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket; //Welcome Socket
        this.estadosGlobais = new ConcurrentHashMap<>(); //Cria as estruturas
        this.bancoDeContas = new ArrayList<>(); 
    }

    public void abreServidor() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); //Cria seu proprio socket quando recebe um socket de cliente, estabelecendo a conexao TCP
                System.out.println("Alguém tentando conexão");
                ClientHandler clientHandler = new ClientHandler(socket, estadosGlobais, bancoDeContas); //Cria ClientHandler
                Thread thread = new Thread(clientHandler); //Joga ele em uma thread
                thread.start(); //Inicia o ClientHandler
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) try {serverSocket.close();} catch(IOException e){e.printStackTrace();}        }
    }

    public void fechaServidor() {
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) throws IOException{ //Duvida: Pq a main tem thorws exception? em teoria n era pra ela lidar com o erro do abre servidor?
        Scanner sc = new Scanner(System.in);
        System.out.print("Informe a porta: ");
        int port = sc.nextInt();
        sc.close();
        ServerSocket serverSocketInit = new ServerSocket(port); //Cria Socket em que nosso servidor aceita conexoes nessa Porta
        Servidor servidor = new Servidor(serverSocketInit); //Cria Servidor com seu proprio Socket
        servidor.abreServidor(); //Espera chegar Sockets de clientes  
    }
    
}