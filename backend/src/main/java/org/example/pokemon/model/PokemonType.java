package org.example.pokemon.model;

import java.awt.Color;

/**
 * Represents Pokemon types with their official color codes.
 */
public enum PokemonType {
    NONE("None", new Color(168, 168, 120)),
    NORMAL("Normal", new Color(168, 168, 120)),
    FIRE("Fire", new Color(240, 128, 48)),
    WATER("Water", new Color(104, 144, 240)),
    ELECTRIC("Electric", new Color(248, 208, 48)),
    GRASS("Grass", new Color(120, 200, 80)),
    ICE("Ice", new Color(152, 216, 216)),
    FIGHTING("Fighting", new Color(192, 48, 40)),
    POISON("Poison", new Color(160, 64, 160)),
    GROUND("Ground", new Color(224, 192, 104)),
    FLYING("Flying", new Color(168, 144, 240)),
    PSYCHIC("Psychic", new Color(248, 88, 136)),
    BUG("Bug", new Color(168, 184, 32)),
    ROCK("Rock", new Color(184, 160, 56)),
    GHOST("Ghost", new Color(112, 88, 152)),
    DRAGON("Dragon", new Color(112, 56, 248)),
    DARK("Dark", new Color(112, 88, 72)),
    STEEL("Steel", new Color(184, 184, 208)),
    FAIRY("Fairy", new Color(238, 153, 172));

    private final String displayName;
    private final Color color;

    PokemonType(String displayName, Color color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
