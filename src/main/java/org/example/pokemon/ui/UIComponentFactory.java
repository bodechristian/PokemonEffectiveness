package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;

import javax.swing.*;
import java.awt.*;

/**
 * Factory for creating consistent UI components.
 * Eliminates duplication in UI component creation.
 */
public class UIComponentFactory {
    // Font constants
    private static final Font LABEL_FONT_BOLD = new Font("Arial", Font.BOLD, 14);
    private static final Font DROPDOWN_FONT = new Font("Arial", Font.PLAIN, 13);
    
    // Size constants
    private static final Dimension DROPDOWN_SIZE = new Dimension(150, 30);

    /**
     * Creates a JComboBox populated with Pokemon types.
     *
     * @param includeNone whether to include NONE as the first option
     * @return configured type dropdown
     */
    public JComboBox<PokemonType> createTypeDropdown(boolean includeNone) {
        JComboBox<PokemonType> dropdown = new JComboBox<>();
        
        if (includeNone) {
            dropdown.addItem(PokemonType.NONE);
        }
        
        for (PokemonType type : PokemonType.values()) {
            if (type != PokemonType.NONE) {
                dropdown.addItem(type);
            }
        }
        
        dropdown.setPreferredSize(DROPDOWN_SIZE);
        dropdown.setFont(DROPDOWN_FONT);
        
        return dropdown;
    }

    /**
     * Creates a consistently styled label.
     *
     * @param text the label text
     * @return configured label
     */
    public JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT_BOLD);
        return label;
    }
}
