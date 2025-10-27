package compartilhado.mensagens;
//Mensagem de entrada ou saída das cobras no servidor
public class MsgFluxo extends MensagemBase{

    private int idRemovido;
    private String username;
    private String tipo;

    public MsgFluxo(int idRemovido, String username, String tipo){
        this.idRemovido = idRemovido;
        this.username = username;
        this.tipo = tipo;
    }

    public int getIdRemovido(){
        return this.idRemovido;
    }

    public String getUsername(){
        return this.username;
    }

    public String getTipo(){
        return this.tipo;
    }
    
}