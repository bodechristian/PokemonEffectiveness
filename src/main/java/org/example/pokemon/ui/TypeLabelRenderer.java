package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Renders Pokemon type badges as colored labels.
 * Single Responsibility: Only handles visual rendering of type badges.
 */
public class TypeLabelRenderer {
    private static final int BRIGHTNESS_THRESHOLD = 160;
    private static final int LABEL_PADDING_VERTICAL = 5;
    private static final int LABEL_PADDING_HORIZONTAL = 10;
    private static final int BORDER_WIDTH = 1;
    
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);

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
        label.setForeground(calculateTextColor(type.getColor()));
        label.setFont(LABEL_FONT);
        label.setBorder(createLabelBorder());
        label.setHorizontalAlignment(SwingConstants.CENTER);

        return label;
    }

    /**
     * Calculates appropriate text color based on background brightness.
     * Uses white text for dark backgrounds, black for light backgrounds.
     */
    private Color calculateTextColor(Color backgroundColor) {
        int brightness = (backgroundColor.getRed() 
                        + backgroundColor.getGreen() 
                        + backgroundColor.getBlue()) / 3;
        
        return brightness < BRIGHTNESS_THRESHOLD ? Color.WHITE : Color.BLACK;
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
