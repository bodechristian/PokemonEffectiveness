package org.example.pokemon.model;

/**
 * Value object representing a damage effectiveness multiplier.
 * Encapsulates the display logic for different multiplier values.
 * 
 * Implemented as a record for immutability and conciseness.
 */
public record EffectivenessMultiplier(double value) {
    
    /**
     * Compact constructor with validation.
     */
    public EffectivenessMultiplier {
        if (value < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative: " + value);
        }
    }

    /**
     * Factory method for creating effectiveness multipliers.
     *
     * @param value the multiplier value
     * @return the effectiveness multiplier
     */
    public static EffectivenessMultiplier of(double value) {
        return new EffectivenessMultiplier(value);
    }

    /**
     * Gets a human-readable display title for this multiplier.
     * Uses if-else for floating-point comparisons (clearer than nested switch).
     *
     * @return the formatted title (e.g., "2x - Super Effective")
     */
    public String getDisplayTitle() {
        if (value == 0.0) return "0x - No Effect";
        if (value == 0.25) return "0.25x - Extremely Not Very Effective";
        if (value == 0.5) return "0.5x - Not Very Effective";
        if (value == 1.0) return "1x - Normal Damage";
        if (value == 2.0) return "2x - Super Effective";
        if (value == 4.0) return "4x - Extremely Effective";
        return value + "x";
    }

    @Override
    public String toString() {
        return getDisplayTitle();
    }
}
