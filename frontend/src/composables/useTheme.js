import { ref, watch } from 'vue'

// Estado global del tema (compartido entre todos los componentes que lo usen)
const isDark = ref(localStorage.getItem('theme') === 'dark')

watch(isDark, (dark) => {
  const theme = dark ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}, { immediate: true })

export function useTheme() {
  function toggleTheme() {
    isDark.value = !isDark.value
  }

  return { isDark, toggleTheme }
}
