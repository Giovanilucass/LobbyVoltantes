package cliente;
import java.io.*;
import compartilhado.*;

public class PinguimRender extends Pinguim{
	private transient Ponto2D posicaoAtual;
	
	
	// public PinguimRender(int id, int x_Atual, int y_Atual, int x_Alvo, int y_Alvo, boolean estaDancando, String cor){
	// 	super(id, x_Alvo, y_Alvo, estaDancando, cor);
	// 	this.posicaoAtual = new Ponto2D(x_Atual, y_Atual);
	// }
	
	public PinguimRender(Pinguim pinguim) {
		super(pinguim.getId(),
        pinguim.getUsername(),
		pinguim.getXAlvo(),
		pinguim.getYAlvo(),
		pinguim.getDancando(),
		pinguim.getCor()
		);

		this.posicaoAtual = new Ponto2D(pinguim.getXAlvo(), pinguim.getYAlvo());

	}

	public void setXAtual(int x){
		this.posicaoAtual.setX(x);
	}

	public void setYAtual(int y){
		this.posicaoAtual.setY(y);
	}

}