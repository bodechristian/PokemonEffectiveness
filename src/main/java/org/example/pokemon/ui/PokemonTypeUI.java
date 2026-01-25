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
    private static final int WINDOW_WIDTH = 720;
    private static final int PANEL_PADDING = 15;
    private static final int PANEL_GAP = 10;
    private static final int VERTICAL_SPACING = 3;
    private static final int SELECTOR_SPACING = 20;
    
    // Layout constants for result groups - use WrapLayout for wrapping
    private static final int TYPE_PANEL_GAP = 4;
    private static final int GROUP_PANEL_PADDING = 8;
    
    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);

    private final TypeEffectivenessCalculator calculator;
    private final TypeLabelRenderer labelRenderer;
    
    private final TypeSelectorPanel type1Selector;
    private final TypeSelectorPanel type2Selector;
    private final JPanel resultsPanel;

    /**
     * Constructor with dependency injection.
     *
     * @param calculator the type effectiveness calculator
     */
    public PokemonTypeUI(TypeEffectivenessCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "Calculator cannot be null");
        this.labelRenderer = new TypeLabelRenderer();

        // Frame setup
        configureFrame();

        // Create UI components
        JPanel mainPanel = createMainPanel();
        JPanel selectorsPanel = createSelectorsPanel();
        this.resultsPanel = createResultsPanel();
        
        // Create type selectors
        this.type1Selector = new TypeSelectorPanel("Defending Type 1 (Required)", false);
        this.type2Selector = new TypeSelectorPanel("Defending Type 2 (Optional)", true);
        
        // Set default selection for type 1
        type1Selector.setSelectedType(PokemonType.NORMAL);
        
        // Add to selectors panel
        selectorsPanel.add(type1Selector);
        selectorsPanel.add(Box.createHorizontalStrut(SELECTOR_SPACING));
        selectorsPanel.add(type2Selector);
        
        JScrollPane scrollPane = createScrollPane();

        // Assemble layout
        mainPanel.add(selectorsPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // Add listeners
        type1Selector.addSelectionListener(type -> updateResults());
        type2Selector.addSelectionListener(type -> updateResults());

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
        panel.setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
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
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
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

        Map<Double, List<PokemonType>> grouped = calculator.groupTypesByEffectiveness(type1, type2);
        displayGroupedResults(grouped);

        resultsPanel.revalidate();
        resultsPanel.repaint();
        
        // Adjust window height to fit content
        adjustWindowHeight();
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
                resultsPanel.add(Box.createVerticalStrut(VERTICAL_SPACING));
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
                new EmptyBorder(GROUP_PANEL_PADDING, GROUP_PANEL_PADDING, 
                              GROUP_PANEL_PADDING, GROUP_PANEL_PADDING)
        ));

        // Add type labels using renderer
        for (PokemonType type : types) {
            JLabel typeLabel = labelRenderer.createTypeLabel(type);
            panel.add(typeLabel);
        }

        return panel;
    }
}
