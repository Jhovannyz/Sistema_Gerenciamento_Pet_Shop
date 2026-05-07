package com.petshop.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente {
    
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String endereco;
    
    // MANTEMOS A LISTA DE PETS APENAS PARA USO EM MEMÓRIA SE PRECISAR,
    // MAS ELA NÃO É SALVA NO CSV DO CLIENTE (O VÍNCULO É FEITO PELO ID NO ARQUIVO DE PETS).
    private List<Pet> pets;

    public Cliente() {
        this.pets = new ArrayList<>();
    }

    // --- GETTERS E SETTERS ---

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    // --- MÉTODOS PARA A LISTA DE PETS ---

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
    
    public void addPet(Pet pet) {
        this.pets.add(pet);
    }
    
    // --- MÉTODOS CSV (PERSISTÊNCIA) ---
    
    // CONVERTE O OBJETO PARA UMA LINHA DE CSV
    @Override
    public String toString() {
        // FORMATO: ID;NOME;CPF;EMAIL;TELEFONE;ENDERECO
        return id + ";" + nome + ";" + cpf + ";" + email + ";" + telefone + ";" + endereco;
    }

    // CRIA UM CLIENTE A PARTIR DE UMA LINHA DO CSV
    public static Cliente fromCSV(String linha) {
        String[] dados = linha.split(";");
        Cliente c = new Cliente();
        
        // TRATAMENTO DE ERRO CASO A LINHA ESTEJA INCOMPLETA
        if (dados.length >= 6) {
            try {
                c.setId(Long.parseLong(dados[0]));
                c.setNome(dados[1]);
                c.setCpf(dados[2]);
                c.setEmail(dados[3]);
                c.setTelefone(dados[4]);
                c.setEndereco(dados[5]);
            } catch (Exception e) {
                System.err.println("ERRO AO LER LINHA DO CLIENTE: " + linha);
            }
        }
        return c;
    }
}