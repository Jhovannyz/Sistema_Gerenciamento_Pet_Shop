package com.petshop.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.petshop.model.Pacote;

public class PacoteService {
    
    private static final String ARQUIVO = "pacotes.csv";

    public PacoteService() {
    }
    
    // --- SALVAR PACOTE ---
    public void salvar(Pacote pacote) {
        if (pacote.getId() == null) {
            pacote.setId(gerarNovoId());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
                writer.write(pacote.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            atualizar(pacote);
        }
    }
    
    // --- LISTAR PACOTES ---
    public List<Pacote> listarTodos() {
        List<Pacote> pacotes = new ArrayList<>();
        File file = new File(ARQUIVO);
        if (!file.exists()) return pacotes;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        pacotes.add(Pacote.fromCSV(linha));
                    } catch (Exception e) {
                        System.err.println("LINHA INVÁLIDA NO ARQUIVO PACOTES: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pacotes;
    }
    
    // --- BUSCAR POR ID ---
    public Optional<Pacote> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }
    
    // --- ATUALIZAR ---
    public void atualizar(Pacote pacoteEditado) {
        List<Pacote> todos = listarTodos();
        boolean encontrou = false;

        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(pacoteEditado.getId())) {
                todos.set(i, pacoteEditado);
                encontrou = true;
                break;
            }
        }

        if (encontrou) {
            reescreverArquivo(todos);
        }
    }

    // --- DELETAR ---
    public void deletar(Long id) {
        List<Pacote> todos = listarTodos();
        todos.removeIf(p -> p.getId().equals(id));
        reescreverArquivo(todos);
    }

    // --- MÉTODOS AUXILIARES ---

    private void reescreverArquivo(List<Pacote> pacotes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Pacote p : pacotes) {
                writer.write(p.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Long gerarNovoId() {
        List<Pacote> todos = listarTodos();
        if (todos.isEmpty()) return 1L;
        return todos.get(todos.size() - 1).getId() + 1;
    }
}