<template>
  <span class="chip" :style="{ background: TYPE_COLORS[type], color: textColorFor(type) }">
    {{ type }}
  </span>
</template>

<script setup lang="ts">
import type { PokemonType } from '~/generated'
import { TYPE_COLORS, textColorFor } from '~/utils/typeColors'

defineProps<{ type: PokemonType }>()
</script>

<style scoped>
.chip {
  display: inline-block;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  font-size: 0.78rem;
  font-weight: 700;
  text-transform: capitalize;
  letter-spacing: 0.02em;
}
</style>
