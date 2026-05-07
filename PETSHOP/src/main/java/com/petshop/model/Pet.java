package com.petshop.model;

public class Pet {
    
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private double peso;     
    private Long idCliente;  // VÍNCULO COM O DONO (SALVA APENAS O ID)

    public Pet() {
    }

    // --- GETTERS E SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }

    // --- MÉTODOS CSV ---
    
    // CONVERTE PARA TEXTO
    @Override
    public String toString() {
        // FORMATO: ID;NOME;ESPECIE;RACA;IDADE;PESO;ID_CLIENTE
        return id + ";" + nome + ";" + especie + ";" + raca + ";" + idade + ";" + peso + ";" + idCliente;
    }

    // LÊ DO TEXTO
    public static Pet fromCSV(String linha) {
        String[] d = linha.split(";");
        Pet p = new Pet();
        
        if (d.length >= 7) {
            try {
                p.setId(Long.parseLong(d[0]));
                p.setNome(d[1]);
                p.setEspecie(d[2]);
                p.setRaca(d[3]);
                p.setIdade(Integer.parseInt(d[4]));
                p.setPeso(Double.parseDouble(d[5]));
                p.setIdCliente(Long.parseLong(d[6]));
            } catch (Exception e) {
                System.err.println("ERRO AO LER LINHA DO PET: " + linha);
            }
        }
        return p;
    }
}