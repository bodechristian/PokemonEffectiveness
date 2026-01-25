package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Objects;

/**
 * A selectable button representing a Pokemon type.
 * Displays the type with its color and handles selection state.
 * Single Responsibility: Rendering and state management for a single type button.
 */
public class TypeSelectorButton extends JToggleButton {
    private static final int BUTTON_PADDING_VERTICAL = 4;
    private static final int BUTTON_PADDING_HORIZONTAL = 8;
    private static final int BORDER_WIDTH = 2;
    private static final int BRIGHTNESS_THRESHOLD = 160;
    
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 10);
    private static final Color SELECTED_BORDER_COLOR = new Color(50, 50, 50);
    private static final Color UNSELECTED_BORDER_COLOR = new Color(150, 150, 150);

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
        setForeground(calculateTextColor(type.getColor()));
        setFont(BUTTON_FONT);
        setFocusPainted(false);
        setOpaque(true);
        setContentAreaFilled(true);
        setBorderPainted(true);
        
        // Set preferred size to ensure button is large enough for text
        setPreferredSize(new Dimension(70, 28));
        setMinimumSize(new Dimension(70, 28));
        
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UNSELECTED_BORDER_COLOR, BORDER_WIDTH),
                new EmptyBorder(BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL,
                               BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL)
        ));
    }

    /**
     * Configures how the button responds to selection changes.
     */
    private void configureSelectionBehavior() {
        addItemListener(e -> updateBorderForSelection());
    }

    /**
     * Updates the border to reflect selection state.
     */
    private void updateBorderForSelection() {
        Color borderColor = isSelected() ? SELECTED_BORDER_COLOR : UNSELECTED_BORDER_COLOR;
        int borderWidth = isSelected() ? BORDER_WIDTH + 1 : BORDER_WIDTH;
        
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(borderColor, borderWidth),
                new EmptyBorder(BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL,
                               BUTTON_PADDING_VERTICAL, BUTTON_PADDING_HORIZONTAL)
        ));
    }

    /**
     * Calculates appropriate text color based on background brightness.
     */
    private Color calculateTextColor(Color backgroundColor) {
        int brightness = (backgroundColor.getRed() 
                        + backgroundColor.getGreen() 
                        + backgroundColor.getBlue()) / 3;
        
        return brightness < BRIGHTNESS_THRESHOLD ? Color.WHITE : Color.BLACK;
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
    }
}
