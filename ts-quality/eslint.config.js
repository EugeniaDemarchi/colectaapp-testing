import js from '@eslint/js'
import globals from 'globals'
import tseslint from 'typescript-eslint'
import { defineConfig, globalIgnores } from 'eslint/config'

export default defineConfig([
  globalIgnores(['dist', 'coverage', 'auditoria/legacy']),
  {
    files: ['**/*.ts'],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
    ],
    languageOptions: {
      globals: globals.browser,
    },
    // Métricas de calidad (TP 3, punto 2): frenan en el pre-commit el código nuevo
    // que vuelva a ser complejo, demasiado anidado o que use == en vez de ===.
    rules: {
      complexity: ['error', { max: 10 }],
      'max-depth': ['error', 3],
      eqeqeq: 'error',
    },
  },
])
