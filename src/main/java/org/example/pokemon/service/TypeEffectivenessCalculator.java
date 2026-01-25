package org.example.pokemon.service;

import org.example.pokemon.data.TypeEffectivenessDataLoader;
import org.example.pokemon.model.PokemonType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Calculates Pokemon type effectiveness.
 * Single Responsibility: Only handles effectiveness calculations.
 */
public class TypeEffectivenessCalculator {
    private static final double DEFAULT_EFFECTIVENESS = 1.0;
    private final Map<PokemonType, Map<PokemonType, Double>> effectivenessChart;

    /**
     * Constructor with dependency injection.
     *
     * @param effectivenessChart the pre-loaded effectiveness data
     */
    public TypeEffectivenessCalculator(Map<PokemonType, Map<PokemonType, Double>> effectivenessChart) {
        this.effectivenessChart = Objects.requireNonNull(effectivenessChart, "Effectiveness chart cannot be null");
    }

    /**
     * Convenience factory method that loads data from default CSV.
     *
     * @return a new calculator with default data
     */
    public static TypeEffectivenessCalculator createDefault() {
        TypeEffectivenessDataLoader loader = new TypeEffectivenessDataLoader();
        return new TypeEffectivenessCalculator(loader.loadEffectivenessData());
    }

    /**
     * Gets the effectiveness multiplier when an attacking type hits a defending type.
     *
     * @param attackingType the type of the attacking move
     * @param defendingType the type being defended
     * @return the damage multiplier (0.0, 0.5, 1.0, 2.0, etc.)
     */
    public double getEffectiveness(PokemonType attackingType, PokemonType defendingType) {
        Objects.requireNonNull(attackingType, "Attacking type cannot be null");
        Objects.requireNonNull(defendingType, "Defending type cannot be null");

        if (attackingType == PokemonType.NONE || defendingType == PokemonType.NONE) {
            return DEFAULT_EFFECTIVENESS;
        }

        return effectivenessChart
                .getOrDefault(attackingType, new EnumMap<>(PokemonType.class))
                .getOrDefault(defendingType, DEFAULT_EFFECTIVENESS);
    }

    /**
     * Calculates effectiveness against a Pokemon with one or two types.
     * For dual-type Pokemon, multipliers are multiplied together.
     *
     * @param attackingType the type of the attacking move
     * @param defendingType1 the primary defending type
     * @param defendingType2 the secondary defending type (can be NONE)
     * @return the combined damage multiplier
     */
    public double calculateCombinedEffectiveness(PokemonType attackingType,
                                                   PokemonType defendingType1,
                                                   PokemonType defendingType2) {
        Objects.requireNonNull(attackingType, "Attacking type cannot be null");
        Objects.requireNonNull(defendingType1, "Primary defending type cannot be null");
        Objects.requireNonNull(defendingType2, "Secondary defending type cannot be null");

        double effectiveness1 = getEffectiveness(attackingType, defendingType1);
        
        if (defendingType2 == PokemonType.NONE) {
            return effectiveness1;
        }

        double effectiveness2 = getEffectiveness(attackingType, defendingType2);
        return effectiveness1 * effectiveness2;
    }

    /**
     * Groups all attacking types by their effectiveness against the target Pokemon.
     *
     * @param defendingType1 the primary defending type
     * @param defendingType2 the secondary defending type (can be NONE)
     * @return a map of multipliers to types that deal that damage
     */
    public Map<Double, List<PokemonType>> groupTypesByEffectiveness(
            PokemonType defendingType1, PokemonType defendingType2) {
        Objects.requireNonNull(defendingType1, "Primary defending type cannot be null");
        Objects.requireNonNull(defendingType2, "Secondary defending type cannot be null");
        
        Map<Double, List<PokemonType>> grouped = new HashMap<>();
        
        for (PokemonType attackingType : PokemonType.values()) {
            if (attackingType == PokemonType.NONE) {
                continue;
            }

            double effectiveness = calculateCombinedEffectiveness(
                    attackingType, defendingType1, defendingType2);
            
            grouped.computeIfAbsent(effectiveness, k -> new java.util.ArrayList<>())
                   .add(attackingType);
        }

        return grouped;
    }
}
