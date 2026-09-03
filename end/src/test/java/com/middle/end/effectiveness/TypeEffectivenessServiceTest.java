package com.middle.end.effectiveness;

import com.middle.end.api.model.PokemonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypeEffectivenessServiceTest {

    private TypeEffectivenessService service;

    @BeforeEach
    void setUp() {
        service = new TypeEffectivenessService(
                new TypeEffectivenessChart(new ClassPathResource("type_effectiveness.csv")));
    }

    @Test
    void singleTypeEffectiveness() {
        assertEquals(2.0, service.getEffectiveness(PokemonType.FIRE, PokemonType.GRASS));
        assertEquals(0.5, service.getEffectiveness(PokemonType.FIRE, PokemonType.WATER));
        assertEquals(0.0, service.getEffectiveness(PokemonType.NORMAL, PokemonType.GHOST));
        // Pairings absent from the chart deal normal damage
        assertEquals(1.0, service.getEffectiveness(PokemonType.NORMAL, PokemonType.FIRE));
    }

    @Test
    void dualTypeMultipliersAreMultiplied() {
        // Charizard is Fire/Flying, Rock hits both for 2x
        assertEquals(4.0, service.calculateCombinedEffectiveness(
                PokemonType.ROCK, PokemonType.FIRE, PokemonType.FLYING));
        // Electric is 2x on Water but 0.5x on Dragon
        assertEquals(1.0, service.calculateCombinedEffectiveness(
                PokemonType.ELECTRIC, PokemonType.WATER, PokemonType.DRAGON));
        // Ground does not touch Flying at all
        assertEquals(0.0, service.calculateCombinedEffectiveness(
                PokemonType.GROUND, PokemonType.STEEL, PokemonType.FLYING));
    }

    @Test
    void missingSecondTypeFallsBackToSingleType() {
        assertEquals(2.0, service.calculateCombinedEffectiveness(
                PokemonType.WATER, PokemonType.FIRE, null));
        // A repeated type must not be squared
        assertEquals(2.0, service.calculateCombinedEffectiveness(
                PokemonType.WATER, PokemonType.FIRE, PokemonType.FIRE));
    }

    @Test
    void matchupsCoverEveryAttackingTypeOnceAndAreOrderedDescending() {
        Map<Double, List<PokemonType>> grouped =
                service.groupTypesByEffectiveness(PokemonType.FIRE, PokemonType.FLYING);

        assertEquals(PokemonType.values().length,
                grouped.values().stream().mapToInt(List::size).sum());
        assertTrue(grouped.get(4.0).contains(PokemonType.ROCK));
        assertTrue(grouped.get(0.25).contains(PokemonType.GRASS));

        List<Double> multipliers = List.copyOf(grouped.keySet());
        for (int i = 1; i < multipliers.size(); i++) {
            assertTrue(multipliers.get(i - 1) > multipliers.get(i),
                    "groups must be ordered from strongest to weakest");
        }
    }

    @Test
    void nullTypesAreRejected() {
        assertThrows(NullPointerException.class,
                () -> service.getEffectiveness(null, PokemonType.FIRE));
        assertThrows(NullPointerException.class,
                () -> service.calculateCombinedEffectiveness(PokemonType.FIRE, null, null));
    }

    @Test
    void multipliersAreDescribed() {
        assertEquals("4x - Extremely Effective", TypeEffectivenessService.describe(4.0));
        assertEquals("0x - No Effect", TypeEffectivenessService.describe(0.0));
        assertEquals("1x - Normal Damage", TypeEffectivenessService.describe(1.0));
    }
}
