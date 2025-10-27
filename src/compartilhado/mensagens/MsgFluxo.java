package compartilhado.mensagens;
//Mensagem de entrada ou saída dos pinguins no servidor
public class MsgFluxo extends MensagemBase{

    private int idRemovido;
    private String username;

    public MsgFluxo(int idRemovido, String username, String tipo){
        super(tipo);
        this.idRemovido = idRemovido;
        this.username = username;
    }

    public int getIdRemovido(){
        return this.idRemovido;
    }

    public String getUsername(){
        return this.username;
    }
    
}