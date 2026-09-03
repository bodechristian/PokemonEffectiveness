import type { EffectivenessResult, Matchups, PokemonType } from '~/generated'

export const useEffectiveness = () => {
  const { $api } = useNuxtApp()

  return {
    calculate: (
      attackingType: PokemonType,
      defendingType1: PokemonType,
      defendingType2?: PokemonType
    ): Promise<EffectivenessResult> =>
      $api.EffectivenessAPI.calculateEffectiveness({ attackingType, defendingType1, defendingType2 }),

    matchups: (
      defendingType1: PokemonType,
      defendingType2?: PokemonType
    ): Promise<Matchups> =>
      $api.EffectivenessAPI.getMatchups({ defendingType1, defendingType2 })
  }
}
