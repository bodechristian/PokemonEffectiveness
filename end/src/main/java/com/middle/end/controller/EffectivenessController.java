package com.middle.end.controller;

import com.middle.end.api.EffectivenessApi;
import com.middle.end.api.model.EffectivenessResult;
import com.middle.end.api.model.MatchupGroup;
import com.middle.end.api.model.Matchups;
import com.middle.end.api.model.PokemonType;
import com.middle.end.effectiveness.TypeEffectivenessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class EffectivenessController implements EffectivenessApi {

    private final TypeEffectivenessService service;

    public EffectivenessController(TypeEffectivenessService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<EffectivenessResult> calculateEffectiveness(PokemonType attackingType,
                                                                      PokemonType defendingType1,
                                                                      PokemonType defendingType2) {
        double multiplier = service.calculateCombinedEffectiveness(attackingType, defendingType1, defendingType2);

        EffectivenessResult result = new EffectivenessResult(
                attackingType, defendingType1, multiplier, TypeEffectivenessService.describe(multiplier));
        result.setDefendingType2(defendingType2);

        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Matchups> getMatchups(PokemonType defendingType1, PokemonType defendingType2) {
        Map<Double, List<PokemonType>> grouped = service.groupTypesByEffectiveness(defendingType1, defendingType2);

        List<MatchupGroup> groups = grouped.entrySet().stream()
                .map(entry -> new MatchupGroup(
                        entry.getKey(), TypeEffectivenessService.describe(entry.getKey()), entry.getValue()))
                .toList();

        Matchups matchups = new Matchups(defendingType1, groups);
        matchups.setDefendingType2(defendingType2);

        return ResponseEntity.ok(matchups);
    }
}
