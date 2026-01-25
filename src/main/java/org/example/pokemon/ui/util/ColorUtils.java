package org.example.pokemon.ui.util;

import java.awt.*;

public final class ColorUtils {
    private static final int BRIGHTNESS_THRESHOLD = 160;
    
    private ColorUtils() {}
    
    public static Color getContrastingTextColor(Color backgroundColor) {
        int brightness = (backgroundColor.getRed() 
                        + backgroundColor.getGreen() 
                        + backgroundColor.getBlue()) / 3;
        return brightness < BRIGHTNESS_THRESHOLD ? Color.WHITE : Color.BLACK;
    }
}
