package cliente;
import compartilhado.*;
import compartilhado.mensagens.MsgAtualizacaoEstado;
import java.util.*;
import java.util.concurrent.*;

public class GerenciadorDeEstados{
	private Map<Integer, CobraRender> estadosLocal;
    private BlockingQueue<MsgAtualizacaoEstado> filaDeEventos; //FILA CRAZY CRAZY FILA 
    private int idConta;
    
    public GerenciadorDeEstados(Map<Integer, CobraRender>estadosLocal, int idConta) {
        this.estadosLocal = estadosLocal;
        this.filaDeEventos = new LinkedBlockingQueue<MsgAtualizacaoEstado>();
        Thread processador = new Thread(this::processarEventos);
        processador.setDaemon(true); // Boa prática: garante que a JVM feche se o main fechar
        processador.start();
    }

    public void debugMap() {
        for (Map.Entry<Integer, CobraRender> entry : estadosLocal.entrySet()) {
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


    public void sincronizarEstado(Map<Integer, Cobra> estadosAutorizado) {
        Set<Integer> IDsServidor = new HashSet<Integer>(estadosAutorizado.keySet());
        
        for (Cobra cobra : estadosAutorizado.values()) { //Para cada cobra na lista REAL de cobras
            int id = cobra.getId(); //Pega o ID da cobra real
           
            CobraRender cobraLocal = estadosLocal.get(id); // Para cada cobra real, pega a sua render local
            if (cobraLocal == null) { //Se não for capaz de achar localmente, cria uma nova renderização de cobra
                CobraRender novoCobraLocal = new CobraRender(cobra); //Cria um render de Cobra baseado na cobra real
                estadosLocal.put(id, novoCobraLocal); 
            }
            else {//Atualiza posições da render
                cobraLocal.setDancando(cobra.getDancando());
                cobraLocal.setXAlvo(cobra.getXAlvo());
                cobraLocal.setYAlvo(cobra.getYAlvo());
            }
            
        }

        Set<Integer> IDsLocais = new HashSet<Integer>(estadosLocal.keySet()) ;
        for (Integer id : IDsLocais) {
            if (!IDsServidor.contains(id)) remover(id);
        };
    }

    public void remover(int idRemovido){
        // synchronized (estadosLocal) {
            estadosLocal.remove(idRemovido);
        // }
    }
    
}