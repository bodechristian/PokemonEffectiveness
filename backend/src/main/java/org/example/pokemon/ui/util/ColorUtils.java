package org.example.pokemon.ui.util;

import org.example.pokemon.ui.UIConstants;

import java.awt.*;

public final class ColorUtils {
    private ColorUtils() {}
    
    public static Color getContrastingTextColor(Color backgroundColor) {
        int brightness = (backgroundColor.getRed() 
                        + backgroundColor.getGreen() 
                        + backgroundColor.getBlue()) / 3;
        return brightness < UIConstants.BRIGHTNESS_THRESHOLD ? Color.WHITE : Color.BLACK;
    }
}
