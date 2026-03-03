package org.example;

import org.example.pokemon.service.TypeEffectivenessCalculator;
import org.example.pokemon.ui.PokemonTypeUI;

import javax.swing.*;

/**
 * Main Class - Entry point for Pokemon Type Effectiveness Calculator.
 */
public class Main {
    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {


        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Dependency injection: create calculator with loaded data
            TypeEffectivenessCalculator calculator = TypeEffectivenessCalculator.createDefault();
            
            // Inject calculator into UI
            PokemonTypeUI ui = new PokemonTypeUI(calculator);
            ui.setVisible(true);
        });
    }
}
