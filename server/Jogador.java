package server;

public class Jogador{
    private String username;
    private String senha;

    public Jogador(String username, String senha){
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
}