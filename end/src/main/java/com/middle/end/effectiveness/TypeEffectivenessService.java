package com.middle.end.effectiveness;

import com.middle.end.api.model.PokemonType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Calculates Pokemon type effectiveness.
 * Single Responsibility: only the calculation rules, the chart data lives in
 * {@link TypeEffectivenessChart}.
 */
@Service
public class TypeEffectivenessService {

    private final TypeEffectivenessChart chart;

    public TypeEffectivenessService(TypeEffectivenessChart chart) {
        this.chart = Objects.requireNonNull(chart, "chart");
    }

    /**
     * Gets the damage multiplier of a single attacking type against a single defending type.
     */
    public double getEffectiveness(PokemonType attackingType, PokemonType defendingType) {
        Objects.requireNonNull(attackingType, "attackingType");
        Objects.requireNonNull(defendingType, "defendingType");
        return chart.multiplierFor(attackingType, defendingType);
    }

    /**
     * Calculates effectiveness against a Pokemon with one or two types.
     * For dual-type Pokemon the two multipliers are multiplied together.
     *
     * @param defendingType2 may be null for a single-type Pokemon
     */
    public double calculateCombinedEffectiveness(PokemonType attackingType,
                                                 PokemonType defendingType1,
                                                 PokemonType defendingType2) {
        double effectiveness = getEffectiveness(attackingType, defendingType1);

        if (defendingType2 == null || defendingType2 == defendingType1) {
            return effectiveness;
        }
        return effectiveness * getEffectiveness(attackingType, defendingType2);
    }

    /**
     * Groups every attacking type by the multiplier it deals to the given defender,
     * ordered from the highest multiplier to the lowest.
     *
     * @param defendingType2 may be null for a single-type Pokemon
     */
    public Map<Double, List<PokemonType>> groupTypesByEffectiveness(PokemonType defendingType1,
                                                                    PokemonType defendingType2) {
        Map<Double, List<PokemonType>> grouped = new TreeMap<>(Comparator.reverseOrder());

        for (PokemonType attackingType : PokemonType.values()) {
            double multiplier = calculateCombinedEffectiveness(attackingType, defendingType1, defendingType2);
            grouped.computeIfAbsent(multiplier, key -> new ArrayList<>()).add(attackingType);
        }

        return new LinkedHashMap<>(grouped);
    }

    /**
     * Human readable description of a multiplier, e.g. "2x - Super Effective".
     */
    public static String describe(double multiplier) {
        if (multiplier == 0.0) return "0x - No Effect";
        if (multiplier == 0.25) return "0.25x - Extremely Not Very Effective";
        if (multiplier == 0.5) return "0.5x - Not Very Effective";
        if (multiplier == 1.0) return "1x - Normal Damage";
        if (multiplier == 2.0) return "2x - Super Effective";
        if (multiplier == 4.0) return "4x - Extremely Effective";
        return multiplier + "x";
    }
}
