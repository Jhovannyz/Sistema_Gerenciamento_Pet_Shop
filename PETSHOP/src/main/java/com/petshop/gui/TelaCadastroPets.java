package com.petshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import com.petshop.model.Pet;
import com.petshop.service.ClienteService;
import com.petshop.service.PetService;

public class TelaCadastroPets extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    // CORES DO TEMA
    private final Color COR_FUNDO = new Color(240, 242, 245);
    private final Color COR_BRANCA = Color.WHITE;
    private final Color COR_AZUL = new Color(0, 123, 255);
    private final Color COR_VERDE = new Color(40, 167, 69);
    private final Color COR_VERMELHO = new Color(220, 53, 69);
    private final Color COR_TEXTO = new Color(50, 50, 50);
    
    // FONTES
    private final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 24);

    // CAMPOS
    private JTextField txtNome;
    private JTextField txtEspecie;
    private JTextField txtRaca;
    private JTextField txtIdade;
    private JTextField txtPeso;
    private JComboBox<String> comboDonos; 
    private JTextField txtBusca;
    
    private JTable tabela;
    private DefaultTableModel tabelaModel;

    // SERVICES
    private final PetService petService;
    private final ClienteService clienteService;

    // CONTROLE
    private Long idPetEdicao = null;
    private List<Cliente> listaClientesCache;

    public TelaCadastroPets(PetService petService, ClienteService clienteService) {
        this.petService = petService;
        this.clienteService = clienteService;

        setTitle("PetShop System - Gerenciamento de Pets");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 950, 750);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBackground(COR_FUNDO);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPane.setLayout(new BorderLayout(0, 20));
        setContentPane(contentPane);

        // --- CABEÇALHO ---
        JPanel panelTopo = new JPanel(new BorderLayout());
        panelTopo.setBackground(COR_FUNDO);
        
        JLabel lblTitulo = new JLabel("Gestão de Pets");
        lblTitulo.setForeground(COR_TEXTO);
        lblTitulo.setFont(FONTE_TITULO);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTopo.add(lblTitulo, BorderLayout.CENTER);
        
        contentPane.add(panelTopo, BorderLayout.NORTH);

        // --- ÁREA CENTRAL ---
        JPanel panelCentral = new JPanel(new BorderLayout(0, 15));
        panelCentral.setOpaque(false);

        // 1. CARD FORMULÁRIO
        JPanel cardFormulario = new JPanel(new BorderLayout());
        cardFormulario.setBackground(COR_BRANCA);
        cardFormulario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 10, 20)));
        
        JLabel lblSubTituloForm = new JLabel("Dados do Animal");
        lblSubTituloForm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSubTituloForm.setForeground(COR_AZUL);
        lblSubTituloForm.setBorder(new EmptyBorder(0, 0, 15, 0));
        cardFormulario.add(lblSubTituloForm, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(3, 4, 15, 15)); 
        panelCampos.setBackground(COR_BRANCA);
        
        panelCampos.add(criarPanelCampo("Nome do Pet*:", txtNome = criarTextField()));
        panelCampos.add(criarPanelCampo("Espécie (Cão/Gato)*:", txtEspecie = criarTextField()));
        panelCampos.add(criarPanelCampo("Raça:", txtRaca = criarTextField()));
        panelCampos.add(criarPanelCampo("Idade (anos)*:", txtIdade = criarTextField()));
        panelCampos.add(criarPanelCampo("Peso (kg)*:", txtPeso = criarTextField()));
        
        // COMBO DONOS
        JPanel panelDono = new JPanel(new BorderLayout(0, 5));
        panelDono.setBackground(COR_BRANCA);
        JLabel lblDono = new JLabel("Dono (Cliente)*:");
        lblDono.setFont(new Font("Segoe UI", Font.BOLD, 12));
        comboDonos = new JComboBox<>();
        comboDonos.setFont(FONTE_PADRAO);
        comboDonos.setBackground(Color.WHITE);
        carregarComboClientes();
        panelDono.add(lblDono, BorderLayout.NORTH);
        panelDono.add(comboDonos, BorderLayout.CENTER);
        panelCampos.add(panelDono);

        cardFormulario.add(panelCampos, BorderLayout.CENTER);

        // BOTÕES DO FORMULÁRIO
        JPanel panelBotoesForm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotoesForm.setBackground(COR_BRANCA);
        
        JButton btnLimpar = criarBotao("Limpar", new Color(108, 117, 125));
        btnLimpar.addActionListener(e -> limparCampos());
        
        JButton btnSalvar = criarBotao("Salvar Pet", COR_VERDE);
        btnSalvar.addActionListener(e -> salvarOuAtualizar());
        
        panelBotoesForm.add(btnLimpar);
        panelBotoesForm.add(btnSalvar);
        
        cardFormulario.add(panelBotoesForm, BorderLayout.SOUTH);
        panelCentral.add(cardFormulario, BorderLayout.NORTH);

        // 2. CARD TABELA
        JPanel cardTabela = new JPanel(new BorderLayout(0, 10));
        cardTabela.setBackground(COR_BRANCA);
        cardTabela.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 20, 15, 20)));

        // BARRA DE BUSCA
        JPanel panelBusca = new JPanel(new BorderLayout(10, 0));
        panelBusca.setBackground(COR_BRANCA);
        
        JLabel lblIconeBusca = new JLabel("Buscar:");
        lblIconeBusca.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        txtBusca = criarTextField();
        txtBusca.setPreferredSize(new Dimension(300, 35));
        
        JButton btnBuscar = criarBotao("Pesquisar", COR_AZUL);
        btnBuscar.addActionListener(e -> filtrarPets());
        
        JButton btnVerTodos = criarBotao("Todos", new Color(23, 162, 184));
        btnVerTodos.addActionListener(e -> { txtBusca.setText(""); carregarTabela(null); });

        JPanel containerBuscaInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        containerBuscaInput.setBackground(COR_BRANCA);
        containerBuscaInput.add(lblIconeBusca);
        containerBuscaInput.add(txtBusca);
        containerBuscaInput.add(btnBuscar);
        containerBuscaInput.add(btnVerTodos);
        
        panelBusca.add(containerBuscaInput, BorderLayout.WEST);
        cardTabela.add(panelBusca, BorderLayout.NORTH);

        // CONFIGURAÇÃO TABELA
        String[] colunas = {"ID", "Nome", "Espécie", "Raça", "Idade", "Peso", "Dono"};
        tabelaModel = new DefaultTableModel(colunas, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(tabelaModel);
        estilizarTabela(tabela);
        
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) carregarDadosParaEdicao(tabela.getSelectedRow());
            }
        });

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(COR_BRANCA);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        cardTabela.add(scrollPane, BorderLayout.CENTER);

        panelCentral.add(cardTabela, BorderLayout.CENTER);
        contentPane.add(panelCentral, BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel panelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelRodape.setBackground(COR_FUNDO);
        
        JButton btnExcluir = criarBotao("Excluir Selecionado", COR_VERMELHO);
        btnExcluir.addActionListener(e -> excluirPet());
        panelRodape.add(btnExcluir);

        contentPane.add(panelRodape, BorderLayout.SOUTH);

        carregarTabela(null);
    }

    // ================= MÉTODOS VISUAIS =================

    private JTextField criarTextField() {
        JTextField tf = new JTextField();
        tf.setFont(FONTE_PADRAO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)), 
                new EmptyBorder(5, 8, 5, 8))); 
        return tf;
    }
    
    private JPanel criarPanelCampo(String label, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(COR_BRANCA);
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

    private void estilizarTabela(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COR_AZUL);
        header.setForeground(Color.WHITE);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); 
    }

    // ================= LÓGICA DO SISTEMA =================

    private void filtrarPets() {
        carregarTabela(txtBusca.getText().trim());
    }

    private void carregarTabela(String filtro) {
        tabelaModel.setRowCount(0);
        List<Pet> pets = petService.listarTodos();

        for (Pet p : pets) {
            String nomeDono = "Desconhecido";
            if (p.getIdCliente() != null) {
                Optional<Cliente> dono = clienteService.buscarPorId(p.getIdCliente());
                if (dono.isPresent()) nomeDono = dono.get().getNome();
            }

            boolean adicionar = true;
            if (filtro != null && !filtro.isEmpty()) {
                String termo = filtro.toLowerCase();
                boolean match = p.getNome().toLowerCase().contains(termo) ||
                                p.getEspecie().toLowerCase().contains(termo) ||
                                p.getRaca().toLowerCase().contains(termo) ||
                                nomeDono.toLowerCase().contains(termo);
                if (!match) adicionar = false;
            }

            if (adicionar) {
                tabelaModel.addRow(new Object[]{
                    p.getId(), p.getNome(), p.getEspecie(), p.getRaca(), 
                    p.getIdade(), p.getPeso(), nomeDono
                });
            }
        }
    }

    private void salvarOuAtualizar() {
        if (txtNome.getText().trim().isEmpty() || txtEspecie.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha Nome e Espécie.");
            return;
        }
        
        if (listaClientesCache.isEmpty() || comboDonos.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um Cliente válido.");
            return;
        }

        try {
            int idade = Integer.parseInt(txtIdade.getText().trim());
            double peso = Double.parseDouble(txtPeso.getText().trim());
            
            int indexSelecionado = comboDonos.getSelectedIndex();
            Cliente donoSelecionado = listaClientesCache.get(indexSelecionado);

            Pet pet = new Pet();
            if (idPetEdicao != null) pet.setId(idPetEdicao);
            
            pet.setNome(txtNome.getText().trim());
            pet.setEspecie(txtEspecie.getText().trim());
            pet.setRaca(txtRaca.getText().trim());
            pet.setIdade(idade);
            pet.setPeso(peso);
            pet.setIdCliente(donoSelecionado.getId()); 

            if (idPetEdicao == null) {
                petService.salvar(pet);
                JOptionPane.showMessageDialog(this, "Pet cadastrado com sucesso!");
            } else {
                petService.atualizar(pet);
                JOptionPane.showMessageDialog(this, "Pet atualizado!");
            }
            limparCampos();
            carregarTabela(null);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Idade e Peso devem ser numéricos.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void excluirPet() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pet na tabela.");
            return;
        }
        Long id = (Long) tabelaModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Excluir este pet?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            petService.deletar(id);
            carregarTabela(null);
            limparCampos();
        }
    }

    private void carregarDadosParaEdicao(int row) {
        Long id = (Long) tabelaModel.getValueAt(row, 0);
        Optional<Pet> opt = petService.buscarPorId(id);
        
        if (opt.isPresent()) {
            Pet p = opt.get();
            idPetEdicao = p.getId();
            txtNome.setText(p.getNome());
            txtEspecie.setText(p.getEspecie());
            txtRaca.setText(p.getRaca());
            txtIdade.setText(String.valueOf(p.getIdade()));
            txtPeso.setText(String.valueOf(p.getPeso()));
            
            for (int i = 0; i < listaClientesCache.size(); i++) {
                if (listaClientesCache.get(i).getId().equals(p.getIdCliente())) {
                    comboDonos.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void limparCampos() {
        txtNome.setText(""); txtEspecie.setText(""); txtRaca.setText("");
        txtIdade.setText(""); txtPeso.setText(""); txtBusca.setText("");
        if (comboDonos.getItemCount() > 0) comboDonos.setSelectedIndex(0);
        idPetEdicao = null;
    }
    
    private void carregarComboClientes() {
        comboDonos.removeAllItems();
        listaClientesCache = clienteService.listarTodos(); 
        if (listaClientesCache.isEmpty()) {
            comboDonos.addItem("Nenhum cliente cadastrado");
            comboDonos.setEnabled(false);
        } else {
            comboDonos.setEnabled(true);
            for (Cliente c : listaClientesCache) {
                comboDonos.addItem(c.getId() + " - " + c.getNome());
            }
        }
    }
}