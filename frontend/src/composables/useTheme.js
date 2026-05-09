import { ref, watch } from 'vue'

// Estado del tema compartido entre todos los componentes que importen este composable
const isDark = ref(localStorage.getItem('theme') === 'dark')

// Cada vez que isDark cambia, actualizamos el atributo del HTML y guardamos la preferencia
watch(isDark, (dark) => {
  const theme = dark ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}, { immediate: true }) // immediate: aplica el tema guardado al cargar la página

export function useTheme() {
  function toggleTheme() {
    isDark.value = !isDark.value
  }

  return { isDark, toggleTheme }
}
