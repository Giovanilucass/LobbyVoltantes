package cliente;

import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.HashMap; 

class PainelJogo extends JPanel implements MouseListener, ActionListener { 
    
    public static String[] cores = {"VERMELHO", "AZUL", "VERDE", "ROSA", "AMARELO", "ROXO"};

    private static final long serialVersionUID = 1L;

    private MensageiroCliente mensageiroCliente;
    
    private Map<Integer, CobraRender> pinguinsMap; // Mapa de todos os pinguins
    private final int meuCobraId; // ID da cobra controlado pelo usuário
    private CobraRender meuCobraLocal; // Referência direta para o cobra local

    // Mapa para gerenciar sprites dinamicamente (Cor -> Imagem)
    private Map<String, Image> sprites = new HashMap<>(); 
    
    private final int LARGURA_SPRITE = 64; 
    private final int ALTURA_SPRITE = 64;  
    private final int SHADOW_WIDTH = (int) (LARGURA_SPRITE * 0.8);
    private final int SHADOW_HEIGHT = (int) (ALTURA_SPRITE * 0.25); 
    private final float SHADOW_OPACITY = 0.4f; 
    private Timer gameTimer; 
    private final int FPS = 60; 
    private final int DELAY_MS = 1000 / FPS; 

    public static String getSpritePath(String cor) {
        switch(cor.toUpperCase()){
            case "VERDE": return "sprites/voltante.png";
            case "AMARELO": return "sprites/voltanteAmarelo.png";
            case "AZUL": return "sprites/voltanteAzul.png";
            case "ROSA": return "sprites/voltanteRosa.png";
            case "ROXO": return "sprites/voltanteRoxo.png";
            case "VERMELHO": return "sprites/voltanteVermeio.png";
            default: return "sprites/voltante.png";
        }
    }

    // Construtor atualizado: recebe o mapa e o ID local
    public PainelJogo(Map<Integer, CobraRender> pinguinsMap, int meuCobraId, MensageiroCliente mensageiroCliente) {
        this.pinguinsMap = pinguinsMap;
        this.meuCobraId = meuCobraId;

        this.mensageiroCliente = mensageiroCliente;
        
        // Obtém a referência local para a cobra do jogador
        this.meuCobraLocal = pinguinsMap.get(meuCobraId);
        
        carregarSprites(); // Carrega todos os sprites

        setBackground(Color.WHITE); 
        this.addMouseListener(this); 
        
        gameTimer = new Timer(DELAY_MS, this);
        gameTimer.start();
    }
    
    private void carregarSprites(){
        // Itera sobre todas as cores e carrega os sprites
        for (String cor : cores) {
            String caminhoSprite = getSpritePath(cor);
            
            Image sprite = null;
            try {
                ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(caminhoSprite));
                sprite = icon.getImage();
                
                if (sprite == null || sprite.getWidth(null) == -1) {
                    System.err.println("Aviso: Sprite não encontrado/inválido: " + caminhoSprite);
                } else {
                    sprites.put(cor, sprite);
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar o sprite " + caminhoSprite + ": " + e.getMessage());
            }
        }
        if (!sprites.containsKey("DEFAULT")) {
            sprites.put("DEFAULT", sprites.getOrDefault("VERDE", null)); 
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        Graphics2D g2d = (Graphics2D) g;
        
        // Itera sobre TODOS os pinguins no mapa para desenhar
        for (CobraRender cobra : pinguinsMap.values()) {
            desenharCobra(g2d, cobra);
        }
    }
    
    private void desenharCobra(Graphics2D g2d, CobraRender cobra) {
        
        Image spriteCobra = sprites.getOrDefault(cobra.getCor(), sprites.get("DEFAULT"));
        // Se a cor for nula ou não estiver carregada, use o default
        if (spriteCobra == null) return; 

        int x_atual = cobra.getXAtual(); 
        int y_atual = cobra.getYAtual();
        
        int x_sprite = x_atual - (LARGURA_SPRITE / 2);
        int y_sprite = y_atual - (ALTURA_SPRITE / 2);
        
        // Coordenadas para a sombra
        int x_sombra = x_atual - (SHADOW_WIDTH / 2);
        int y_sombra = y_atual + (ALTURA_SPRITE / 2) - SHADOW_HEIGHT; 
        
        AffineTransform originalTransform = g2d.getTransform();

        // A. DESENHO DA SOMBRA
        Composite originalComposite = g2d.getComposite();
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SHADOW_OPACITY);
        g2d.setComposite(alpha);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x_sombra, y_sombra, SHADOW_WIDTH, SHADOW_HEIGHT);
        g2d.setComposite(originalComposite);
        
        // B. ROTAÇÃO E DESENHO DO SPRITE DO COBRA
        if (cobra.getDancando()) {
            g2d.translate(x_atual, y_atual);
            double radianos = Math.toRadians(cobra.getAnguloRotacao());
            g2d.rotate(radianos);
            
            g2d.drawImage(spriteCobra, -LARGURA_SPRITE / 2, -ALTURA_SPRITE / 2, LARGURA_SPRITE, ALTURA_SPRITE, this);
            
            g2d.setTransform(originalTransform); 
        } else {
            g2d.drawImage(spriteCobra, x_sprite, y_sprite, LARGURA_SPRITE, ALTURA_SPRITE, this);
        }
        
        // C. DESENHO DO USERNAME
        String username = cobra.getUsername();
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        
        FontMetrics fm = g2d.getFontMetrics();
        int larguraTexto = fm.stringWidth(username);
        
        int x_texto = x_atual - (larguraTexto / 2);
        int y_texto = y_sprite - 10; 
        
        g2d.drawString(username, x_texto, y_texto);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Evento de mouse afeta APENAS o cobra local
        if (meuCobraLocal != null) {
            if (!meuCobraLocal.getDancando()) {
                int novoX = e.getX();
                int novoY = e.getY();
                meuCobraLocal.setXAlvo(novoX);
                meuCobraLocal.setYAlvo(novoY);
                try{
                    mensageiroCliente.enviaPosicao(novoX, novoY);
                }catch(Exception excecao){
                    excecao.printStackTrace();
                }
                //System.out.println("Alvo da cobra local (" + meuCobraLocal.getId() + ") definido para: (" + novoX + ", " + novoY + ")");
            } else {
                //System.out.println("Cobra local está dançando e não pode se mover!");
            }
        }
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Atualiza a posição de TODOS os pinguins no mapa
        for (CobraRender cobra : pinguinsMap.values()) {
             cobra.atualizarPosicao();
        }
        repaint();
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}