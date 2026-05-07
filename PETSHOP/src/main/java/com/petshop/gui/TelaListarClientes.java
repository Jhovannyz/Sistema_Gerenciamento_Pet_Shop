package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.petshop.model.Cliente;
import com.petshop.service.ClienteService;

public class TelaListarClientes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // CORES DO TEMA
    private final Color COR_FUNDO = new Color(240, 242, 245);
    private final Color COR_BRANCA = Color.WHITE;
    private final Color COR_AZUL = new Color(0, 123, 255);
    private final Color COR_VERMELHO = new Color(220, 53, 69);
    private final Color COR_CINZA_CLARO = new Color(230, 230, 230);

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtBusca;
    
    private final ClienteService clienteService;

    // MÉTODO MAIN APENAS PARA TESTES
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                ClienteService service = new ClienteService();
                new TelaListarClientes(service).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public TelaListarClientes(ClienteService clienteService) {
        this.clienteService = clienteService;

        setTitle("Gerenciamento de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COR_FUNDO);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- CABEÇALHO ---
        JLabel lblTitulo = new JLabel("Lista de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // --- PAINEL CENTRAL (TABELA + BUSCA) ---
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setOpaque(false);

        // 1. ÁREA DE BUSCA (CARD BRANCO)
        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelBusca.setBackground(COR_BRANCA);
        panelBusca.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), new EmptyBorder(5, 5, 5, 5)));
        
        JLabel lblBusca = new JLabel("Buscar por Nome/CPF:");
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBusca.add(lblBusca);
        
        txtBusca = new JTextField(25);
        panelBusca.add(txtBusca);
        
        JButton btnBuscar = criarBotao("Filtrar", COR_AZUL);
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> buscarClientes());
        panelBusca.add(btnBuscar);
        
        JButton btnLimpar = criarBotao("Limpar", new Color(108, 117, 125));
        btnLimpar.setPreferredSize(new Dimension(100, 30));
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            carregarDados(null);
        });
        panelBusca.add(btnLimpar);
        
        panelCentral.add(panelBusca, BorderLayout.NORTH);

        // 2. TABELA
        String[] colunas = {"ID", "Nome", "CPF", "E-mail", "Telefone", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        estilizarTabela(table); // APLICA ESTILO BONITO
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COR_BRANCA);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panelCentral.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(panelCentral, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel panelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSul.setBackground(COR_FUNDO);
        
        JButton btnAtualizar = criarBotao("Atualizar Lista", COR_AZUL);
        btnAtualizar.addActionListener(e -> carregarDados(null));
        panelSul.add(btnAtualizar);
        
        JButton btnExcluir = criarBotao("Excluir Selecionado", COR_VERMELHO);
        btnExcluir.addActionListener(e -> excluirClienteSelecionado());
        panelSul.add(btnExcluir);
        
        JButton btnFechar = criarBotao("Fechar", new Color(108, 117, 125));
        btnFechar.addActionListener(e -> dispose());
        panelSul.add(btnFechar);
        
        contentPane.add(panelSul, BorderLayout.SOUTH);

        // CARREGA DADOS INICIAIS
        carregarDados(null);
    }

    // --- MÉTODOS DE LÓGICA ---

    private void carregarDados(String termoBusca) {
        tableModel.setRowCount(0);

        if (clienteService == null) return;

        List<Cliente> lista = clienteService.listarTodos();

        // FILTRO DE BUSCA
        if (termoBusca != null && !termoBusca.isEmpty()) {
            String termo = termoBusca.toLowerCase();
            lista = lista.stream()
                    .filter(c -> c.getNome().toLowerCase().contains(termo) || 
                                 c.getCpf().contains(termo))
                    .collect(Collectors.toList());
        }

        for (Cliente c : lista) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getNome(), c.getCpf(), 
                c.getEmail(), c.getTelefone(), c.getEndereco()
            });
        }
    }
    
    private void buscarClientes() {
        carregarDados(txtBusca.getText());
    }

    private void excluirClienteSelecionado() {
        int linhaSelecionada = table.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) table.getValueAt(linhaSelecionada, 0);
        String nome = (String) table.getValueAt(linhaSelecionada, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o cliente " + nome + "?\nEssa ação não pode ser desfeita.",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                clienteService.deletar(id);
                carregarDados(null);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    // --- MÉTODOS VISUAIS ---

    private JButton criarBotao(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
    }

    private void estilizarTabela(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(COR_CINZA_CLARO);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COR_AZUL);
        header.setForeground(Color.WHITE);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // CENTRALIZA ID
    }
}