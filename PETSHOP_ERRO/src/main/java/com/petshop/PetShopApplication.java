package com.petshop;

import java.awt.EventQueue;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.petshop.gui.TelaMenu;

public class PetShopApplication {

    // PONTO DE PARTIDA DO SISTEMA
    public static void main(String[] args) {
        
        // TENTA APLICAR O VISUAL DO SISTEMA OPERACIONAL (WINDOWS)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // IGNORA SE NÃO CONSEGUIR
        }

        // INICIA A INTERFACE GRÁFICA NA THREAD CORRETA
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    TelaMenu frame = new TelaMenu();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ERRO AO INICIAR SISTEMA: " + e.getMessage());
                }
            }
        });
    }
}