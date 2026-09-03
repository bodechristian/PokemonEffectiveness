package com.middle.end.mcp;

import com.middle.end.api.model.PokemonType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class McpPokemonTypeTest {

    /**
     * The MCP enum exists only so the tool schema and the deserializer agree.
     * If the spec gains or renames a type, this catches the drift.
     */
    @Test
    void mirrorsTheGeneratedApiEnum() {
        List<String> api = Arrays.stream(PokemonType.values()).map(Enum::name).toList();
        List<String> mcp = Arrays.stream(McpPokemonType.values()).map(Enum::name).toList();

        assertEquals(api, mcp);
    }

    @Test
    void convertsBothWays() {
        for (McpPokemonType type : McpPokemonType.values()) {
            assertEquals(type, McpPokemonType.fromApiType(type.toApiType()));
        }
    }
}
