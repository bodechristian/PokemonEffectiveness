package com.middle.end.mcp;

import com.middle.end.api.model.PokemonType;
import com.middle.end.effectiveness.TypeEffectivenessService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Exposes the effectiveness calculation to MCP clients.
 *
 * <p>Nothing else is needed to publish these: the MCP server starter's annotation
 * scanner picks up {@link McpTool} methods on any bean, and the same
 * {@link TypeEffectivenessService} backs both the REST API and the tools.
 */
@Service
public class EffectivenessMcpTools {

    private final TypeEffectivenessService service;

    public EffectivenessMcpTools(TypeEffectivenessService service) {
        this.service = service;
    }

    @McpTool(
            name = "calculate_combined_effectiveness",
            title = "Calculate combined type effectiveness",
            description = """
                    Calculate the damage multiplier an attacking move type deals to a Pokemon \
                    with one or two defending types. For dual-type Pokemon the two multipliers \
                    are multiplied together, so the result can be 0, 0.25, 0.5, 1, 2 or 4.""")
    public EffectivenessAnswer calculateCombinedEffectiveness(
            @McpToolParam(required = true, description = "Type of the attacking move")
            McpPokemonType attackingType,
            @McpToolParam(required = true, description = "Primary type of the defending Pokemon")
            McpPokemonType defendingType1,
            @McpToolParam(required = false, description = "Secondary type of the defending Pokemon, omit for single-type Pokemon")
            McpPokemonType defendingType2) {

        PokemonType secondary = defendingType2 == null ? null : defendingType2.toApiType();
        double multiplier = service.calculateCombinedEffectiveness(
                attackingType.toApiType(), defendingType1.toApiType(), secondary);

        return new EffectivenessAnswer(attackingType, defendingType1, defendingType2,
                multiplier, TypeEffectivenessService.describe(multiplier));
    }

    @McpTool(
            name = "get_type_matchups",
            title = "Get all matchups against a defender",
            description = """
                    List every attacking type grouped by the damage multiplier it deals to a \
                    Pokemon with the given defending types, ordered from strongest to weakest. \
                    Use this to answer questions like "what is super effective against a \
                    Fire/Flying Pokemon".""")
    public List<MatchupAnswer> getTypeMatchups(
            @McpToolParam(required = true, description = "Primary type of the defending Pokemon")
            McpPokemonType defendingType1,
            @McpToolParam(required = false, description = "Secondary type of the defending Pokemon, omit for single-type Pokemon")
            McpPokemonType defendingType2) {

        PokemonType secondary = defendingType2 == null ? null : defendingType2.toApiType();
        Map<Double, List<PokemonType>> grouped =
                service.groupTypesByEffectiveness(defendingType1.toApiType(), secondary);

        return grouped.entrySet().stream()
                .map(entry -> new MatchupAnswer(
                        entry.getKey(),
                        TypeEffectivenessService.describe(entry.getKey()),
                        entry.getValue().stream().map(McpPokemonType::fromApiType).toList()))
                .toList();
    }

    @McpTool(
            name = "list_pokemon_types",
            title = "List the known Pokemon types",
            description = "List every Pokemon type accepted by the other tools.")
    public List<McpPokemonType> listPokemonTypes() {
        return List.of(McpPokemonType.values());
    }

    public record EffectivenessAnswer(McpPokemonType attackingType,
                                      McpPokemonType defendingType1,
                                      McpPokemonType defendingType2,
                                      double multiplier,
                                      String label) {
    }

    public record MatchupAnswer(double multiplier, String label, List<McpPokemonType> attackingTypes) {
    }
}
