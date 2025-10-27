package compartilhado.mensagens;

public class MsgRespostaLogin extends MensagemBase{
    private String mensagem;
    private int id;
    private boolean login;

    public MsgRespostaLogin(String mensagem, boolean login, int id){
        super("RESPOSTA_LOGIN");
        this.mensagem = mensagem;
        this.login = login;
        this.id = id;
    }

    public String getMensagem()
    {
        return this.mensagem;
    }

    public boolean getLogin()
    {
        return this.login;
    }

    public int getId(){
        return this.id;
    }
}