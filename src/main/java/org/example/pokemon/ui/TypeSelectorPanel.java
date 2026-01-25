package org.example.pokemon.ui;

import org.example.pokemon.model.PokemonType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A panel containing selectable buttons for all Pokemon types.
 * Single Responsibility: Managing type selection UI and notifying listeners of changes.
 */
public class TypeSelectorPanel extends JPanel {
    private static final int GRID_COLUMNS = 4;
    private static final int HORIZONTAL_GAP = 4;
    private static final int VERTICAL_GAP = 4;
    private static final int PANEL_PADDING = 8;
    
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 13);

    private final List<TypeSelectorButton> typeButtons;
    private final ButtonGroup buttonGroup;
    private final boolean allowNoneSelection;
    private final List<Consumer<PokemonType>> selectionListeners;

    /**
     * Creates a type selector panel.
     *
     * @param title the title to display for this panel
     * @param allowNoneSelection whether to allow deselecting (NONE selection)
     */
    public TypeSelectorPanel(String title, boolean allowNoneSelection) {
        this.allowNoneSelection = allowNoneSelection;
        this.typeButtons = new ArrayList<>();
        this.buttonGroup = new ButtonGroup();
        this.selectionListeners = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT
        );
        setBorder(border);

        JPanel gridPanel = createGridPanel();
        add(gridPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the grid panel containing all type buttons.
     */
    private JPanel createGridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, GRID_COLUMNS, HORIZONTAL_GAP, VERTICAL_GAP));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        for (PokemonType type : PokemonType.values()) {
            if (type == PokemonType.NONE) {
                continue;
            }

            TypeSelectorButton button = new TypeSelectorButton(type);
            button.addActionListener(e -> handleButtonSelection(button));
            
            typeButtons.add(button);
            
            // Always use ButtonGroup to prevent multiple selections
            buttonGroup.add(button);
            
            panel.add(button);
        }

        return panel;
    }

    /**
     * Handles button selection and notifies listeners.
     */
    private void handleButtonSelection(TypeSelectorButton button) {
        if (allowNoneSelection) {
            // For optional type, allow clicking the same button to deselect
            if (!button.isSelected()) {
                // Button was deselected (clicked when already selected)
                buttonGroup.clearSelection();
                notifySelectionChanged(PokemonType.NONE);
            } else {
                // Button was selected
                notifySelectionChanged(button.getType());
            }
        } else {
            // For mandatory type, always notify of the selection
            notifySelectionChanged(button.getType());
        }
    }

    /**
     * Adds a listener to be notified when selection changes.
     *
     * @param listener the listener to add
     */
    public void addSelectionListener(Consumer<PokemonType> listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        selectionListeners.add(listener);
    }

    /**
     * Notifies all listeners of a selection change.
     */
    private void notifySelectionChanged(PokemonType selectedType) {
        for (Consumer<PokemonType> listener : selectionListeners) {
            listener.accept(selectedType);
        }
    }

    /**
     * Gets the currently selected type.
     *
     * @return the selected type, or NONE if nothing is selected
     */
    public PokemonType getSelectedType() {
        for (TypeSelectorButton button : typeButtons) {
            if (button.isSelected()) {
                return button.getType();
            }
        }
        return PokemonType.NONE;
    }

    /**
     * Sets the selected type programmatically.
     *
     * @param type the type to select
     */
    public void setSelectedType(PokemonType type) {
        Objects.requireNonNull(type, "Type cannot be null");
        
        for (TypeSelectorButton button : typeButtons) {
            button.setSelected(button.getType() == type);
        }
        
        if (type == PokemonType.NONE) {
            clearSelection();
        }
    }

    /**
     * Clears the current selection (only works if allowNoneSelection is true).
     */
    public void clearSelection() {
        if (!allowNoneSelection) {
            return;
        }
        
        for (TypeSelectorButton button : typeButtons) {
            button.setSelected(false);
        }
    }
}
