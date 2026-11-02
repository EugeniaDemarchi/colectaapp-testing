package ar.utn.colectaapp.aportes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PUNTO 2 - STUBS: inyectan ENTRADAS INDIRECTAS (la respuesta de la pasarela).
 * Pregunta que responden: "¿cómo reacciona mi servicio ante esta respuesta?"
 * El Assert mira el ESTADO del aporte, no la interacción con la pasarela.
 *
 * Los stubs están escritos a mano como lambdas para que se vea que no tienen lógica:
 * devuelven siempre la misma respuesta "enlatada".
 */
@DisplayName("Punto 2 - AporteService con STUBS de la pasarela")
class AporteServiceStubTest {

    private RepositorioAportesEnMemoria repositorio;

    @BeforeEach
    void setUp() {
        repositorio = new RepositorioAportesEnMemoria();
        repositorio.guardar(new Aporte("AP-1", "padre-7", new BigDecimal("4351.50")));
    }

    @Test
    @DisplayName("Pasarela aprueba -> el aporte queda PAGADO con el id de transacción")
    void pagoAprobadoMarcaAportePagado() {
        // Arrange: stub que simula "Mercado Pago aprobó"
        PasarelaPagos stubAprueba = (ref, monto) -> ResultadoPago.aprobado("MP-TX-001");
        AporteService servicio = new AporteService(repositorio, stubAprueba);

        // Act
        Aporte resultado = servicio.pagarAporte("AP-1");

        // Assert (estado)
        assertAll(
                () -> assertEquals(EstadoAporte.PAGADO, resultado.getEstado()),
                () -> assertEquals("MP-TX-001", resultado.getIdTransaccion()));
    }

    @Test
    @DisplayName("Pasarela rechaza -> el aporte queda RECHAZADO con el motivo")
    void pagoRechazadoMarcaAporteRechazado() {
        // Arrange: stub que simula "fondos insuficientes"
        PasarelaPagos stubRechaza = (ref, monto) -> ResultadoPago.rechazado("Fondos insuficientes");
        AporteService servicio = new AporteService(repositorio, stubRechaza);

        // Act
        Aporte resultado = servicio.pagarAporte("AP-1");

        // Assert (estado)
        assertAll(
                () -> assertEquals(EstadoAporte.RECHAZADO, resultado.getEstado()),
                () -> assertEquals("Fondos insuficientes", resultado.getMotivoRechazo()));
    }

    @Test
    @DisplayName("Pasarela caída -> excepción controlada y el aporte sigue PENDIENTE")
    void pasarelaCaidaDejaAportePendiente() {
        // Arrange: stub que simula un timeout de la API externa
        PasarelaPagos stubCaido = (ref, monto) -> {
            throw new PasarelaNoDisponibleException("timeout 30s");
        };
        AporteService servicio = new AporteService(repositorio, stubCaido);

        // Act
        PagoNoProcesadoException error = assertThrows(PagoNoProcesadoException.class,
                () -> servicio.pagarAporte("AP-1"));

        // Assert
        assertAll(
                () -> assertTrue(error.getMessage().contains("pasarela no disponible")),
                () -> assertEquals(EstadoAporte.PENDIENTE,
                        repositorio.buscarPorId("AP-1").orElseThrow().getEstado()));
    }
}
