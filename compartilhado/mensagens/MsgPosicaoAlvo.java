package compartilhado.mensagens;
import compartilhado.Ponto2D;

public class MsgPosicaoAlvo extends MensagemBase{
	private int idRemetente;
	private Ponto2D alvo;

	public MsgPosicaoAlvo(int idRemetente, Ponto2D alvo){
		super("ALVO");
		this.alvo = alvo;
		this.idRemetente = idRemetente;
	}

	public Ponto2D getAlvo()
	{
		return this.alvo;
	}

	public int getID()
	{
		return this.idRemetente;
	}
}