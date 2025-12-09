package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.petshop.model.Pacote;
import com.petshop.model.Pet;
import com.petshop.model.Servico;
import com.petshop.service.ClienteService;
import com.petshop.service.PacoteService;
import com.petshop.service.PetService;
import com.petshop.service.ServicoService;

public class TelaCadastroServico extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    // CORES DO TEMA
    private final Color COR_FUNDO = new Color(240, 242, 245);
    private final Color COR_BRANCA = Color.WHITE;
    private final Color COR_AZUL = new Color(0, 123, 255);
    private final Color COR_VERDE = new Color(40, 167, 69);
    private final Color COR_VERMELHO = new Color(220, 53, 69);

    // CAMPOS
    private JComboBox<String> comboPets;
    private JComboBox<String> comboTipo;
    private JTextField txtData;
    private JTextField txtValor;
    
    // CAMPO DE BUSCA (NOVO)
    private JTextField txtBusca; 
    
    private JTable tabela;
    private DefaultTableModel tabelaModel;

    private final ServicoService servicoService;
    private final PetService petService;
    private final ClienteService clienteService;
    private final PacoteService pacoteService;

    private List<Pet> listaPetsCache;
    private List<Pacote> listaPacotesCache;

    public TelaCadastroServico(ServicoService servicoService, PetService petService, ClienteService clienteService, PacoteService pacoteService) {
        this.servicoService = servicoService;
        this.petService = petService;
        this.clienteService = clienteService;
        this.pacoteService = pacoteService;

        setTitle("Agendamento de Serviços e Pacotes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 750);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COR_FUNDO);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- CABEÇALHO ---
        JLabel lblTitulo = new JLabel("Contratação de Serviços");
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

        JPanel pPet = criarPanelCampo("Selecione o Pet*:", comboPets = new JComboBox<>());
        comboPets.setBackground(Color.WHITE);
        carregarComboPets();
        panelCampos.add(pPet);

        JPanel pTipo = criarPanelCampo("Serviço ou Pacote*:", comboTipo = new JComboBox<>());
        comboTipo.setBackground(Color.WHITE);
        carregarComboTipos(); 
        comboTipo.addActionListener(e -> atualizarPrecoAutomatico());
        panelCampos.add(pTipo);

        JPanel pData = criarPanelCampo("Data (dd/MM/yyyy)*:", txtData = new JTextField());
        txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panelCampos.add(pData);

        JPanel pValor = criarPanelCampo("Valor (R$)*:", txtValor = new JTextField());
        panelCampos.add(pValor);

        cardForm.add(panelCampos, BorderLayout.CENTER);

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtn.setBackground(COR_BRANCA);
        JButton btnAgendar = criarBotao("Confirmar Agendamento", COR_VERDE);
        btnAgendar.addActionListener(e -> agendarServico());
        panelBtn.add(btnAgendar);
        cardForm.add(panelBtn, BorderLayout.SOUTH);

        panelCentral.add(cardForm, BorderLayout.NORTH);

        // 2. ÁREA DA TABELA (CONTAINER COM BUSCA)
        JPanel panelTabelaContainer = new JPanel(new BorderLayout(0, 5));
        
        // --- BARRA DE BUSCA (ADICIONADA) ---
        JPanel panelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusca.setBackground(COR_FUNDO);
        
        JLabel lblBusca = new JLabel("Buscar Serviço:");
        lblBusca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelBusca.add(lblBusca);
        
        txtBusca = new JTextField(20);
        txtBusca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelBusca.add(txtBusca);
        
        JButton btnBuscar = criarBotao("Pesquisar", COR_AZUL);
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> filtrarServicos()); // CHAMA O FILTRO
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
        String[] colunas = {"ID", "Serviço/Pacote", "Pet", "Dono", "Data", "Valor"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("Agendamentos Futuros"));
        
        panelTabelaContainer.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelTabelaContainer, BorderLayout.CENTER);

        contentPane.add(panelCentral, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel panelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSul.setBackground(COR_FUNDO);
        JButton btnCancelar = criarBotao("Cancelar Serviço", COR_VERMELHO);
        btnCancelar.addActionListener(e -> cancelarServico());
        panelSul.add(btnCancelar);
        contentPane.add(panelSul, BorderLayout.SOUTH);

        carregarTabela(null);
    }

    // ================= LÓGICA =================

    private void filtrarServicos() {
        carregarTabela(txtBusca.getText().trim());
    }

    private void carregarTabela(String filtro) {
        tabelaModel.setRowCount(0);
        List<Servico> lista = servicoService.listarTodos();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Servico s : lista) {
            String nomePet = "Desconhecido";
            String nomeDono = "-";
            
            Optional<Pet> petOpt = petService.buscarPorId(s.getIdPet());
            if (petOpt.isPresent()) {
                nomePet = petOpt.get().getNome();
                if (petOpt.get().getIdCliente() != null) {
                    Optional<Cliente> cliOpt = clienteService.buscarPorId(petOpt.get().getIdCliente());
                    if (cliOpt.isPresent()) nomeDono = cliOpt.get().getNome();
                }
            }

            // LÓGICA DO FILTRO DE BUSCA
            boolean adicionar = true;
            if (filtro != null && !filtro.isEmpty()) {
                String termo = filtro.toLowerCase();
                boolean match = s.getNome().toLowerCase().contains(termo) ||
                                nomePet.toLowerCase().contains(termo) ||
                                nomeDono.toLowerCase().contains(termo);
                if (!match) adicionar = false;
            }

            if (adicionar) {
                tabelaModel.addRow(new Object[]{
                    s.getId(), s.getNome(), nomePet, nomeDono, s.getData().format(fmt), "R$ " + s.getPreco()
                });
            }
        }
    }

    private void carregarComboTipos() {
        comboTipo.removeAllItems();
        comboTipo.addItem("Banho");
        comboTipo.addItem("Tosa");
        comboTipo.addItem("Consulta Veterinária");
        
        listaPacotesCache = pacoteService.listarTodos();
        if (!listaPacotesCache.isEmpty()) {
            for (Pacote p : listaPacotesCache) {
                comboTipo.addItem("[PACOTE] " + p.getNome());
            }
        }
    }

    private void atualizarPrecoAutomatico() {
        String selecionado = (String) comboTipo.getSelectedItem();
        if (selecionado == null) return;

        if (selecionado.startsWith("[PACOTE]")) {
            String nomePacote = selecionado.replace("[PACOTE] ", "");
            for (Pacote p : listaPacotesCache) {
                if (p.getNome().equals(nomePacote)) {
                    txtValor.setText(p.getPrecoComDesconto().toString());
                    return;
                }
            }
        } else {
            txtValor.setText(""); 
        }
    }

    private void agendarServico() {
        if (comboPets.getSelectedIndex() < 0 || listaPetsCache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um Pet.");
            return;
        }

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate dataAgendamento = LocalDate.parse(txtData.getText().trim(), fmt);
            
            if (dataAgendamento.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Erro: A data deve ser futura.");
                return;
            }

            String valorStr = txtValor.getText().replace(",", ".").trim();
            BigDecimal preco = new BigDecimal(valorStr);
            
            String nomeServico = (String) comboTipo.getSelectedItem();
            Pet petSelecionado = listaPetsCache.get(comboPets.getSelectedIndex());

            Servico servico = new Servico();
            servico.setNome(nomeServico); 
            servico.setPreco(preco);      
            servico.setData(dataAgendamento);
            servico.setIdPet(petSelecionado.getId());

            servicoService.salvar(servico);
            JOptionPane.showMessageDialog(this, "Agendado com sucesso!");
            
            // txtData.setText(""); // Mantém a data para facilitar múltiplos agendamentos
            carregarTabela(null);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: Verifique a data e o valor.");
        }
    }

    private void cancelarServico() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento.");
            return;
        }
        Long id = (Long) tabelaModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Cancelar?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            servicoService.cancelar(id);
            carregarTabela(null);
        }
    }

    private void carregarComboPets() {
        comboPets.removeAllItems();
        listaPetsCache = petService.listarTodos();
        for (Pet p : listaPetsCache) {
            String nomeDono = "Sem Dono";
            if (p.getIdCliente() != null) {
                Optional<Cliente> dono = clienteService.buscarPorId(p.getIdCliente());
                if (dono.isPresent()) nomeDono = dono.get().getNome();
            }
            comboPets.addItem(p.getNome() + " (Dono: " + nomeDono + ")");
        }
    }

    private JPanel criarPanelCampo(String label, javax.swing.JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(COR_BRANCA);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        btn.setPreferredSize(new Dimension(200, 35));
        return btn;
    }

    private void estilizarTabela(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COR_AZUL);
        header.setForeground(Color.WHITE);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }
}