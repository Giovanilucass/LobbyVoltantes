package compartilhado.mensagens;
import java.io.*;

public class MensagemBase implements Serializable{ //Classe mãe de todas as mensagens, que futuramente se torna´ra uma mensagem especifíca

	public MensagemBase(){ //Podemos utilizar mensagens Base para garantir que não entrarão quaisquer objetos nos métodos enviados
	}

}