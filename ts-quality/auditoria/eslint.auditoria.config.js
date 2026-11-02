// Configuración de ESLint SOLO para la auditoría estática (punto 2 del TP 3).
// Agrega métricas de complejidad sobre las reglas recomendadas.
import js from '@eslint/js'
import tseslint from 'typescript-eslint'
import { defineConfig } from 'eslint/config'

export default defineConfig([
  {
    files: ['**/*.ts'],
    extends: [js.configs.recommended, tseslint.configs.recommended],
    rules: {
      // Complejidad ciclomática (McCabe): caminos independientes por función. Umbral 10.
      complexity: ['error', { max: 10 }],
      // Profundidad de anidamiento de bloques (if dentro de if dentro de if...).
      'max-depth': ['error', 3],
      // Comparaciones con == en vez de === (coerción de tipos implícita).
      eqeqeq: 'error',
    },
  },
])
