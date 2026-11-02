# ts-quality — Vitest, ESLint, Prettier and a pre-commit hook

TypeScript code-quality pipeline applied to a set of small domain functions from ColectaApp.

| Command | What it does |
|---|---|
| `npm run test` | Unit tests (Vitest) for `src/lib` |
| `npm run test:coverage` | Tests + coverage; **fails below 80%** lines/branches/functions/statements (`vitest.config.ts`) |
| `npm run lint` | ESLint, including `complexity`, `max-depth` and `eqeqeq` rules |
| `npm run typecheck` | TypeScript in strict mode |
| `npm run format:check` | Prettier check |
| `npm run auditoria:complejidad` | Cyclomatic complexity: legacy vs refactored code |
| `npm run auditoria:duplicacion` | Copy-paste detection (jscpd) |
| `npm run auditoria:dependencias` | `npm audit` + `npm outdated` |

## Folders

- `src/lib/` — the code under test and its `*.test.ts` files:
  - `formato.ts` — currency (ARS) and short date formatting (the tests caught and fixed a day/month bug).
  - `confeti.ts` — deterministic color assignment per person.
  - `estadoColecta.ts` — status label and color of a collection, refactored from `auditoria/legacy/estadoColecta.legacy.ts`.
- `.githooks/pre-commit` — runs Prettier → ESLint → Vitest and cancels the commit if any step fails.
- `auditoria/` — the legacy function, the ESLint configs used to measure it, two `package.json` sets (outdated vs updated) and the raw results (`resultados/*.log`).
- `evidencias/` — console output of the hook blocking commits (formatter, linter, tests) and approving one.

Step-by-step instructions and the full write-up of the audit, in Spanish: [`PASOS.md`](PASOS.md). (They were written for the original standalone folder, `colectaapp-calidad`.)
