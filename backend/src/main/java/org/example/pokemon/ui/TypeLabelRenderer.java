package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;
import org.example.pokemon.ui.util.ColorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static org.example.pokemon.ui.UIConstants.*;

public class TypeLabelRenderer {
    
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
        label.setPreferredSize(new Dimension(TYPE_LABEL_WIDTH, TYPE_LABEL_HEIGHT));
        label.setMinimumSize(new Dimension(TYPE_LABEL_WIDTH, TYPE_LABEL_HEIGHT));
        return label;
    }

    private javax.swing.border.Border createLabelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL)
        );
    }
}
