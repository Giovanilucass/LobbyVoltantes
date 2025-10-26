package compartilhado.mensagens;

public class MsgRespostaLogin extends MensagemBase{
    private String mensagem;
    private boolean login;

    public MsgRespostaLogin(String mensagem, boolean login){
        super("RESPOSTA_LOGIN");
        this.mensagem = mensagem;
        this.login = login;
    }

    public String getMensagem()
    {
        return this.mensagem;
    }

    public boolean getLogin()
    {
        return this.login;
    }
}