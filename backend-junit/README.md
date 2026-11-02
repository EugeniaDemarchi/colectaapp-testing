# backend-junit — JUnit 5, Mockito and JaCoCo

Standalone Maven project (Java 21) with code from the ColectaApp domain and its test suite.

```
src/main/java/ar/utn/colectaapp/
  montos/CalculadoraMontoAporte.java   function under test (AAA pattern, parameterized tests)
  montos/CalculadoraMontoFinal.java    compound condition (branch coverage)
  aportes/AporteService.java           service that calls an external payment gateway
  aportes/PasarelaPagos.java           gateway interface (e.g. Mercado Pago)
  aportes/...                          Aporte, EstadoAporte, ResultadoPago, exceptions

src/test/java/ar/utn/colectaapp/
  montos/CalculadoraMontoAporteTest.java   AAA suite + @ParameterizedTest
  aportes/AporteServiceStubTest.java       STUBS — verify resulting state
  aportes/AporteServiceMockTest.java       MOCKS — verify interactions (times, never, InOrder)
  aportes/RepositorioAportesEnMemoria.java FAKE in-memory repository
  montos/CalculadoraMontoFinalTest.java    cases C1–C4 → 100% branch coverage
```

## Run

```bash
mvn test
```

The JaCoCo report is generated at `target/site/jacoco/index.html` (the **Branches** column shows branch coverage). In IntelliJ: open the folder with the `pom.xml`, set the SDK to Java 21 and run `src/test/java` with coverage.

## Key ideas

- **Stub vs. mock.** The stub tests answer "did the service end in the right state?"; the mock tests answer "did the service talk to the gateway correctly?" — exactly once, with the frozen amount, never for an already-paid contribution, never persisting when the gateway is down, and charging *before* saving.
- **Idempotency.** An already-paid contribution is never charged again.
- **Statement vs. branch coverage.** For `fueraDeTermino && !tieneProrroga`, cases C1 and C2 already reach 100% statement coverage; C3 and C4 are required to cover every branch of the compound condition. The table is documented at the top of `CalculadoraMontoFinalTest`.
