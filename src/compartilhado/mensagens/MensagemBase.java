package compartilhado.mensagens;
import java.io.*;

public class MensagemBase implements Serializable{ //Classe mãe de todas as mensagens, que futuramente se torna´ra uma mensagem especifíca
	private String tipo;

	public MensagemBase(String tipo){ //Podemos utilizar mensagens Base para enviar Strings normais
		this.tipo=tipo;
	}

	public String getTipo(){
		return this.tipo;
	}
}