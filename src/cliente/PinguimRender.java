package cliente;

import compartilhado.Pinguim;
import compartilhado.Ponto2D;

public class PinguimRender extends Pinguim { 
    
    private transient Ponto2D posicaoAtual;
    private final double VELOCIDADE_DE_MOVIMENTO = 0.1; 
    
    private transient double anguloRotacao = 0; 
    
    private final double VELOCIDADE_ROTACAO = 10.0; 

    public PinguimRender(compartilhado.Pinguim pinguim) {
        super(pinguim.getId(),
        pinguim.getUsername(),
        pinguim.getXAlvo(),
        pinguim.getYAlvo(),
        pinguim.getDancando(),
        pinguim.getCor()
        );

        this.posicaoAtual = new Ponto2D(pinguim.getXAlvo(), pinguim.getYAlvo());
        this.anguloRotacao = 0;
    }

    public int getXAtual(){ return this.posicaoAtual.getX(); }
    public int getYAtual(){ return this.posicaoAtual.getY(); }
    public void setXAtual(int x){ this.posicaoAtual.setX(x); }
    public void setYAtual(int y){ this.posicaoAtual.setY(y); }
    
    public double getAnguloRotacao() {
        return this.anguloRotacao;
    }

    public void atualizarPosicao() {

        // Se estiver dançando, não se move, mas atualiza a rotação
        if (super.getDancando()) {
            this.atualizarRotacao();
            return;
        }

        // Lógica de Movimentação
        double currentX = this.posicaoAtual.getX();
        double currentY = this.posicaoAtual.getY();
        int targetX = super.getXAlvo();
        int targetY = super.getYAlvo();

        double dx = targetX - currentX;
        double dy = targetY - currentY;
        
        if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
            this.posicaoAtual.setX(targetX);
            this.posicaoAtual.setY(targetY);
            return;
        }

        double moveX = dx * VELOCIDADE_DE_MOVIMENTO;
        double moveY = dy * VELOCIDADE_DE_MOVIMENTO;

        this.posicaoAtual.setX((int) (currentX + moveX));
        this.posicaoAtual.setY((int) (currentY + moveY));
    }
    
    private void atualizarRotacao() {
        // Incrementa o ângulo
        this.anguloRotacao += VELOCIDADE_ROTACAO;
        
        // Mantém o ângulo entre 0 e 360 graus (loop)
        if (this.anguloRotacao >= 360) {
            this.anguloRotacao -= 360;
        }
    }
}