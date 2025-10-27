//USADO PARA MANDAR MENSAGENS
package cliente; 

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import compartilhado.mensagens.*;
import compartilhado.*;

public class MensageiroCliente {
    private ObjectOutputStream oos;
    private String username;

   public MensageiroCliente(ObjectOutputStream oos, String username) {
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



    public void enviaDanca() throws Exception{
        MsgDanca msgDanca = new MsgDanca();
        oos.writeObject(msgDanca);
        oos.flush();
    }

    public void enviaSair() throws Exception{
        MsgFluxo msgSaida = new MsgFluxo(0, username, "SAIDA");
        oos.writeObject(msgSaida);
        oos.flush();
    }

    public void enviaPosicao(int posX, int posY) throws Exception{
        MsgPosicaoAlvo msgPosicaoAlvo = new MsgPosicaoAlvo(new Ponto2D(posX, posY));
        oos.writeObject(msgPosicaoAlvo);
        oos.flush(); // Envia a mensagem do cliente para o servidor
        // System.out.println("Mover para " + posX + "x" + posY);
    }



    public void enviaChat(String texto) throws Exception//Envia mensagem Requisao
    {
        MsgChat msgChat = new MsgChat(texto, this.username); // Caso não tenha $ é apenas uma mensagem de etxto
        oos.writeObject(msgChat);
        oos.flush(); // Envia a mensagem do cliente para o servidor
        //}
    }

}
