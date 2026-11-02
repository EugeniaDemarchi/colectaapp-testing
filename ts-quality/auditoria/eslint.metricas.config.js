// Solo para MEDIR: con umbral 1, ESLint informa la complejidad de TODAS las funciones.
import tseslint from 'typescript-eslint'
import { defineConfig } from 'eslint/config'

export default defineConfig([
  {
    files: ['**/*.ts'],
    languageOptions: { parser: tseslint.parser },
    rules: {
      complexity: ['warn', { max: 1 }],
      'max-depth': ['warn', 1],
    },
  },
])
