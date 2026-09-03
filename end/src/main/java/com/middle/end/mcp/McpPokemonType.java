package com.middle.end.mcp;

import com.middle.end.api.model.PokemonType;

/**
 * The MCP-facing spelling of {@link PokemonType}.
 *
 * <p>The generated API enum carries {@code @JsonValue} lowercase wire names, but
 * the MCP tool schema generator emits Java constant names instead. A client
 * following that schema sends {@code "FIRE"} while Jackson would only accept
 * {@code "fire"}, so neither spelling works. This enum has no {@code @JsonValue},
 * which makes the advertised schema and the deserializer agree.
 *
 * <p>{@code McpPokemonTypeTest} keeps the two enums from drifting apart.
 */
public enum McpPokemonType {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS, ICE, FIGHTING, POISON, GROUND,
    FLYING, PSYCHIC, BUG, ROCK, GHOST, DRAGON, DARK, STEEL, FAIRY;

    public PokemonType toApiType() {
        return PokemonType.valueOf(name());
    }

    public static McpPokemonType fromApiType(PokemonType type) {
        return valueOf(type.name());
    }
}
