package compartilhado;
import java.io.*;

public class Ponto2D implements Serializable {
    private int x;
	private int y;
	
	public Ponto2D(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
}