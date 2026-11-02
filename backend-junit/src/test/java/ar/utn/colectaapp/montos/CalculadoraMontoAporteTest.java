package ar.utn.colectaapp.montos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PUNTO 1 - Patrón AAA (Arrange / Act / Assert).
 * Cada test verifica UN comportamiento y tiene las tres etapas marcadas.
 */
@DisplayName("Punto 1 - CalculadoraMontoAporte (patrón AAA)")
class CalculadoraMontoAporteTest {

    private CalculadoraMontoAporte calculadora;

    @BeforeEach
    void setUp() {
        // Arrange compartido: cada test recibe una instancia nueva (tests independientes entre sí)
        calculadora = new CalculadoraMontoAporte();
    }

    // ---------- Camino feliz ----------

    @Test
    @DisplayName("3 litros de nafta a $1450,50 dan $4351,50")
    void calculaMontoConUnidadesYPrecioValidos() {
        // Arrange
        BigDecimal litros = new BigDecimal("3");
        BigDecimal precioLitro = new BigDecimal("1450.50");

        // Act
        BigDecimal monto = calculadora.calcular(litros, precioLitro);

        // Assert
        assertEquals(new BigDecimal("4351.50"), monto);
    }

    // ---------- Casos de borde ----------

    @Test
    @DisplayName("Redondea a centavos con HALF_UP (1,5 x 999,99 = 1499,985 -> 1499,99)")
    void redondeaACentavos() {
        // Arrange
        BigDecimal unidades = new BigDecimal("1.5");
        BigDecimal precio = new BigDecimal("999.99");

        // Act
        BigDecimal monto = calculadora.calcular(unidades, precio);

        // Assert
        assertAll("monto redondeado",
                () -> assertEquals(new BigDecimal("1499.99"), monto),
                () -> assertEquals(2, monto.scale(), "siempre 2 decimales"));
    }

    @Test
    @DisplayName("Con la unidad mínima de precio (0,01) no pierde centavos")
    void aceptaPrecioMinimo() {
        // Arrange
        BigDecimal unidades = BigDecimal.ONE;
        BigDecimal precioMinimo = new BigDecimal("0.01");

        // Act
        BigDecimal monto = calculadora.calcular(unidades, precioMinimo);

        // Assert
        assertEquals(new BigDecimal("0.01"), monto);
    }

    // ---------- Inyección de errores controlados ----------

    @Test
    @DisplayName("Precio nulo (índice no congelado) lanza excepción con mensaje claro")
    void precioNuloLanzaExcepcion() {
        // Arrange
        BigDecimal unidades = new BigDecimal("3");
        BigDecimal precioNulo = null;

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(unidades, precioNulo));

        // Assert
        assertEquals("Las unidades y el precio de referencia son obligatorios", error.getMessage());
    }

    @ParameterizedTest(name = "unidades = {0}")
    @ValueSource(strings = {"0", "-1", "-0.5"})
    @DisplayName("Unidades cero o negativas lanzan excepción")
    void unidadesNoPositivasLanzanExcepcion(String valor) {
        // Arrange
        BigDecimal unidades = new BigDecimal(valor);
        BigDecimal precio = new BigDecimal("1450.50");

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(unidades, precio));

        // Assert
        assertEquals("Las unidades de referencia deben ser mayores a cero", error.getMessage());
    }

    @Test
    @DisplayName("Precio cero lanza excepción")
    void precioCeroLanzaExcepcion() {
        // Arrange
        BigDecimal unidades = new BigDecimal("3");
        BigDecimal precioCero = BigDecimal.ZERO;

        // Act
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(unidades, precioCero));

        // Assert
        assertEquals("El precio de referencia debe ser mayor a cero", error.getMessage());
    }
}
