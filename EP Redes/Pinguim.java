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
	
	public boolean getDancando(){
		return this.estaDancando;
	}
	
	public String getCor(){
		return this.cor;
	}
}