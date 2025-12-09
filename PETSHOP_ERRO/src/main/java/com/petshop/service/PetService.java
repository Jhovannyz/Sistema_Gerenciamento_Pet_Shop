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

import com.petshop.model.Pet;

public class PetService {

    private static final String ARQUIVO = "pets.csv";

    public PetService() {
    }

    // --- SALVAR ---
    public void salvar(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(gerarNovoId());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, true))) {
                writer.write(pet.toString());
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            atualizar(pet);
        }
    }

    // --- LISTAR TODOS ---
    public List<Pet> listarTodos() {
        List<Pet> pets = new ArrayList<>();
        File file = new File(ARQUIVO);
        if (!file.exists()) return pets;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    try {
                        pets.add(Pet.fromCSV(linha));
                    } catch (Exception e) {
                        System.err.println("LINHA INVÁLIDA NO ARQUIVO PETS: " + linha);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pets;
    }

    // --- BUSCAR POR ID ---
    public Optional<Pet> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    // --- ATUALIZAR ---
    public void atualizar(Pet petEditado) {
        List<Pet> todos = listarTodos();
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(petEditado.getId())) {
                todos.set(i, petEditado);
                break;
            }
        }
        reescreverArquivo(todos);
    }

    // --- DELETAR ---
    public void deletar(Long id) {
        List<Pet> todos = listarTodos();
        todos.removeIf(p -> p.getId().equals(id));
        reescreverArquivo(todos);
    }

    // --- MÉTODOS AUXILIARES ---

    private void reescreverArquivo(List<Pet> pets) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO, false))) {
            for (Pet p : pets) {
                writer.write(p.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Long gerarNovoId() {
        List<Pet> todos = listarTodos();
        if (todos.isEmpty()) return 1L;
        return todos.get(todos.size() - 1).getId() + 1;
    }
}