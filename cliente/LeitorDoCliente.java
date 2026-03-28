package cliente;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.SwingUtilities;

import compartilhado.*;
import compartilhado.mensagens.*;

public class LeitorDoCliente implements Runnable{
    private Socket socket;
    private ObjectInputStream ois;
    private final PainelJogo pipeInterface;
    private final ChatCliente pipeChat; //Comunicação entre leitor de cliente echat

    public LeitorDoCliente(Socket socket, ObjectInputStream ois, PainelJogo Interface, ChatCliente chat) {
        this.socket = socket;
        this.ois = ois;
        this.pipeInterface = Interface;
        this.pipeChat = chat;

    }

    public void closeGeral(Socket socket, ObjectInputStream ois) {
        try {
            if(ois != null) ois.close();
            if(socket != null) socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        MensagemBase mensagem; //Me
        while (true) {
            try {
                mensagem = (MensagemBase) ois.readObject(); // Le a mensgem recebida
                
                if (mensagem instanceof MsgAtualizacaoEstado) {
                    final MsgAtualizacaoEstado mensagemAttEstado = (MsgAtualizacaoEstado) mensagem;
                    
                    // SwingUtilities.invokeLater(() -> {
                        // pipeInterface.sincronizarEstado(mensagemAttEstado.getEstadoGlobal());
                        // pipeInterface.repaint();
                    // });
                }
                else if (mensagem instanceof MsgChat) {
                    final MsgChat mensagemChat = (MsgChat) mensagem;
                    pipeChat.displayTexto(mensagemChat); //Lê a mensagem e coloco no terminal
                }
                else {
                    break;
                }
                
            }catch (EOFException e) {
                // Conexao encerrada pelo Servidor de forma limpa
                break;
            } catch (IOException e) {
                // ERRO DE I/O: Conexao perdida (Socket fechado, timeout)
                break;
            } catch (ClassNotFoundException e) {
                // ERRO DE SERIALIZACAOO: O Servidor enviou uma classe que o Cliente nao conhece
                System.err.println("Falha de Serializacao (ClassNotFound): " + e.getMessage());
                break;
            } catch (Exception e) {
                 // Catch de seguranca para erros inesperados
                 break;
            } } 
    }
}

