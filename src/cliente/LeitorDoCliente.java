package cliente;
import compartilhado.mensagens.*;
import java.io.*;

public class LeitorDoCliente implements Runnable{
    private ObjectInputStream ois; //Recebe a atualização do servidor (estados autorizados)
    private final GerenciadorDeEstados pipeGerenciadorEstados; //Comunicação entre leitor e interface
    private final MensageiroCliente pipeChat; //Comunicação entre leitor de cliente e chat

    public LeitorDoCliente(ObjectInputStream ois, GerenciadorDeEstados Interface, MensageiroCliente chat) {
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
                
                if (mensagem instanceof MsgAtualizacaoEstado mensagemAttEstado) { //A última palavra recebe o typecast da primeira
                    pipeGerenciadorEstados.addEvento(mensagemAttEstado);
                    // System.out.println("Atualização chegou.");
                  
                }
                else if (mensagem instanceof MsgChat mensagemChat) {
                    Jogo.adicionarMensagemChatCliente(mensagemChat.getUsername(), mensagemChat.getTexto());
                }

                else if (mensagem instanceof MsgFluxo msgFluxo) {
                    if(msgFluxo.getTipo().equals("SAIDA")){
                        Jogo.adicionarMensagemChatServidor(msgFluxo.getUsername() + " SAIU DO SERVIDOR");
                    }else if(msgFluxo.getTipo().equals("ENTRADA")){
                        Jogo.adicionarMensagemChatServidor(msgFluxo.getUsername() + " ENTROU NO SERVIDOR");
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

