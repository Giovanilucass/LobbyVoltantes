package compartilhado.mensagens;

public class MsgLogin extends MensagemBase{
    private String username;
    private String senha;

    public MsgLogin(String username, String senha){
        super("LOGIN");
        this.username = username;
        this.senha = senha;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getSenha()
    {
        return this.senha;
    }
}