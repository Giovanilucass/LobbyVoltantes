package cliente;
import compartilhado.*;
import compartilhado.mensagens.MsgAtualizacaoEstado;
import java.util.*;
import java.util.concurrent.*;

public class GerenciadorDeEstados{
	private Map<Integer, PinguimRender> estadosLocal;
    private BlockingQueue<MsgAtualizacaoEstado> filaDeEventos; //FILA CRAZY CRAZY FILA 
    
    public GerenciadorDeEstados(Map<Integer, PinguimRender>estadosLocal) {
        this.estadosLocal = estadosLocal;
        this.filaDeEventos = new LinkedBlockingQueue<MsgAtualizacaoEstado>();
        Thread processador = new Thread(this::processarEventos);
        processador.setDaemon(true); // Boa prática: garante que a JVM feche se o main fechar
        processador.start();
    }

    public void debugMap() {
        for (Map.Entry<Integer, PinguimRender> entry : estadosLocal.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getXAlvo() + " ; " + entry.getValue().getYAlvo());
        }
    }

    public void addEvento(MsgAtualizacaoEstado evento) {
        filaDeEventos.offer(evento); //Equivalente ao add
    }

    private void processarEventos(){
        while(true) {
            try {
                MsgAtualizacaoEstado msg = filaDeEventos.take(); // Take = Offer
                sincronizarEstado(msg.getEstadoGlobal()); //Faz a sincronização
                debugMap();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public void sincronizarEstado(Map<Integer, Pinguim> estadosAutorizado) {
        for (Pinguim pinguim : estadosAutorizado.values()) { //Para cada pinguim na lista REAL de pinguins
            int id = pinguim.getId(); //Pega o ID do pinguim real
            PinguimRender pinguimLocal = estadosLocal.get(id); // Para cada pinguim real, pega a sua render local

            if (pinguimLocal == null) { //Se não for capaz de achar localmente, cria uma nova renderização de pinguim
                PinguimRender novoPinguimLocal = new PinguimRender(pinguim); //Cria um render de Pinguim baseado no pinguim real
                estadosLocal.put(id, novoPinguimLocal); 
            }

            else {//Atualiza posições da render
                pinguimLocal.setDancando(pinguim.getDancando());
                pinguimLocal.setXAlvo(pinguim.getXAlvo());
                pinguimLocal.setYAlvo(pinguim.getYAlvo());
            }
        }
    }

    public void remover(int idRemovido){
        // synchronized (estadosLocal) {
            estadosLocal.remove(idRemovido);
        // }
    }
    
}