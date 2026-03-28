package server;
//SERÁ QUE JOGADOR PODE TER ATRIBUTO DE ONLINE?
public class Conta{
    private String username;
    private String senha;
    private boolean online;

    public Conta(String username, String senha){
        this.online = true;
        this.username = username;
        this.senha = senha;
    }

    public String getUsername()
    {
        return this.username;
    }

    public boolean compareSenha(String senha) {
        return senha.equals(this.senha);
    }

    public void setOnline(boolean online){
        this.online = online;
    }

    public boolean getOnline(){
        return this.online;
    }
}