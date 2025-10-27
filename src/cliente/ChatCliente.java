//USADO PARA MANDAR MENSAGENS
package cliente; 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import compartilhado.mensagens.*;
import compartilhado.*;

public class ChatCliente {
    private ObjectOutputStream oos;
    private String username;

   public ChatCliente(ObjectOutputStream oos, String username) {
        this.oos = oos;
        this.username = username;
    }

    public void chatServer(String texto) {
        System.out.println("[SERVER] " + texto);
    }
    
    public void chatClientes(MsgChat msg) {
        // Vai precisar de mais depois
        System.out.println(msg.getUsername() + ": " + msg.getTexto());
    }

    public void enviaChat() throws Exception//Envia mensagem Requisao
    {
        
        Scanner scanner = new Scanner(System.in);
        String texto = scanner.nextLine(); //Pega a mensagem do cliente
        if(texto.charAt(0)=='$'){
            switch(texto.charAt(1)){
                case 'a': //Andar
                    Scanner sc = new Scanner(texto.substring(2));
                    int x = sc.nextInt(); //Transforma em inteiros as posições passadas
                    int y = sc.nextInt(); 
                    MsgPosicaoAlvo msgPosicaoAlvo = new MsgPosicaoAlvo(new Ponto2D(x, y));
                    oos.writeObject(msgPosicaoAlvo);
                    oos.flush(); // Envia a mensagem do cliente para o servidor
                    System.out.println("Mover para " + x + "x" + y);
                    break;
                case 'd': //Dancar
                    System.out.println("DANCA MALUCAAAAA");
                    MsgDanca msgDanca = new MsgDanca();
                    oos.writeObject(msgDanca);
                    oos.flush();
                    break;
                case 'q': //Quittar
                    System.out.println("Saindo do server...");
                    MsgFluxo msgSaida = new MsgFluxo(0, username, "SAIDA");
                    oos.writeObject(msgSaida);
                    oos.flush();
                    throw new Exception();

                default:
                    System.out.println("Comando Invalido.");
                    break;
            }
        }else{
            MsgChat msgChat = new MsgChat(texto, this.username); // Caso não tenha $ é apenas uma mensagem de etxto
            oos.writeObject(msgChat);
            oos.flush(); // Envia a mensagem do cliente para o servidor
        }
    }

}
