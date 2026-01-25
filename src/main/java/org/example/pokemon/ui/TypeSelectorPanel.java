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

import static org.example.pokemon.ui.UIConstants.*;

public class TypeSelectorPanel extends JPanel {
    private final List<TypeSelectorButton> typeButtons;
    private final ButtonGroup buttonGroup;
    private final boolean allowNoneSelection;
    private final List<Consumer<PokemonType>> selectionListeners;
    private PokemonType lastSelectedType = PokemonType.NONE;

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

    private JPanel createGridPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, GRID_COLUMNS, TYPE_PANEL_GAP, TYPE_PANEL_GAP));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        for (PokemonType type : PokemonType.values()) {
            if (type == PokemonType.NONE) {
                continue;
            }

            TypeSelectorButton button = new TypeSelectorButton(type);
            button.addActionListener(e -> handleButtonSelection(button));
            
            typeButtons.add(button);
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
            if (button.getType() == lastSelectedType) {
                // User clicked the already-selected button - deselect it
                buttonGroup.clearSelection();
                lastSelectedType = PokemonType.NONE;
                notifySelectionChanged(PokemonType.NONE);
            } else {
                // Button was selected (different button)
                lastSelectedType = button.getType();
                notifySelectionChanged(button.getType());
            }
        } else {
            // For mandatory type, always notify of the selection
            lastSelectedType = button.getType();
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
        } else {
            lastSelectedType = type;
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
        lastSelectedType = PokemonType.NONE;
    }
    
    /**
     * Disables the button for a specific type to prevent duplicate selection.
     * 
     * @param type the type to disable, or null to enable all buttons
     */
    public void setDisabledType(PokemonType type) {
        for (TypeSelectorButton button : typeButtons) {
            if (type != null && button.getType() == type) {
                // If this button is currently selected, deselect it first
                if (button.isSelected()) {
                    buttonGroup.clearSelection();
                    lastSelectedType = PokemonType.NONE;
                    notifySelectionChanged(PokemonType.NONE);
                }
                button.setEnabled(false);
            } else {
                button.setEnabled(true);
            }
        }
    }
}
