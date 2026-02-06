package org.example;

import org.example.pokemon.model.EffectivenessMultiplier;
import org.example.pokemon.model.PokemonType;
import org.example.pokemon.service.TypeEffectivenessCalculator;
import org.example.pokemon.ui.TypeLabelRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private TypeEffectivenessCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = TypeEffectivenessCalculator.createDefault();
    }

    @Test
    void testTypeEffectivenessCalculator() {
        // Test single type effectiveness
        assertEquals(2.0, calculator.getEffectiveness(PokemonType.FIRE, PokemonType.GRASS));
        assertEquals(0.5, calculator.getEffectiveness(PokemonType.FIRE, PokemonType.WATER));
        assertEquals(0.0, calculator.getEffectiveness(PokemonType.NORMAL, PokemonType.GHOST));
        
        // Test dual type effectiveness (Fire/Flying like Charizard vs Rock)
        assertEquals(4.0, calculator.calculateCombinedEffectiveness(
                PokemonType.ROCK, PokemonType.FIRE, PokemonType.FLYING));
    }
    
    @Test
    void testPokemonTypeEnum() {
        assertNotNull(PokemonType.FIRE.getColor());
        assertEquals("Fire", PokemonType.FIRE.getDisplayName());
        assertNotEquals(PokemonType.FIRE.getColor(), PokemonType.WATER.getColor());
    }

    @Test
    void testEffectivenessMultiplier() {
        EffectivenessMultiplier multiplier = EffectivenessMultiplier.of(2.0);
        assertEquals(2.0, multiplier.value());
        assertEquals("2x - Super Effective", multiplier.getDisplayTitle());
    }

    @Test
    void testTypeLabelRenderer() {
        TypeLabelRenderer renderer = new TypeLabelRenderer();
        assertThrows(IllegalArgumentException.class, () -> renderer.createTypeLabel(null));
        assertThrows(IllegalArgumentException.class, () -> renderer.createTypeLabel(PokemonType.NONE));
        
        // Should not throw
        assertDoesNotThrow(() -> renderer.createTypeLabel(PokemonType.FIRE));
    }

    @Test
    void testNullSafety() {
        // Calculator should reject null inputs
        assertThrows(NullPointerException.class, 
            () -> calculator.getEffectiveness(null, PokemonType.FIRE));
        assertThrows(NullPointerException.class, 
            () -> calculator.calculateCombinedEffectiveness(PokemonType.FIRE, null, PokemonType.NONE));
    }
}
