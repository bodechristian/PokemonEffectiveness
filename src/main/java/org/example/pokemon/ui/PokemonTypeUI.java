package org.example.pokemon.ui;

import org.example.pokemon.model.EffectivenessMultiplier;
import org.example.pokemon.model.PokemonType;
import org.example.pokemon.service.TypeEffectivenessCalculator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static org.example.pokemon.ui.UIConstants.*;

public class PokemonTypeUI extends JFrame {
    private static final int SELECTOR_SPACING = 20;

    private final TypeEffectivenessCalculator calculator;
    private final TypeLabelRenderer labelRenderer;
    private final UIComponentFactory componentFactory;

    private final TypeSelectorPanel type1Selector;
    private final TypeSelectorPanel type2Selector;
    private final JPanel resultsPanel;

    // Display panels for currently selected types
    private final JPanel type1Display;
    private final JPanel type2Display;

    /**
     * Constructor with dependency injection.
     *
     * @param calculator the type effectiveness calculator
     */
    public PokemonTypeUI(TypeEffectivenessCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "Calculator cannot be null");
        this.labelRenderer = new TypeLabelRenderer();
        this.componentFactory = new UIComponentFactory();

        // Frame setup
        configureFrame();

        // Create UI components
        JPanel mainPanel = createMainPanel();
        this.resultsPanel = createResultsPanel();

        // Create type selectors
        this.type1Selector = new TypeSelectorPanel("Defending Type 1 (Required)", false);
        this.type2Selector = new TypeSelectorPanel("Defending Type 2 (Optional)", true);

        // Create selected type display panels using factory
        this.type1Display = componentFactory.createSelectedTypeDisplayPanel();
        this.type2Display = componentFactory.createSelectedTypeDisplayPanel();

        // Set default selection for type 1
        type1Selector.setSelectedType(PokemonType.NORMAL);

        // Disable the matching type in Type 2 panel
        type2Selector.setDisabledType(PokemonType.NORMAL);

        // Create selector panels with displays using factory
        JPanel type1Container = componentFactory.createSelectorContainer(type1Display, type1Selector);
        JPanel type2Container = componentFactory.createSelectorContainer(type2Display, type2Selector);

        // Create main selectors panel
        JPanel selectorsPanel = createSelectorsPanel();
        selectorsPanel.add(type1Container);
        selectorsPanel.add(componentFactory.createHorizontalSpacing(SELECTOR_SPACING));
        selectorsPanel.add(type2Container);

        // Create separator between selectors and results using factory
        JSeparator separator = componentFactory.createHorizontalSeparator();

        JScrollPane scrollPane = createScrollPane();

        // Assemble layout with separator
        JPanel topPanel = componentFactory.createVerticalPanel();
        topPanel.add(selectorsPanel);
        topPanel.add(componentFactory.createVerticalSpacing(10));
        topPanel.add(separator);
        topPanel.add(componentFactory.createVerticalSpacing(10));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // Add listeners with validation to prevent duplicate type selection
        type1Selector.addSelectionListener(type -> handleType1Selection(type));
        type2Selector.addSelectionListener(type -> handleType2Selection(type));

        // Initial calculation
        updateResults();
    }

    /**
     * Configures the main frame properties.
     */
    private void configureFrame() {
        setTitle("Pokemon Type Effectiveness Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, 600); // Initial height, will be adjusted dynamically
        setLocationRelativeTo(null);
        setResizable(true);
    }

    /**
     * Creates the main panel with padding and layout.
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(PANEL_GAP, PANEL_GAP));
        panel.setBorder(new EmptyBorder(MAIN_PANEL_PADDING, MAIN_PANEL_PADDING, MAIN_PANEL_PADDING, MAIN_PANEL_PADDING));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Creates the panel that will contain both type selectors.
     */
    private JPanel createSelectorsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Creates the results panel.
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Creates a scroll pane for the results.
     */
    private JScrollPane createScrollPane() {
        return componentFactory.createResultsScrollPane(resultsPanel);
    }

    /**
     * Handles Type 1 selection with validation to prevent duplicate types.
     */
    private void handleType1Selection(PokemonType selectedType) {
        // Update Type 2 panel to disable the button matching Type 1
        // This may trigger handleType2Selection if Type 2 gets cleared
        boolean type2WasCleared = type2Selector.getSelectedType() == selectedType;
        type2Selector.setDisabledType(selectedType);

        // Only update results if Type 2 wasn't cleared (to avoid double update)
        // If it was cleared, handleType2Selection will be called automatically
        if (!type2WasCleared) {
            updateResults();
        }
    }

    /**
     * Handles Type 2 selection with validation to prevent duplicate types.
     */
    private void handleType2Selection(PokemonType selectedType) {
        // The button should already be disabled if it matches Type 1,
        // but this is called if the selection changes
        updateResults();
    }

    /**
     * Updates the results panel based on selected types.
     */
    private void updateResults() {
        resultsPanel.removeAll();

        PokemonType type1 = type1Selector.getSelectedType();
        PokemonType type2 = type2Selector.getSelectedType();

        // Ensure type2 is never null
        if (type2 == null) {
            type2 = PokemonType.NONE;
        }

        // Update the selected type displays
        updateSelectedTypeDisplay(type1Display, type1);
        updateSelectedTypeDisplay(type2Display, type2);

        Map<Double, List<PokemonType>> grouped = calculator.groupTypesByEffectiveness(type1, type2);
        displayGroupedResults(grouped);

        resultsPanel.revalidate();
        resultsPanel.repaint();

        // Adjust window height to fit content
        adjustWindowHeight();
    }

    /**
     * Updates a selected type display panel with the current type button.
     */
    private void updateSelectedTypeDisplay(JPanel displayPanel, PokemonType type) {
        displayPanel.removeAll();

        if (type != null && type != PokemonType.NONE) {
            // Create a label that looks like the selected button using factory
            JLabel displayLabel = componentFactory.createSelectedTypeLabel(type);
            displayPanel.add(displayLabel);
        } else {
            // Create an empty placeholder button for "none selected" using factory
            JLabel emptyLabel = componentFactory.createEmptyTypeLabel();
            displayPanel.add(emptyLabel);
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    /**
     * Adjusts the window height to fit all content without extra whitespace.
     */
    private void adjustWindowHeight() {
        // Use invokeLater to ensure layout is complete before calculating size
        SwingUtilities.invokeLater(() -> {
            pack(); // Resizes window to fit preferred size of components
            setSize(WINDOW_WIDTH, getHeight()); // Keep width fixed, use calculated height
        });
    }

    /**
     * Displays the grouped effectiveness results.
     */
    private void displayGroupedResults(Map<Double, List<PokemonType>> grouped) {
        // Use TreeMap to sort by multiplier in descending order
        TreeMap<Double, List<PokemonType>> sorted = new TreeMap<>((a, b) -> Double.compare(b, a));
        sorted.putAll(grouped);

        // Display each group
        for (Map.Entry<Double, List<PokemonType>> entry : sorted.entrySet()) {
            double multiplier = entry.getKey();
            List<PokemonType> types = entry.getValue();

            if (!types.isEmpty()) {
                JPanel groupPanel = createGroupPanel(multiplier, types);
                resultsPanel.add(groupPanel);
                resultsPanel.add(componentFactory.createVerticalSpacing(RESULTS_VERTICAL_SPACING));
            }
        }
    }

    /**
     * Creates a panel for a specific effectiveness group.
     * Uses WrapLayout to ensure types wrap instead of requiring horizontal scroll.
     */
    private JPanel createGroupPanel(double multiplier, List<PokemonType> types) {
        // Create custom panel that properly calculates max size for wrapping
        JPanel panel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE, pref.height);
            }
        };

        // Use WrapLayout instead of FlowLayout for proper wrapping
        panel.setLayout(new WrapLayout(FlowLayout.LEFT, TYPE_PANEL_GAP, TYPE_PANEL_GAP));
        panel.setBackground(Color.WHITE);

        // Create title using value object
        String title = EffectivenessMultiplier.of(multiplier).getDisplayTitle();
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT
        );

        // Add padding around the border using CompoundBorder with EmptyBorder for internal padding
        panel.setBorder(BorderFactory.createCompoundBorder(
                border,
                new EmptyBorder(PANEL_PADDING, PANEL_PADDING,
                                PANEL_PADDING, PANEL_PADDING)
        ));

        // Add type labels using renderer
        for (PokemonType type : types) {
            JLabel typeLabel = labelRenderer.createTypeLabel(type);
            panel.add(typeLabel);
        }

        return panel;
    }
}
