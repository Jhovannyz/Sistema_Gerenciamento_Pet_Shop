package com.petshop.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Servico {
    
    private Long id;
    private String nome;            // Mudou de 'tipo' para 'nome'
    private BigDecimal preco;       // Mudou de 'valor' (double) para 'preco' (BigDecimal)
    
    // Campos de Agendamento
    private LocalDate data; 
    private Long idPet;     

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Servico() {}

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // O PACOTE ESTÁ PROCURANDO ESTE MÉTODO AQUI:
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public Long getIdPet() { return idPet; }
    public void setIdPet(Long idPet) { this.idPet = idPet; }

    // --- CSV Methods ---
    @Override
    public String toString() {
        String dataStr = (data != null) ? data.format(FMT) : "";
        // Formato: ID;Nome;Preco;Data;IdPet
        return id + ";" + nome + ";" + preco + ";" + dataStr + ";" + idPet;
    }

    public static Servico fromCSV(String linha) {
        String[] d = linha.split(";");
        Servico s = new Servico();
        if (d.length >= 5) {
            try {
                s.setId(Long.parseLong(d[0]));
                s.setNome(d[1]);
                // Converte String para BigDecimal
                if (d[2] != null && !d[2].isEmpty()) {
                    s.setPreco(new BigDecimal(d[2]));
                }
                
                if (!d[3].isEmpty()) {
                    s.setData(LocalDate.parse(d[3], FMT));
                }
                if (!d[4].equals("null")) {
                    s.setIdPet(Long.parseLong(d[4]));
                }
            } catch (Exception e) {
                System.err.println("Erro linha CSV Servico: " + linha);
            }
        }
        return s;
    }
}