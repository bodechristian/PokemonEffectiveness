package org.example.pokemon.data;

import org.example.pokemon.model.PokemonType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Loads Pokemon type effectiveness data from CSV file.
 * Single Responsibility: Only handles data loading and parsing.
 */
public class TypeEffectivenessDataLoader {
    private static final String DEFAULT_CSV_PATH = "/type_effectiveness.csv";
    private final String csvPath;

    /**
     * Creates a loader with the default CSV path.
     */
    public TypeEffectivenessDataLoader() {
        this(DEFAULT_CSV_PATH);
    }

    /**
     * Creates a loader with a custom CSV path.
     *
     * @param csvPath path to the CSV resource file
     */
    public TypeEffectivenessDataLoader(String csvPath) {
        this.csvPath = Objects.requireNonNull(csvPath, "CSV path cannot be null");
    }

    /**
     * Loads type effectiveness data from the CSV file.
     *
     * @return a map of attacking types to defending types with their effectiveness multipliers
     * @throws TypeEffectivenessLoadException if the data cannot be loaded
     */
    public Map<PokemonType, Map<PokemonType, Double>> loadEffectivenessData() {
        Map<PokemonType, Map<PokemonType, Double>> effectivenessChart = new EnumMap<>(PokemonType.class);

        try (InputStream is = getClass().getResourceAsStream(csvPath)) {
            if (is == null) {
                throw new TypeEffectivenessLoadException("CSV file not found: " + csvPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();

                    // Skip comments and empty lines
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    parseLine(line, lineNumber, effectivenessChart);
                }
            }
        } catch (IOException e) {
            throw new TypeEffectivenessLoadException("Failed to load type effectiveness data from: " + csvPath, e);
        }

        return effectivenessChart;
    }

    /**
     * Parses a single CSV line and adds it to the effectiveness chart.
     */
    private void parseLine(String line, int lineNumber, Map<PokemonType, Map<PokemonType, Double>> chart) {
        String[] parts = line.split(",");
        
        if (parts.length != 3) {
            System.err.println("Warning: Invalid CSV format at line " + lineNumber + ": " + line);
            return;
        }

        try {
            PokemonType attackingType = PokemonType.valueOf(parts[0].trim().toUpperCase());
            PokemonType defendingType = PokemonType.valueOf(parts[1].trim().toUpperCase());
            double multiplier = parseMultiplier(parts[2].trim(), lineNumber);

            chart.computeIfAbsent(attackingType, k -> new EnumMap<>(PokemonType.class))
                 .put(defendingType, multiplier);

        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Invalid type at line " + lineNumber + ": " + line);
        }
    }

    /**
     * Parses and validates a multiplier value.
     */
    private double parseMultiplier(String value, int lineNumber) {
        try {
            double multiplier = Double.parseDouble(value);
            if (multiplier < 0) {
                System.err.println("Warning: Negative multiplier at line " + lineNumber + ", using absolute value");
                return Math.abs(multiplier);
            }
            return multiplier;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid multiplier format: " + value);
        }
    }

    /**
     * Exception thrown when type effectiveness data cannot be loaded.
     */
    public static class TypeEffectivenessLoadException extends RuntimeException {
        public TypeEffectivenessLoadException(String message) {
            super(message);
        }

        public TypeEffectivenessLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
