package compartilhado.mensagens;
import java.util.*;
import compartilhado.Pinguim;

public class MsgAtualizacaoEstado extends MensagemBase{
	private Map<Integer, Pinguim> estadoGlobal;

	public MsgAtualizacaoEstado(Map<Integer, Pinguim> estadoGlobal){
		super("ATUALIZACAO");
		this.estadoGlobal = new HashMap<Integer, Pinguim>(estadoGlobal);
	}

	public Map<Integer, Pinguim> getEstadoGlobal() {
		return estadoGlobal;
	}
}