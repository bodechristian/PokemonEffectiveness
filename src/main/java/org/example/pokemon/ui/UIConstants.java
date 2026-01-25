package org.example.pokemon.ui;

import java.awt.*;

public final class UIConstants {
    private UIConstants() {}
    
    // Window dimensions
    public static final int WINDOW_WIDTH = 720;
    
    // Component dimensions
    public static final int TYPE_LABEL_WIDTH = 70;
    public static final int TYPE_LABEL_HEIGHT = 28;
    
    // Padding
    public static final int PADDING_VERTICAL = 4;
    public static final int PADDING_HORIZONTAL = 8;
    public static final int PANEL_PADDING = 8;
    public static final int MAIN_PANEL_PADDING = 15;
    
    // Gaps
    public static final int PANEL_GAP = 10;
    public static final int TYPE_PANEL_GAP = 4;
    public static final int GRID_COLUMNS = 4;
    
    // Spacing
    public static final int VERTICAL_SPACING = 10;
    public static final int RESULTS_VERTICAL_SPACING = 3;
    
    // Borders
    public static final int SELECTED_BORDER_WIDTH = 3;
    public static final int NORMAL_BORDER_WIDTH = 2;
    public static final int DISABLED_LINE_WIDTH = 4;
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 10);
    public static final Font PLACEHOLDER_FONT = new Font("Arial", Font.PLAIN, 10);
    
    // Colors
    public static final int BRIGHTNESS_THRESHOLD = 160;
    public static final Color SELECTED_BORDER_COLOR = new Color(50, 50, 50);
    public static final Color UNSELECTED_BORDER_COLOR = new Color(150, 150, 150);
}
