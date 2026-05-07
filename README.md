# Sistema de Gerenciamento para Pet Shop 🐾

Sistema desenvolvido em Java (Swing) para gerenciamento completo de um Pet Shop, atendendo aos requisitos da disciplina de Programação Orientada a Objetos. O sistema permite o cadastro de clientes, pets, serviços e pacotes promocionais, com persistência de dados em arquivos CSV.

## 📋 Funcionalidades Implementadas

* **Gestão de Clientes:** Cadastro completo com validação de e-mail e telefone, listagem, busca por nome/CPF e exclusão.
* **Gestão de Pets:** Cadastro de animais vinculados a um dono específico (Cliente).
* **Agendamento de Serviços:** Contratação de serviços (Banho, Tosa, etc.) com validação de data futura e cálculo automático de valores.
* **Pacotes Promocionais:** Criação de pacotes de serviços com desconto automático.
* **Dashboard:** Painel principal com menu lateral moderno e atalhos rápidos para as funções.
* **Persistência:** Todos os dados são salvos automaticamente em arquivos de texto (`clientes.csv`, `pets.csv`, `servicos.csv`, `pacotes.csv`).

## 🚀 Tecnologias Utilizadas

* **Linguagem:** Java 11+
* **Interface Gráfica:** Java Swing (AWT/Swing)
* **Armazenamento:** Arquivos de texto (.csv)
* **IDE:** Eclipse

## 📂 Estrutura do Projeto

O código está organizado seguindo o padrão MVC simplificado (Model-View-Service):

* `src/main/java/com/petshop/gui`: Telas e interface visual (JFrames).
* `src/main/java/com/petshop/model`: Classes que representam os dados (Cliente, Pet, Servico, Pacote).
* `src/main/java/com/petshop/service`: Lógica de negócios e manipulação de arquivos CSV.

## ⚙️ Como Rodar

1.  Clone este repositório ou baixe o código fonte.
2.  Importe o projeto no Eclipse (**File > Import > Existing Maven Projects** ou **Existing Projects**).
3.  Localize a classe principal: `com.petshop.PetShopApplication.java` (ou execute diretamente a `TelaMenu.java`).
4.  Clique com o botão direito -> **Run As** -> **Java Application**.
5.  O sistema criará automaticamente os arquivos `.csv` na pasta raiz do projeto conforme novos dados forem cadastrados.

## 👥 Autores

* Giovani Silva 
* Lucas de Jesus
* Fred Gabriel
* Gustavo Barbosa