package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.petshop.service.ClienteService;
import com.petshop.service.PacoteService;
import com.petshop.service.PetService;
import com.petshop.service.ServicoService;

public class TelaMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // --- CORES DO MENU LATERAL (ESTILO DASHBOARD MODERNO) ---
    private final Color COR_SIDEBAR = new Color(33, 37, 41); // PRETO SUAVE
    private final Color COR_BOTAO_NORMAL = new Color(33, 37, 41);
    private final Color COR_BOTAO_HOVER = new Color(73, 80, 87); // CINZA AO PASSAR O MOUSE
    private final Color COR_TEXTO_MENU = Color.WHITE;
    private final Color COR_FUNDO_PRINCIPAL = new Color(245, 245, 245); // CINZA MUITO CLARO

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new TelaMenu().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public TelaMenu() {
        setTitle("Sistema Pet Shop Profissional");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 650);
        setLocationRelativeTo(null);

        // --- LAYOUT PRINCIPAL: BORDER LAYOUT ---
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // =================================================================
        // 1. MENU LATERAL (SIDEBAR - ESQUERDA)
        // =================================================================
        JPanel panelSidebar = new JPanel();
        panelSidebar.setBackground(COR_SIDEBAR);
        panelSidebar.setPreferredSize(new Dimension(220, 600));
        panelSidebar.setLayout(new GridLayout(9, 1, 0, 5)); // LINHAS PARA OS BOTÕES
        panelSidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // --- TÍTULO NO TOPO DO MENU ---
        JLabel lblLogo = new JLabel("SISTEMA PET");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBorder(new EmptyBorder(0, 0, 20, 0)); // ESPAÇO ABAIXO DO TÍTULO
        panelSidebar.add(lblLogo);

        // --- BOTÕES DO MENU LATERAL ---
        panelSidebar.add(criarBotaoMenu(" > Clientes", e -> abrirTelaListarClientes()));
        panelSidebar.add(criarBotaoMenu(" > Novo Cliente", e -> abrirTelaCadastroCliente()));
        panelSidebar.add(criarBotaoMenu(" > Pets", e -> abrirTelaPets()));
        panelSidebar.add(criarBotaoMenu(" > Serviços", e -> abrirTelaServicos()));
        panelSidebar.add(criarBotaoMenu(" > Pacotes", e -> abrirTelaPacotes()));
        
        // ESPAÇADOR (PARA EMPURRAR O BOTÃO SAIR UM POUCO)
        panelSidebar.add(new JLabel("")); 
        panelSidebar.add(new JLabel("")); 

        // --- BOTÃO SAIR (COM COR DIFERENCIADA) ---
        JButton btnSair = criarBotaoMenu(" Sair do Sistema", e -> System.exit(0));
        btnSair.setForeground(new Color(255, 107, 107)); // VERMELHO CLARO
        panelSidebar.add(btnSair);

        contentPane.add(panelSidebar, BorderLayout.WEST);

        // =================================================================
        // 2. ÁREA DE CONTEÚDO (CENTRO - DIREITA)
        // =================================================================
        JPanel panelConteudo = new JPanel();
        panelConteudo.setBackground(COR_FUNDO_PRINCIPAL);
        panelConteudo.setLayout(new GridBagLayout()); // CENTRALIZA O TEXTO NO MEIO DA TELA

        // --- TEXTO DE BOAS-VINDAS ---
        JLabel lblBemVindo = new JLabel("<html><center>Bem-vindo ao Sistema<br>Selecione uma opção no menu lateral</center></html>");
        lblBemVindo.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        lblBemVindo.setForeground(new Color(150, 150, 150)); // CINZA SUAVE
        
        panelConteudo.add(lblBemVindo);
        contentPane.add(panelConteudo, BorderLayout.CENTER);
    }

    // --- MÉTODO PARA CRIAR BOTÕES ESTILIZADOS DO MENU ---
    private JButton criarBotaoMenu(String texto, ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setForeground(COR_TEXTO_MENU);
        btn.setBackground(COR_BOTAO_NORMAL);
        // REMOVE BORDAS PADRÃO DO JAVA E ADICIONA PADDING INTERNO
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0)); 
        btn.setHorizontalAlignment(SwingConstants.LEFT); // TEXTO ALINHADO À ESQUERDA
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // ADICIONA A AÇÃO (CLIQUE)
        btn.addActionListener(acao);

        // --- EFEITO HOVER (MUDAR DE COR QUANDO O MOUSE PASSA) ---
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(COR_BOTAO_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COR_BOTAO_NORMAL);
            }
        });

        return btn;
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    private void abrirTelaListarClientes() {
        try {
            new TelaListarClientes(new ClienteService()).setVisible(true);
        } catch (Exception ex) { erro(ex); }
    }
    
    private void abrirTelaCadastroCliente() {
        try {
            new TelaCadastroCliente(new ClienteService()).setVisible(true);
        } catch (Exception ex) { erro(ex); }
    }

    private void abrirTelaPets() {
        try {
            new TelaCadastroPets(new PetService(), new ClienteService()).setVisible(true);
        } catch (Exception ex) { erro(ex); }
    }
    
    private void abrirTelaServicos() {
        try {
            // AGORA PASSAMOS O PACOTESERVICE TAMBÉM
            new TelaCadastroServico(
                new ServicoService(), 
                new PetService(), 
                new ClienteService(), 
                new PacoteService() 
            ).setVisible(true);
        } catch (Exception ex) { erro(ex); }
    }
    
    private void abrirTelaPacotes() {
        try {
            new TelaCadastroPacote(new PacoteService()).setVisible(true);
        } catch (Exception ex) { erro(ex); }
    }
    
    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this, "ERRO: " + ex.getMessage());
    }
}