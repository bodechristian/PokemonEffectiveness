package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;
import org.example.pokemon.ui.util.ColorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Renders Pokemon type badges as colored labels.
 * Single Responsibility: Only handles visual rendering of type badges.
 */
public class TypeLabelRenderer {
    private static final int LABEL_PADDING_VERTICAL = 4;
    private static final int LABEL_PADDING_HORIZONTAL = 8;
    private static final int BORDER_WIDTH = 1;
    
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 10);

    /**
     * Creates a color-coded label for a Pokemon type.
     *
     * @param type the Pokemon type to render
     * @return a JLabel configured with the type's color and styling
     */
    public JLabel createTypeLabel(PokemonType type) {
        if (type == null || type == PokemonType.NONE) {
            throw new IllegalArgumentException("Cannot create label for null or NONE type");
        }

        JLabel label = new JLabel(type.getDisplayName());
        label.setOpaque(true);
        label.setBackground(type.getColor());
        label.setForeground(ColorUtils.getContrastingTextColor(type.getColor()));
        label.setFont(LABEL_FONT);
        label.setBorder(createLabelBorder());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Match the size of selector buttons
        label.setPreferredSize(new Dimension(70, 28));
        label.setMinimumSize(new Dimension(70, 28));

        return label;
    }

    /**
     * Creates a consistent border for type labels.
     */
    private javax.swing.border.Border createLabelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, BORDER_WIDTH),
                new EmptyBorder(LABEL_PADDING_VERTICAL, LABEL_PADDING_HORIZONTAL, 
                              LABEL_PADDING_VERTICAL, LABEL_PADDING_HORIZONTAL)
        );
    }
}
