import { defineConfig } from 'vitest/config'

export default defineConfig({
  test: {
    include: ['src/**/*.test.ts'],
    coverage: {
      provider: 'v8',
      include: ['src/lib/**/*.ts'],
      reporter: ['text', 'html'],
      // Umbral de la Definition of Done (TP1, criterio 3): mínimo 80 %
      thresholds: { lines: 80, branches: 80, functions: 80, statements: 80 },
    },
  },
})
