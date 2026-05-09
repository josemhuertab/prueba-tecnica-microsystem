<template>
  <div id="app-root">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

// Restaura el tema guardado al recargar la página
onMounted(() => {
  const saved = localStorage.getItem('theme') || 'light'
  document.documentElement.setAttribute('data-theme', saved)
})
</script>

<style>
#app-root {
  min-height: 100vh;
}
</style>
