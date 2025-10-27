package cliente;

import compartilhado.Pinguim;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Jogo {

    public static String[] cores = {"VERMELHO", "AZUL", "VERDE", "ROSA", "AMARELO", "ROXO"};

    // JTextArea e JScrollPane serão acessíveis por meio de uma referência final
    private static JTextArea logChat;

    public static void iniciaJogo(int MEU_PINGUIM_ID, Map<Integer, PinguimRender> pinguinsRenderMap, MensageiroCliente mensageiroCliente) {
        SwingUtilities.invokeLater(() -> {

            // --- 2. CONFIGURAÇÃO DA JANELA E PAINEL ---
            JFrame frame = new JFrame("Lobby dos Voltantes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600); 
            frame.setLocationRelativeTo(null); 
            frame.setLayout(new BorderLayout());

            PainelJogo painelJogo = new PainelJogo(pinguinsRenderMap, MEU_PINGUIM_ID, mensageiroCliente);
            frame.add(painelJogo, BorderLayout.CENTER); 
            
            
            // --- 3. CRIAÇÃO DA ÁREA DE CHAT E BOTÃO DE DANÇA ---
            
            // 3.1. NOVO: Área de Log do Chat (JTextArea em um JScrollPane)
            logChat = new JTextArea(4, 55); // 4 linhas, 55 colunas
            logChat.setEditable(false); // O usuário não deve editar o log
            logChat.setLineWrap(true);   // Quebra de linha automática
            logChat.setWrapStyleWord(true); // Quebra por palavra
            
            JScrollPane scrollChat = new JScrollPane(logChat);
            scrollChat.setPreferredSize(new Dimension(650, 80)); // Tamanho fixo para o log
            
            // 3.2. Campo de entrada de chat (JTextField)
            JTextField campoChat = new JTextField(40); 
            campoChat.setToolTipText("Digite sua mensagem e pressione Enter");

            // **MODIFICADO**: Botão de Saída usando botaoSaida.png
            ImageIcon iconSair = null;
            try {
                // Carrega o sprite do botão Sair
                iconSair = new ImageIcon(Jogo.class.getClassLoader().getResource("botaoSaida.png"));
            } catch (Exception e) {
                System.err.println("Erro ao carregar sprite do botão (botaoSaida.png). Usando texto.");
            }
            
            JButton btnSair = new JButton();
            if (iconSair != null && iconSair.getImageLoadStatus() == MediaTracker.COMPLETE) {
                 // Redimensiona o ícone para o tamanho do botão Dançar
                 Image img = iconSair.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                 btnSair.setIcon(new ImageIcon(img));
                 btnSair.setPreferredSize(new Dimension(50, 50));
                 btnSair.setText(""); // Remove o texto
            } else {
                 btnSair.setText("Sair"); // Usa texto como fallback
                 btnSair.setPreferredSize(new Dimension(100, 50));
                 btnSair.setForeground(Color.WHITE);
            }
            btnSair.setToolTipText("Sair");
            
            // 3.3. Botão de Dança (Código mantido)
            ImageIcon iconDanca = null;
            try {
                iconDanca = new ImageIcon(Jogo.class.getClassLoader().getResource("botaoDanca.png"));
            } catch (Exception e) {
                System.err.println("Erro ao carregar sprite do botão (botaoDanca.png). Usando texto.");
            }
            
            JButton btnDanca = new JButton();
            if (iconDanca != null && iconDanca.getImageLoadStatus() == MediaTracker.COMPLETE) {
                 Image img = iconDanca.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                 btnDanca.setIcon(new ImageIcon(img));
                 btnDanca.setPreferredSize(new Dimension(50, 50));
                 btnDanca.setText("");
            } else {
                 btnDanca.setText("Dançar");
                 btnDanca.setPreferredSize(new Dimension(100, 50));
            }
            btnDanca.setToolTipText("Dançar");
            
            // 3.4. Painel de entrada (JTextField + JButton)
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Alinha à direita, margem de 5px entre componentes
            inputPanel.setOpaque(false);
            
            inputPanel.add(campoChat); 
            inputPanel.add(btnDanca);
            inputPanel.add(btnSair); 

            // 3.5. Painel de UI Principal (Contém o scroll acima do input)
            JPanel uiPanel = new JPanel();
            // Usamos BoxLayout para empilhar verticalmente o log acima do painel de input
            uiPanel.setLayout(new BoxLayout(uiPanel, BoxLayout.Y_AXIS));
            uiPanel.setOpaque(false);
            
            // NOVO: Adiciona o scroll (log)
            scrollChat.setAlignmentX(Component.RIGHT_ALIGNMENT); // Alinha o scroll à direita
            uiPanel.add(scrollChat);
            
            // Adiciona o input (campo + botão)
            inputPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            uiPanel.add(inputPanel); 
            
            // Adiciona o painel de UI (Log + Input) no fundo do Frame
            frame.add(uiPanel, BorderLayout.SOUTH); 
            
            
            // --- 4. CONFIGURAÇÃO DAS AÇÕES ---

            // Ação do Botão Dançar
            btnDanca.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PinguimRender pinguimLocal = pinguinsRenderMap.get(MEU_PINGUIM_ID);
                    if (pinguimLocal != null) {
                        pinguimLocal.setDancando(!pinguimLocal.getDancando());
                        try{
                            mensageiroCliente.enviaDanca();
                        }catch(Exception excecao){
                            excecao.printStackTrace();
                        }
                        System.out.println("Estado de dança do pinguim " + MEU_PINGUIM_ID + " alterado para: " + pinguimLocal.getDancando());
                    }
                }
            });

            // Ação do Botão SAIR (MANTIDA)
            btnSair.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Solicitando encerramento do jogo...");
                    try{
                        mensageiroCliente.enviaSair();
                    }catch(Exception excecao){
                        excecao.printStackTrace();
                    }
                    System.exit(0); 
                }
            });
            
            // Ação do campo de chat ao apertar ENTER
            campoChat.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String mensagem = campoChat.getText();
                    try{
                        mensageiroCliente.enviaChat(mensagem);
                    }catch(Exception excecao){
                        excecao.printStackTrace();
                    }
                    if (!mensagem.trim().isEmpty()) {
                        String username = pinguinsRenderMap.get(MEU_PINGUIM_ID).getUsername();
                        
                        // Envia a mensagem para o log na tela
                        adicionarMensagemChatCliente(username, mensagem);
                        
                        // Simulação de log no terminal (Mantido para debugging de rede)
                        System.out.println("CHAT: [" + username + "]: " + mensagem);
                        
                        // Limpa o campo de texto após o envio
                        campoChat.setText("");
                    }
                }
            });

            // 5. Exibir a Janela
            frame.setVisible(true);
            
            // Simulação de uma mensagem inicial
            adicionarMensagemChatServidor("Bem-vindo ao Lobby dos Voltantes!");
        });
    }
    
    /**
     * **NOVO MÉTODO**: Adiciona uma nova mensagem ao log de chat e força a rolagem.
     */
    public static void adicionarMensagemChatCliente(String username, String mensagem) {
        String linha = String.format("[%s]: %s\n", username, mensagem);
        // Adiciona a nova linha ao final do JTextArea
        logChat.append(linha);
        // Garante que a área de texto role automaticamente para a última linha (efeito "de baixo para cima")
        logChat.setCaretPosition(logChat.getDocument().getLength());
    }

    public static void adicionarMensagemChatServidor(String mensagem) {
        String linha = String.format("[SERVER] %s\n", mensagem);
        logChat.append(linha);
        logChat.setCaretPosition(logChat.getDocument().getLength());
    }

    // (Método getSpritePath mantido inalterado)
    public static String getSpritePath(String cor) {
        // ... (lógica de switch case) ...
        switch(cor.toUpperCase()){
            case "VERDE": return "voltante.png";
            case "AMARELO": return "voltanteAmarelo.png";
            case "AZUL": return "voltanteAzul.png";
            case "ROSA": return "voltanteRosa.png";
            case "ROXO": return "voltanteRoxo.png";
            case "VERMELHO": return "voltanteVermeio.png";
            default: return "voltante.png";
        }
    }
}