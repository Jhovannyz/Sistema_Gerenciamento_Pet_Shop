package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;

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

import com.petshop.model.Pacote;
import com.petshop.service.PacoteService;

public class TelaCadastroPacote extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // CORES (MANTENDO PADRÃO VISUAL)
    private final Color COR_FUNDO = new Color(240, 242, 245);
    private final Color COR_BRANCA = Color.WHITE;
    private final Color COR_ROXA = new Color(111, 66, 193); 
    private final Color COR_VERMELHO = new Color(220, 53, 69);
    private final Color COR_AZUL = new Color(0, 123, 255);

    // CAMPOS
    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtPreco;
    private JTextField txtDesconto;
    
    // CAMPO DE BUSCA
    private JTextField txtBusca; 
    
    private JTable tabela;
    private DefaultTableModel tabelaModel;

    private final PacoteService pacoteService;

    public TelaCadastroPacote(PacoteService pacoteService) {
        this.pacoteService = pacoteService;

        setTitle("Gestão de Pacotes Promocionais");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 850, 650);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COR_FUNDO);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- CABEÇALHO ---
        JLabel lblTitulo = new JLabel("Cadastro de Pacotes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitulo, BorderLayout.NORTH);

        // --- CENTRO ---
        JPanel panelCentral = new JPanel(new BorderLayout(0, 15));
        panelCentral.setOpaque(false);

        // 1. FORMULÁRIO
        JPanel cardForm = new JPanel(new BorderLayout());
        cardForm.setBackground(COR_BRANCA);
        cardForm.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), new EmptyBorder(20, 20, 10, 20)));

        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 20, 20));
        panelCampos.setBackground(COR_BRANCA);

        panelCampos.add(criarPanelCampo("Nome do Pacote*:", txtNome = new JTextField()));
        panelCampos.add(criarPanelCampo("Descrição (Ex: Banho + Tosa)*:", txtDescricao = new JTextField()));
        panelCampos.add(criarPanelCampo("Preço Bruto (R$)*:", txtPreco = new JTextField()));
        panelCampos.add(criarPanelCampo("Desconto (%):", txtDesconto = new JTextField()));

        cardForm.add(panelCampos, BorderLayout.CENTER);

        // BOTÃO SALVAR
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.setBackground(COR_BRANCA);
        JButton btnSalvar = criarBotao("Salvar Pacote", COR_ROXA);
        btnSalvar.addActionListener(e -> salvarPacote());
        panelBtn.add(btnSalvar);
        cardForm.add(panelBtn, BorderLayout.SOUTH);

        panelCentral.add(cardForm, BorderLayout.NORTH);

        // 2. ÁREA DA TABELA (COM BUSCA)
        JPanel panelTabelaContainer = new JPanel(new BorderLayout(0, 5));
        
        // --- BARRA DE BUSCA (ADICIONADA) ---
        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusca.setBackground(COR_FUNDO);
        
        JLabel lblBusca = new JLabel("Buscar Pacote:");
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBusca.add(lblBusca);
        
        txtBusca = new JTextField(20);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelBusca.add(txtBusca);
        
        JButton btnBuscar = criarBotao("Pesquisar", COR_AZUL);
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> filtrarPacotes());
        panelBusca.add(btnBuscar);
        
        JButton btnLimparBusca = criarBotao("Ver Todos", new Color(108, 117, 125));
        btnLimparBusca.setPreferredSize(new Dimension(100, 30));
        btnLimparBusca.addActionListener(e -> { 
            txtBusca.setText(""); 
            carregarTabela(null); 
        });
        panelBusca.add(btnLimparBusca);
        
        panelTabelaContainer.add(panelBusca, BorderLayout.NORTH);

        // CONFIGURAÇÃO DA TABELA
        String[] colunas = {"ID", "Pacote", "Descrição", "Preço Original", "Desconto", "Preço Final"};
        tabelaModel = new DefaultTableModel(colunas, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabela = new JTable(tabelaModel);
        estilizarTabela(tabela);

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(COR_BRANCA);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pacotes Ativos"));
        
        panelTabelaContainer.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelTabelaContainer, BorderLayout.CENTER);

        contentPane.add(panelCentral, BorderLayout.CENTER);

        // RODAPÉ
        JPanel panelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSul.setBackground(COR_FUNDO);
        JButton btnExcluir = criarBotao("Excluir Pacote", COR_VERMELHO);
        btnExcluir.addActionListener(e -> excluirPacote());
        panelSul.add(btnExcluir);
        contentPane.add(panelSul, BorderLayout.SOUTH);

        carregarTabela(null);
    }

    // --- LÓGICA ---

    private void filtrarPacotes() {
        carregarTabela(txtBusca.getText().trim());
    }

    private void carregarTabela(String filtro) {
        tabelaModel.setRowCount(0);
        List<Pacote> lista = pacoteService.listarTodos();
        
        for (Pacote p : lista) {
            boolean adicionar = true;
            if (filtro != null && !filtro.isEmpty()) {
                String termo = filtro.toLowerCase();
                boolean match = p.getNome().toLowerCase().contains(termo) ||
                                p.getDescricao().toLowerCase().contains(termo);
                if (!match) adicionar = false;
            }

            if (adicionar) {
                tabelaModel.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getDescricao(), 
                    "R$ " + p.getPrecoTotal(), 
                    p.getDesconto() + "%",
                    "R$ " + p.getPrecoComDesconto()
                });
            }
        }
    }

    private void salvarPacote() {
        if(txtNome.getText().trim().isEmpty() || txtPreco.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Preço são obrigatórios.");
            return;
        }

        try {
            Pacote pacote = new Pacote();
            pacote.setNome(txtNome.getText());
            pacote.setDescricao(txtDescricao.getText());
            
            // CORREÇÃO: USANDO VALUEOF
            double preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
            pacote.setPrecoTotal(BigDecimal.valueOf(preco)); 
            
            String descStr = txtDesconto.getText().replace(",", ".").trim();
            if(descStr.isEmpty()) descStr = "0";
            pacote.setDesconto(new BigDecimal(descStr));

            pacoteService.salvar(pacote);
            
            JOptionPane.showMessageDialog(this, "Pacote salvo com sucesso!");
            limparCampos();
            carregarTabela(null);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro no valor numérico: " + ex.getMessage());
        }
    }

    private void excluirPacote() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote.");
            return;
        }
        Long id = (Long) tabelaModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Excluir?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            pacoteService.deletar(id);
            carregarTabela(null);
        }
    }

    private void limparCampos() {
        txtNome.setText(""); txtDescricao.setText(""); 
        txtPreco.setText(""); txtDesconto.setText("");
        txtBusca.setText("");
    }

    // --- ESTILO VISUAL ---
    private JPanel criarPanelCampo(String label, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(COR_BRANCA);
        p.add(new JLabel(label), BorderLayout.NORTH);
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
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }

    private void estilizarTabela(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COR_ROXA);
        header.setForeground(Color.WHITE);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
    }
}