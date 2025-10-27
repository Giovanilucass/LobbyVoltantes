package compartilhado.mensagens;

public class MsgChat extends MensagemBase{

	private String username; //Id remetente
	private String texto;
	
	public MsgChat(String texto, String username){
		super("CHAT");
		this.texto = texto;
		this.username = username;
	}

	public String getTexto()
	{
		return this.texto;
	}

	public String getUsername()
	{
		return this.username;
	}
}