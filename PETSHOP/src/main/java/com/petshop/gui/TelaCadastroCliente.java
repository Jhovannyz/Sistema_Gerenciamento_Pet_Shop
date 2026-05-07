package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.petshop.model.Cliente;
import com.petshop.service.ClienteService;

public class TelaCadastroCliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // CORES DO TEMA (PADRÃO DO PROJETO)
    private final Color COR_FUNDO = new Color(240, 242, 245);
    private final Color COR_BRANCA = Color.WHITE;
    private final Color COR_VERDE = new Color(40, 167, 69);
    private final Color COR_VERMELHO = new Color(220, 53, 69);
    private final Color COR_TEXTO = new Color(50, 50, 50);

    // CAMPOS DO FORMULÁRIO
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtEndereco;
    
    private final ClienteService clienteService;

    // MÉTODO MAIN PARA TESTES ISOLADOS
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                ClienteService service = new ClienteService();
                new TelaCadastroCliente(service).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // CONSTRUTOR DA TELA
    public TelaCadastroCliente(ClienteService clienteService) {
        this.clienteService = clienteService;
        
        setTitle("Cadastro de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 600); // AUMENTEI A ALTURA PARA CABER TUDO
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBackground(COR_FUNDO);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);
        
        // --- CABEÇALHO ---
        JLabel lblTitulo = new JLabel("Novo Cliente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);
        
        // --- FORMULÁRIO (CARD BRANCO) ---
        JPanel cardForm = new JPanel(new BorderLayout());
        cardForm.setBackground(COR_BRANCA);
        cardForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1), 
                new EmptyBorder(20, 20, 20, 20)));
        
        // GRID COM ESPAÇAMENTO VERTICAL MAIOR (20PX)
        JPanel panelCampos = new JPanel(new GridLayout(5, 1, 10, 20)); 
        panelCampos.setBackground(COR_BRANCA);
        
        panelCampos.add(criarPanelCampo("Nome Completo*:", txtNome = criarTextField()));
        panelCampos.add(criarPanelCampo("CPF:", txtCpf = criarTextField()));
        panelCampos.add(criarPanelCampo("Telefone*:", txtTelefone = criarTextField()));
        panelCampos.add(criarPanelCampo("E-mail*:", txtEmail = criarTextField()));
        panelCampos.add(criarPanelCampo("Endereço:", txtEndereco = criarTextField()));
        
        cardForm.add(panelCampos, BorderLayout.CENTER);
        contentPane.add(cardForm, BorderLayout.CENTER);
        
        // --- BOTÕES (RODAPÉ) ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoes.setBackground(COR_FUNDO);
        
        JButton btnCancelar = criarBotao("Cancelar", COR_VERMELHO);
        btnCancelar.addActionListener(e -> dispose());
        
        JButton btnSalvar = criarBotao("Salvar Cliente", COR_VERDE);
        btnSalvar.addActionListener(e -> salvarCliente());
        
        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        
        contentPane.add(panelBotoes, BorderLayout.SOUTH);
    }
    
 // --- LÓGICA DE SALVAMENTO ---
    private void salvarCliente() {
        // 1. VALIDAÇÃO BÁSICA (CAMPOS VAZIOS)
        if (txtNome.getText().trim().isEmpty() || 
            txtTelefone.getText().trim().isEmpty() || 
            txtEmail.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios (*).", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. >>> COLOQUE AQUI A VALIDAÇÃO DO E-MAIL <<<
        if (!txtEmail.getText().contains("@") || !txtEmail.getText().contains(".")) {
            JOptionPane.showMessageDialog(this, "E-mail inválido! Verifique o formato.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Cliente cliente = new Cliente();
            cliente.setNome(txtNome.getText().trim());
            cliente.setCpf(txtCpf.getText().trim());
            cliente.setTelefone(txtTelefone.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setEndereco(txtEndereco.getText().trim());
            
            clienteService.salvar(cliente);
            
            JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtCpf.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        txtNome.requestFocus();
    }
    
    // --- MÉTODOS VISUAIS AUXILIARES ---
    
    private JTextField criarTextField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tf.setPreferredSize(new Dimension(200, 40)); 
        
        // BORDA COMPOSTA + PADDING INTERNO CONFORTÁVEL
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), 
                new EmptyBorder(5, 10, 5, 10)));
        
        return tf;
    }

    private JPanel criarPanelCampo(String label, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(COR_BRANCA);
        
        p.setPreferredSize(new Dimension(100, 70));
        
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_TEXTO);
        p.add(l, BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private JButton criarBotao(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        return btn;
    }
}