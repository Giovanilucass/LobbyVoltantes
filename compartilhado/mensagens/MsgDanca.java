package compartilhado.mensagens;


public class MsgDanca extends MensagemBase{ //
	private int idRemetente;
	private String acao; // Ex: "INICIAR_DANCA", 

	public MsgDanca(int idRemetente){
		super("DANCA");
		this.idRemetente = idRemetente;
	}

	public int getID()
	{
		return this.idRemetente;
	}
}