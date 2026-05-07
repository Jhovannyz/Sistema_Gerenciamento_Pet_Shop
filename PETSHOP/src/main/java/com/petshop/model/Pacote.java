package com.petshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pacote {
    
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal desconto;
    private List<Servico> servicos;
    private BigDecimal precoTotal;

    public Pacote() {
        this.servicos = new ArrayList<>();
        this.precoTotal = BigDecimal.ZERO;
        this.desconto = BigDecimal.ZERO;
    }

    // --- Getters e Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public void setServicos(List<Servico> servicos) {
        this.servicos = servicos;
        // Se a lista vier preenchida, recalculamos. 
        // Se for nula, inicializamos para evitar erros.
        if (this.servicos == null) {
            this.servicos = new ArrayList<>();
        } else {
            calcularPrecoTotal();
        }
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    // IMPORTANTE: Deixei PUBLIC para que a TelaCadastroPacote consiga 
    // definir o preço manual que digitamos lá.
    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    // --- Métodos de Lógica ---

    public void adicionarServico(Servico servico) {
        if (this.servicos == null) this.servicos = new ArrayList<>();
        this.servicos.add(servico);
        calcularPrecoTotal();
    }

    public void removerServico(Servico servico) {
        if (this.servicos != null) {
            this.servicos.remove(servico);
            calcularPrecoTotal();
        }
    }
 // Recalcula o valor somando os serviços
    private void calcularPrecoTotal() {
        if (servicos == null || servicos.isEmpty()) {
            // Se não tem serviços, não faz nada (mantém o preço manual se houver)
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        // Laço 'for-each' simples e à prova de erros
        for (Servico s : servicos) {
            BigDecimal valorServico = s.getPreco();
            
            // Proteção contra valor nulo
            if (valorServico == null) {
                valorServico = BigDecimal.ZERO;
            }
            
            total = total.add(valorServico);
        }
        
        this.setPrecoTotal(total);
    }

    public BigDecimal getPrecoComDesconto() {
        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) == 0) {
            return getPrecoTotal();
        }
        // Aplica % de desconto
        BigDecimal fatorDesconto = desconto.divide(new BigDecimal("100"));
        BigDecimal valorDoDesconto = getPrecoTotal().multiply(fatorDesconto);
        
        return getPrecoTotal().subtract(valorDoDesconto);
    }

    // --- MÉTODOS CSV ---

    @Override
    public String toString() {
        // Trata a lista de serviços para evitar NullPointerException
        String idsServicos = "";
        if (servicos != null && !servicos.isEmpty()) {
            idsServicos = servicos.stream()
                    .map(s -> s.getId().toString())
                    .collect(Collectors.joining("|"));
        }

        // Formato: ID;Nome;Descricao;Desconto;PrecoTotal;ListaDeIDs
        return id + ";" + 
               nome + ";" + 
               descricao + ";" + 
               (desconto != null ? desconto : "0") + ";" + 
               (precoTotal != null ? precoTotal : "0") + ";" + 
               idsServicos;
    }

    public static Pacote fromCSV(String linha) {
        String[] d = linha.split(";");
        Pacote p = new Pacote();
        
        if (d.length >= 5) {
            try {
                p.setId(Long.parseLong(d[0]));
                p.setNome(d[1]);
                p.setDescricao(d[2]);
                p.setDesconto(new BigDecimal(d[3]));
                p.setPrecoTotal(new BigDecimal(d[4])); // Lê o preço salvo no arquivo

                // Se houver serviços vinculados (coluna 6, índice 5)
                if (d.length > 5 && !d[5].isEmpty()) {
                    String[] ids = d[5].split("\\|"); 
                    for (String idStr : ids) {
                        try {
                            Servico sStub = new Servico();
                            sStub.setId(Long.parseLong(idStr));
                            p.getServicos().add(sStub);
                        } catch (NumberFormatException e) {
                            // Ignora ID inválido
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao ler pacote CSV: " + linha);
            }
        }
        return p;
    }
}