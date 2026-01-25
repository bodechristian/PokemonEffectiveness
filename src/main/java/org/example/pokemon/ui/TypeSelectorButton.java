package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;
import org.example.pokemon.ui.util.ColorUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Objects;

import static org.example.pokemon.ui.UIConstants.*;

public class TypeSelectorButton extends JToggleButton {
    private final PokemonType type;

    /**
     * Creates a type selector button for the given Pokemon type.
     *
     * @param type the Pokemon type this button represents
     */
    public TypeSelectorButton(PokemonType type) {
        super(type.getDisplayName());
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        
        if (type == PokemonType.NONE) {
            throw new IllegalArgumentException("Cannot create selector for NONE type");
        }

        configureAppearance();
        configureSelectionBehavior();
    }

    /**
     * Configures the visual appearance of the button.
     */
    private void configureAppearance() {
        // Override the UI to prevent Look and Feel from overriding our colors
        setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());
        
        setBackground(type.getColor());
        setForeground(ColorUtils.getContrastingTextColor(type.getColor()));
        setFont(LABEL_FONT);
        setFocusPainted(false);
        setOpaque(true);
        setContentAreaFilled(true);
        setBorderPainted(true);
        
        setPreferredSize(new Dimension(TYPE_LABEL_WIDTH, TYPE_LABEL_HEIGHT));
        setMinimumSize(new Dimension(TYPE_LABEL_WIDTH, TYPE_LABEL_HEIGHT));
        
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UNSELECTED_BORDER_COLOR, NORMAL_BORDER_WIDTH),
                new EmptyBorder(PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL)
        ));
    }

    private void configureSelectionBehavior() {
        addItemListener(e -> updateBorderForSelection());
    }

    private void updateBorderForSelection() {
        Color borderColor = isSelected() ? SELECTED_BORDER_COLOR : UNSELECTED_BORDER_COLOR;
        int borderWidth = isSelected() ? SELECTED_BORDER_WIDTH : NORMAL_BORDER_WIDTH;
        
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, borderWidth),
                new EmptyBorder(PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_VERTICAL, PADDING_HORIZONTAL)
        ));
    }

    /**
     * Gets the Pokemon type this button represents.
     *
     * @return the Pokemon type
     */
    public PokemonType getType() {
        return type;
    }

    /**
     * Overrides paintComponent to ensure background color is always painted.
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        
        super.paintComponent(g);
        
        // If disabled, paint a semi-transparent grey overlay and diagonal line AFTER super.paintComponent
        // This ensures the line is drawn on top of the text
        if (!isEnabled()) {
            g.setColor(new Color(220, 220, 220, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw diagonal line from bottom-left to top-right
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setStroke(new BasicStroke(DISABLED_LINE_WIDTH));
            g2d.setColor(new Color(80, 80, 80));
            g2d.drawLine(0, getHeight(), getWidth(), 0);
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setForeground(enabled ? ColorUtils.getContrastingTextColor(type.getColor()) : Color.GRAY);
        repaint();
    }
}
