package server;

public class Conta{
    private String username;
    private String senha;
    private boolean online=true;
    private int xSalvo=400;
    private int ySalvo=300;

    public Conta(String username, String senha){
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

    public int getXSalvo() {
        return xSalvo;
    }

    public int getYSalvo() {
        return ySalvo;
    }

    public void salvarPosicao(int X, int Y) {
        this.xSalvo = X;
        this.ySalvo = Y;
    }

}
