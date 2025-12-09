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

import com.petshop.model.Servico;

public class ServicoService {

    private static final String ARQUIVO = "servicos.csv";

    public ServicoService() {
    }

    // --- SALVAR (AGENDAR) ---
    public void salvar(Servico servico) {
        if (servico.getId() == null) {
            servico.setId(gerarNovoId());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
                writer.write(servico.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            atualizar(servico);
        }
    }

    // --- LISTAR TODOS OS AGENDAMENTOS ---
    public List<Servico> listarTodos() {
        List<Servico> lista = new ArrayList<>();
        File file = new File(ARQUIVO);
        if (!file.exists()) return lista;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        lista.add(Servico.fromCSV(linha));
                    } catch (Exception e) {
                        System.err.println("LINHA INVÁLIDA NO ARQUIVO SERVICOS: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // --- BUSCAR POR ID ---
    public Optional<Servico> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    // --- ATUALIZAR ---
    public void atualizar(Servico servicoEditado) {
        List<Servico> todos = listarTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(servicoEditado.getId())) {
                todos.set(i, servicoEditado);
                break;
            }
        }
        reescreverArquivo(todos);
    }

    // --- CANCELAR / DELETAR ---
    public void cancelar(Long id) {
        List<Servico> todos = listarTodos();
        todos.removeIf(s -> s.getId().equals(id));
        reescreverArquivo(todos);
    }

    // --- MÉTODOS AUXILIARES ---

    private void reescreverArquivo(List<Servico> lista) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Servico s : lista) {
                writer.write(s.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Long gerarNovoId() {
        List<Servico> todos = listarTodos();
        if (todos.isEmpty()) return 1L;
        return todos.get(todos.size() - 1).getId() + 1;
    }
}