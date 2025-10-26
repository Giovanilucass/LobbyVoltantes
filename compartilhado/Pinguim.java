package compartilhado;
import java.io.*;

public class Pinguim implements Serializable{
	private int id;
	private Ponto2D posicaoAtual;
	private Ponto2D posicaoAlvo;
	private boolean estaDancando;
	private String cor;
	
	public Pinguim(int id, int x_Atual, int y_Atual, int x_Alvo, int y_Alvo, boolean estaDancando, String cor){
		this.id = id;
		this.posicaoAtual = new Ponto2D(x_Atual, y_Atual);
		this.posicaoAlvo = new Ponto2D(x_Alvo, y_Alvo);
		this.estaDancando = estaDancando;
		this.cor = cor;
	}
	
	public int getId(){
		return this.id;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public boolean getDancando(){
		return this.estaDancando;
	}

	public void setDancando(){
		this.estaDancando = !this.estaDancando;
	}
	
	public String getCor(){
		return this.cor;
	}

	public void setCor(String cor){
		this.cor = cor;
	}

	public int getXAtual(){
		return posicaoAtual.getX();
	}

	public int getYAtual(){
		return posicaoAtual.getY();
	}

	public int getXAlvo(){
		return posicaoAlvo.getX();
	}

	public int getYAlvo(){
		return posicaoAlvo.getY();
	}

	public void setXAtual(int x){
		this.posicaoAtual.setX(x);
	}

	public void setYAtual(int y){
		this.posicaoAtual.setY(y);
	}

	public void setXAlvo(int x){
		this.posicaoAlvo.setX(x);
	}

	public void setYAlvo(int y){
		this.posicaoAlvo.setY(y);
	}
}