<template>
  <main>
    <header>
      <h1>Pokémon Type Effectiveness</h1>
      <p class="sub">Pick a defending Pokémon's types, then check a single attack or the full matchup table.</p>
    </header>

    <section class="panel">
      <h2>Defending Pokémon</h2>
      <div class="row">
        <label>
          Primary type
          <select v-model="defendingType1">
            <option v-for="type in types" :key="type" :value="type">{{ label(type) }}</option>
          </select>
        </label>
        <label>
          Secondary type
          <select v-model="defendingType2">
            <option :value="undefined">— none —</option>
            <option v-for="type in types" :key="type" :value="type">{{ label(type) }}</option>
          </select>
        </label>
      </div>
      <div class="chips">
        <TypeChip :type="defendingType1" />
        <TypeChip v-if="defendingType2" :type="defendingType2" />
      </div>
    </section>

    <section class="panel">
      <h2>Single attack</h2>
      <div class="row">
        <label>
          Attacking type
          <select v-model="attackingType">
            <option v-for="type in types" :key="type" :value="type">{{ label(type) }}</option>
          </select>
        </label>
        <button :disabled="busy" @click="runCalculate">Calculate</button>
      </div>
      <p v-if="result" class="result">
        <TypeChip :type="result.attackingType" /> deals
        <strong>{{ result.label }}</strong>
      </p>
    </section>

    <section class="panel">
      <h2>All matchups</h2>
      <button :disabled="busy" @click="runMatchups">Show matchup table</button>
      <div v-if="matchups" class="groups">
        <div v-for="group in matchups.groups" :key="group.multiplier" class="group">
          <h3>{{ group.label }}</h3>
          <div class="chips">
            <TypeChip v-for="type in group.attackingTypes" :key="type" :type="type" />
          </div>
        </div>
      </div>
    </section>

    <p v-if="error" class="error">{{ error }}</p>
  </main>
</template>

<script setup lang="ts">
import { PokemonType } from '~/generated'
import type { EffectivenessResult, Matchups } from '~/generated'
import { useEffectiveness } from '~/composables/useEffectiveness'

const { calculate, matchups: fetchMatchups } = useEffectiveness()

const types = Object.values(PokemonType)
const label = (type: PokemonType) => type.charAt(0).toUpperCase() + type.slice(1)

const attackingType = ref<PokemonType>(PokemonType.Rock)
const defendingType1 = ref<PokemonType>(PokemonType.Fire)
const defendingType2 = ref<PokemonType | undefined>(PokemonType.Flying)

const result = ref<EffectivenessResult | null>(null)
const matchups = ref<Matchups | null>(null)
const error = ref<string>('')
const busy = ref(false)

// Any change to the defender invalidates results that were computed for the old one.
watch([defendingType1, defendingType2], () => {
  result.value = null
  matchups.value = null
})

const guard = async (action: () => Promise<void>) => {
  busy.value = true
  error.value = ''
  try {
    await action()
  } catch (e) {
    error.value = `Request failed: ${e instanceof Error ? e.message : String(e)}`
  } finally {
    busy.value = false
  }
}

const runCalculate = () => guard(async () => {
  result.value = await calculate(attackingType.value, defendingType1.value, defendingType2.value)
})

const runMatchups = () => guard(async () => {
  matchups.value = await fetchMatchups(defendingType1.value, defendingType2.value)
})
</script>

<style>
:root {
  color-scheme: light dark;
  --panel: rgba(127, 127, 127, 0.09);
  --border: rgba(127, 127, 127, 0.28);
}

body {
  margin: 0;
  font-family: system-ui, -apple-system, "Segoe UI", sans-serif;
  line-height: 1.5;
}

main {
  max-width: 52rem;
  margin: 0 auto;
  padding: 2rem 1.25rem 4rem;
}

h1 { margin: 0 0 0.25rem; font-size: 1.75rem; }
h2 { margin: 0 0 0.75rem; font-size: 1.05rem; }
h3 { margin: 0 0 0.5rem; font-size: 0.9rem; font-weight: 600; opacity: 0.85; }

.sub { margin: 0 0 1.5rem; opacity: 0.7; }

.panel {
  background: var(--panel);
  border: 1px solid var(--border);
  border-radius: 0.75rem;
  padding: 1.25rem;
  margin-bottom: 1rem;
}

.row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: flex-end;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.03em;
  opacity: 0.8;
}

select, button {
  font: inherit;
  padding: 0.45rem 0.7rem;
  border-radius: 0.5rem;
  border: 1px solid var(--border);
  background: transparent;
  color: inherit;
}

button {
  cursor: pointer;
  font-weight: 600;
  background: var(--panel);
}

button:disabled { opacity: 0.5; cursor: progress; }

.chips { display: flex; flex-wrap: wrap; gap: 0.4rem; margin-top: 0.9rem; }

.result { margin: 1rem 0 0; display: flex; align-items: center; gap: 0.5rem; flex-wrap: wrap; }

.groups { margin-top: 1rem; display: flex; flex-direction: column; gap: 1rem; }

.group .chips { margin-top: 0; }

.error {
  color: #c03028;
  font-weight: 600;
}
</style>
