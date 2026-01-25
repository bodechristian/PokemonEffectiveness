package org.example.pokemon.ui;

import org.example.pokemon.model.EffectivenessMultiplier;
import org.example.pokemon.model.PokemonType;
import org.example.pokemon.service.TypeEffectivenessCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Main UI for the Pokemon Type Effectiveness Calculator.
 * Follows Single Responsibility: Only handles UI coordination and layout.
 */
public class PokemonTypeUI extends JFrame {
    // UI dimension constants
    private static final int WINDOW_WIDTH = 700;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PANEL_PADDING = 15;
    private static final int PANEL_GAP = 10;
    private static final int VERTICAL_SPACING = 10;
    
    // Layout constants
    private static final int FLOW_HORIZONTAL_GAP = 15;
    private static final int FLOW_VERTICAL_GAP = 10;
    private static final int TYPE_PANEL_GAP = 8;
    
    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);

    private final TypeEffectivenessCalculator calculator;
    private final TypeLabelRenderer labelRenderer;
    private final UIComponentFactory componentFactory;
    
    private final JComboBox<PokemonType> type1Dropdown;
    private final JComboBox<PokemonType> type2Dropdown;
    private final JPanel resultsPanel;

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
        JPanel topPanel = createTopPanel();
        this.resultsPanel = createResultsPanel();
        
        this.type1Dropdown = componentFactory.createTypeDropdown(false);
        this.type2Dropdown = componentFactory.createTypeDropdown(true);
        
        setupTopPanel(topPanel);
        
        JScrollPane scrollPane = createScrollPane();

        // Assemble layout
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // Add listeners
        type1Dropdown.addActionListener(e -> updateResults());
        type2Dropdown.addActionListener(e -> updateResults());

        // Initial calculation
        updateResults();
    }

    /**
     * Configures the main frame properties.
     */
    private void configureFrame() {
        setTitle("Pokemon Type Effectiveness Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
    }

    /**
     * Creates the main panel with padding and layout.
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(PANEL_GAP, PANEL_GAP));
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Creates the top panel for type selection.
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, FLOW_HORIZONTAL_GAP, FLOW_VERTICAL_GAP));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    /**
     * Sets up the top panel with dropdowns.
     */
    private void setupTopPanel(JPanel topPanel) {
        topPanel.add(componentFactory.createLabel("Defending Type 1:"));
        topPanel.add(type1Dropdown);
        topPanel.add(componentFactory.createLabel("Defending Type 2:"));
        topPanel.add(type2Dropdown);
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
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    /**
     * Updates the results panel based on selected types.
     */
    private void updateResults() {
        resultsPanel.removeAll();

        PokemonType type1 = (PokemonType) type1Dropdown.getSelectedItem();
        PokemonType type2 = (PokemonType) type2Dropdown.getSelectedItem();

        if (type1 == null) {
            return;
        }

        // Ensure type2 is never null
        if (type2 == null) {
            type2 = PokemonType.NONE;
        }

        Map<Double, List<PokemonType>> grouped = calculator.groupTypesByEffectiveness(type1, type2);
        displayGroupedResults(grouped);

        resultsPanel.revalidate();
        resultsPanel.repaint();
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
                resultsPanel.add(Box.createVerticalStrut(VERTICAL_SPACING));
            }
        }
    }

    /**
     * Creates a panel for a specific effectiveness group.
     */
    private JPanel createGroupPanel(double multiplier, List<PokemonType> types) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, TYPE_PANEL_GAP, TYPE_PANEL_GAP));
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
        panel.setBorder(border);

        // Add type labels using renderer
        for (PokemonType type : types) {
            JLabel typeLabel = labelRenderer.createTypeLabel(type);
            panel.add(typeLabel);
        }

        return panel;
    }
}
