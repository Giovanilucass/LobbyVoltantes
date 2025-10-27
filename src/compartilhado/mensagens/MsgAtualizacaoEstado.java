package compartilhado.mensagens;
import java.util.*;
import compartilhado.Cobra;

public class MsgAtualizacaoEstado extends MensagemBase{
	private Map<Integer, Cobra> estadoGlobal;

	public MsgAtualizacaoEstado(Map<Integer, Cobra> estadoGlobal){
		super("ATUALIZACAO");
		this.estadoGlobal = new HashMap<Integer, Cobra>(estadoGlobal);
	}

	public Map<Integer, Cobra> getEstadoGlobal() {
		return estadoGlobal;
	}
}