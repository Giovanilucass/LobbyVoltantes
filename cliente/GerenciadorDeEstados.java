package cliente;
import compartilhado.*;
import javax.swing.*;
import java.util.*;

public class GerenciadorDeEstados{
	private Map<Integer, PinguimRender> estadosLocal;
    
    
    public GerenciadorDeEstados(Map<Integer, PinguimRender>estadosLocal) {
        this.estadosLocal = estadosLocal;
    }

    public void debugMap() {
        for (Map.Entry<Integer, PinguimRender> entry : estadosLocal.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue().getXAlvo() + " ; " + entry.getValue().getYAlvo());
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
                estadosLocal.put(id, pinguimLocal);
            }
        }
    }

    public void remover(int idRemovido){
        estadosLocal.remove(idRemovido);
    }
    
}