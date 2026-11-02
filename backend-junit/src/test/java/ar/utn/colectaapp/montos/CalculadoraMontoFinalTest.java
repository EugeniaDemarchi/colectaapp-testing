package ar.utn.colectaapp.montos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PUNTO 3 - Casos para 100% de COBERTURA DE RAMAS.
 *
 *  Caso | esRecaudador | fueraDeTermino | tieneProrroga | D1 | D2 (A && !B) | Esperado
 *  C1   | true         | -              | -             | V  | no se evalúa | 0.00
 *  C2   | false        | true           | false         | F  | V            | 1100.00
 *  C3   | false        | false          | false         | F  | F (A falsa)  | 1000.00
 *  C4   | false        | true           | true          | F  | F (!B falsa) | 1000.00
 *
 *  C1 + C2 ya dan 100% de SENTENCIAS. C3 y C4 solo los exige la cobertura de RAMAS.
 */
@DisplayName("Punto 3 - CalculadoraMontoFinal (cobertura de ramas)")
class CalculadoraMontoFinalTest {

    private final CalculadoraMontoFinal calculadora = new CalculadoraMontoFinal();
    private final BigDecimal montoCongelado = new BigDecimal("1000.00");

    @Test
    @DisplayName("C1 - El recaudador no aporta (D1 verdadera)")
    void c1_recaudadorNoAporta() {
        // Arrange / Act
        BigDecimal monto = calculadora.calcular(montoCongelado, true, true, false);
        // Assert
        assertEquals(new BigDecimal("0.00"), monto);
    }

    @Test
    @DisplayName("C2 - Fuera de término sin prórroga paga recargo (D2 verdadera)")
    void c2_fueraDeTerminoSinProrrogaPagaRecargo() {
        BigDecimal monto = calculadora.calcular(montoCongelado, false, true, false);
        assertEquals(new BigDecimal("1100.00"), monto);
    }

    @Test
    @DisplayName("C3 - En término paga el monto congelado (D2 falsa por fueraDeTermino)")
    void c3_enTerminoPagaMontoCongelado() {
        BigDecimal monto = calculadora.calcular(montoCongelado, false, false, false);
        assertEquals(new BigDecimal("1000.00"), monto);
    }

    @Test
    @DisplayName("C4 - Fuera de término CON prórroga no paga recargo (D2 falsa por prórroga)")
    void c4_fueraDeTerminoConProrrogaNoPagaRecargo() {
        BigDecimal monto = calculadora.calcular(montoCongelado, false, true, true);
        assertEquals(new BigDecimal("1000.00"), monto);
    }
}
