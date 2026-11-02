# ColectaApp — Testing & Code Quality

[![CI](https://github.com/EugeniaDemarchi/colectaapp-testing/actions/workflows/ci.yml/badge.svg)](https://github.com/EugeniaDemarchi/colectaapp-testing/actions/workflows/ci.yml)

Individual testing and code-quality practice built on the domain of **ColectaApp** (a platform for recurring birthday collections whose amounts are indexed to protect them from inflation). Two independent projects, one per stack:

| Folder | Stack | What it shows |
|---|---|---|
| [`backend-junit/`](backend-junit) | Java 21 · Maven · **JUnit 5 · Mockito · JaCoCo** | AAA pattern, parameterized tests, **stubs vs. mocks**, fakes, dependency injection for testability, **100% branch coverage** of a compound condition |
| [`ts-quality/`](ts-quality) | TypeScript · **Vitest · ESLint · Prettier** | Unit tests with an **80% coverage gate**, a **pre-commit hook** (format → lint → tests), and a **static audit**: cyclomatic complexity, duplication and dependency vulnerabilities, before and after a refactor |

Both run on every push in **GitHub Actions** (see `.github/workflows/ci.yml`). The JaCoCo HTML report is uploaded as a build artifact.

## Highlights

**Java (`backend-junit`)**
- `AporteService` charges a contribution through an external payment gateway (`PasarelaPagos`). It receives its dependencies through the constructor, so tests can swap them.
- **Stub tests** check *state* (the contribution ends up `PAGADO` / `RECHAZADO`); **mock tests** check *interactions* with `verify()`: the gateway is charged exactly once, with the frozen amount, never twice for an already-paid contribution, and *before* the result is persisted (`InOrder`).
- `CalculadoraMontoFinal` has a compound condition (`fueraDeTermino && !tieneProrroga`). The test suite documents why two cases already give 100% statement coverage but four are needed for 100% **branch** coverage.

**TypeScript (`ts-quality`)**
- A pre-commit hook blocks the commit if formatting, lint or tests fail. The `evidencias/` folder has the real console output of each blocked and approved commit.
- Static audit of a deliberately bad legacy function and its refactor:

  | Metric | Legacy | Refactored |
  |---|---|---|
  | Cyclomatic complexity (per function) | 12 and 11 | 8, 3 and 5 |
  | Duplicated blocks | several clones | none |
  | `npm audit` on the dependency set | 5 vulnerabilities (2 high) | 0 |

  **Characterization tests** compare the new implementation with the legacy one case by case, so the refactor is proven not to change behavior. Raw outputs are in `ts-quality/auditoria/resultados/`.

## Run it

```bash
# Java
cd backend-junit
mvn test                      # report: target/site/jacoco/index.html

# TypeScript
cd ts-quality
npm ci
npm run test:coverage
npm run lint
npm run auditoria:complejidad # complexity metrics, legacy vs refactored
```

To enable the pre-commit hook in a clone of this repo:

```bash
git config core.hooksPath ts-quality/.githooks
```

## About this project

Individual coursework for **Metodología de Sistemas II** (Universidad Tecnológica Nacional, *Tecnicatura Universitaria en Programación*). I wrote the Java code and tests, the TypeScript tests, the quality tooling (pre-commit hook, ESLint rules, Vitest coverage gate) and the audit and refactor of the legacy code. It is separate from the ColectaApp team repository, which is still in development. Code comments and test names are in Spanish.

**Author:** Eugenia Demarchi · [LinkedIn](https://www.linkedin.com/in/eugenia-demarchi/) · [GitHub](https://github.com/EugeniaDemarchi)
