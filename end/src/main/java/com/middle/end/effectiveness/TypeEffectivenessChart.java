package com.middle.end.effectiveness;

import com.middle.end.api.model.PokemonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Holds the type effectiveness chart, loaded once from CSV at startup.
 * Single Responsibility: only data loading and lookup, no calculation rules.
 */
@Component
public class TypeEffectivenessChart {

    private static final Logger log = LoggerFactory.getLogger(TypeEffectivenessChart.class);
    private static final double DEFAULT_EFFECTIVENESS = 1.0;

    private final Map<PokemonType, Map<PokemonType, Double>> chart;

    public TypeEffectivenessChart(@Value("${app.effectiveness.chart:classpath:type_effectiveness.csv}") Resource csv) {
        this.chart = load(csv);
        log.info("Loaded type effectiveness chart for {} attacking types from {}", chart.size(), csv.getDescription());
    }

    /**
     * Gets the multiplier for a single attacker/defender pairing.
     * Pairings that are absent from the chart deal normal damage.
     */
    public double multiplierFor(PokemonType attackingType, PokemonType defendingType) {
        return chart.getOrDefault(attackingType, Collections.emptyMap())
                .getOrDefault(defendingType, DEFAULT_EFFECTIVENESS);
    }

    private static Map<PokemonType, Map<PokemonType, Double>> load(Resource csv) {
        Map<PokemonType, Map<PokemonType, Double>> parsed = new EnumMap<>(PokemonType.class);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                parseLine(line, lineNumber, parsed);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load type effectiveness data from " + csv.getDescription(), e);
        }

        if (parsed.isEmpty()) {
            throw new IllegalStateException("Type effectiveness chart is empty: " + csv.getDescription());
        }
        return parsed;
    }

    private static void parseLine(String line, int lineNumber, Map<PokemonType, Map<PokemonType, Double>> target) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            log.warn("Ignoring malformed chart entry at line {}: {}", lineNumber, line);
            return;
        }

        try {
            PokemonType attackingType = PokemonType.valueOf(parts[0].trim().toUpperCase());
            PokemonType defendingType = PokemonType.valueOf(parts[1].trim().toUpperCase());
            double multiplier = Double.parseDouble(parts[2].trim());

            if (multiplier < 0) {
                log.warn("Ignoring negative multiplier at line {}: {}", lineNumber, line);
                return;
            }

            target.computeIfAbsent(attackingType, key -> new EnumMap<>(PokemonType.class))
                    .put(defendingType, multiplier);
        } catch (IllegalArgumentException e) {
            log.warn("Ignoring unparsable chart entry at line {}: {}", lineNumber, line);
        }
    }
}
