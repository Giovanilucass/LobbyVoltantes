package cliente;
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.SwingUtilities;

import compartilhado.*;
import compartilhado.mensagens.*;

public class LeitorDoCliente implements Runnable{
    private ObjectInputStream ois; //Recebe a atualização do servidor (estados autorizados)
    private final GerenciadorDeEstados pipeGerenciadorEstados; //Comunicação entre leitor e interface
    private final ChatCliente pipeChat; //Comunicação entre leitor de cliente e chat

    public LeitorDoCliente(ObjectInputStream ois, GerenciadorDeEstados Interface, ChatCliente chat) {
        this.ois = ois;
        this.pipeGerenciadorEstados = Interface;
        this.pipeChat = chat;

    }

    @Override
    public void run() {
        MensagemBase mensagem; //Me
        while (true) {
            try {
                mensagem = (MensagemBase) ois.readObject(); // Le a mensgem recebida
                
                if (mensagem instanceof MsgAtualizacaoEstado) {
                    final MsgAtualizacaoEstado mensagemAttEstado = (MsgAtualizacaoEstado) mensagem;
                    pipeGerenciadorEstados.addEvento(mensagemAttEstado);
                    System.out.println("Atualização chegou.");
                    
                  
                }
                else if (mensagem instanceof MsgChat) {
                    final MsgChat mensagemChat = (MsgChat) mensagem; //Final: transforma variavel em constante (não correr risco do objeto ser alterado)
                    pipeChat.chatClientes(mensagemChat); //Pede para o chat tratar a mensagem (mas será que tem que esperar o chat terminar para continuar rodando o leitor?)
    
                }
                else if (mensagem instanceof MsgFluxo) {
                    final MsgFluxo msgFluxo = (MsgFluxo) mensagem;
                    if(msgFluxo.getTipo().equals("SAIDA")){
                        pipeGerenciadorEstados.remover(msgFluxo.getIdRemovido());
                        pipeChat.chatServer(msgFluxo.getUsername() + " SAIU DO SERVIDOR");
                    }else if(msgFluxo.getTipo().equals("ENTRADA")){
                        pipeChat.chatServer(msgFluxo.getUsername() + " ENTROU NO SERVIDOR");
                    }
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

