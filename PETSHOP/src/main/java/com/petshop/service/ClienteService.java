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

import com.petshop.model.Cliente;

public class ClienteService {

    private static final String ARQUIVO = "clientes.csv";

    // CONSTRUTOR VAZIO
    public ClienteService() {
    }

    // --- SALVAR (NOVO OU ATUALIZAÇÃO) ---
    public void salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setId(gerarNovoId());
            // ADICIONA AO FINAL DO ARQUIVO (APPEND = TRUE)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
                writer.write(cliente.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            atualizar(cliente);
        }
    }

    // --- LISTAR TODOS ---
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        File file = new File(ARQUIVO);
        
        // SE O ARQUIVO NÃO EXISTE, RETORNA LISTA VAZIA
        if (!file.exists()) return clientes;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        clientes.add(Cliente.fromCSV(linha));
                    } catch (Exception e) {
                        System.err.println("LINHA INVÁLIDA NO ARQUIVO CLIENTES: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    // --- BUSCAR POR ID ---
    public Optional<Cliente> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    // --- BUSCAR POR CPF ---
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return listarTodos().stream()
                .filter(c -> c.getCpf().equals(cpf))
                .findFirst();
    }

    // --- ATUALIZAR (REESCREVE O ARQUIVO) ---
    public void atualizar(Cliente clienteEditado) {
        List<Cliente> todos = listarTodos();
        boolean encontrou = false;

        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(clienteEditado.getId())) {
                todos.set(i, clienteEditado);
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
        List<Cliente> todos = listarTodos();
        todos.removeIf(c -> c.getId().equals(id));
        reescreverArquivo(todos);
    }

    // --- MÉTODOS AUXILIARES ---

    private void reescreverArquivo(List<Cliente> clientes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Cliente c : clientes) {
                writer.write(c.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Long gerarNovoId() {
        List<Cliente> todos = listarTodos();
        if (todos.isEmpty()) return 1L;
        // PEGA O ÚLTIMO ID DA LISTA E SOMA 1
        return todos.get(todos.size() - 1).getId() + 1;
    }
}