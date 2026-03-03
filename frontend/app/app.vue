<template>
  <div>
    <button @click="fetchGreeting">Get Greeting</button>
    <p>{{ greeting }}</p>
  </div>
</template>

<script setup lang="ts">
import {useHelloWorld} from "~/composables/useHelloWorld";

const { getGreeting } = useHelloWorld();

const greeting = ref<string>('nuttin');

const fetchGreeting = async () => {
  greeting.value = await getGreeting();
};
</script>
