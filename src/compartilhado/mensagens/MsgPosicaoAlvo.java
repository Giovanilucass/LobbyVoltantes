package compartilhado.mensagens;
import compartilhado.Ponto2D;

public class MsgPosicaoAlvo extends MensagemBase{
	private Ponto2D alvo;

	public MsgPosicaoAlvo(Ponto2D alvo){
		this.alvo = alvo;
	}

	public Ponto2D getAlvo()
	{
		return this.alvo;
	}

}