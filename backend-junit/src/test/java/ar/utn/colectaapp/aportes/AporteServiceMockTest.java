package ar.utn.colectaapp.aportes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PUNTO 2 - MOCKS: auditan el PROTOCOLO DE INTERACCIÓN (salidas indirectas).
 * Pregunta que responden: "¿mi servicio le habló a la pasarela como debía?"
 * El Assert interroga al mock con verify(): qué método, con qué argumentos,
 * cuántas veces y en qué orden.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Punto 2 - AporteService con MOCKS de la pasarela")
class AporteServiceMockTest {

    @Mock
    private PasarelaPagos pasarela;

    @Mock
    private AporteRepository repositorio;

    @InjectMocks
    private AporteService servicio;

    @Test
    @DisplayName("Cobra EXACTAMENTE una vez y con el monto CONGELADO")
    void cobraUnaVezConMontoCongelado() {
        // Arrange
        Aporte aporte = new Aporte("AP-1", "padre-7", new BigDecimal("4351.50"));
        when(repositorio.buscarPorId("AP-1")).thenReturn(Optional.of(aporte));
        when(pasarela.cobrar(any(), any())).thenReturn(ResultadoPago.aprobado("MP-TX-001"));

        // Act
        servicio.pagarAporte("AP-1");

        // Assert (interacción)
        verify(pasarela, times(1)).cobrar("AP-1", new BigDecimal("4351.50"));
        verifyNoMoreInteractions(pasarela);
    }

    @Test
    @DisplayName("Si el aporte ya está PAGADO, NUNCA llama a la pasarela (no hay doble cobro)")
    void noCobraDosVeces() {
        // Arrange
        Aporte yaPagado = new Aporte("AP-1", "padre-7", new BigDecimal("4351.50"));
        yaPagado.marcarPagado("MP-TX-000");
        when(repositorio.buscarPorId("AP-1")).thenReturn(Optional.of(yaPagado));

        // Act
        servicio.pagarAporte("AP-1");

        // Assert (interacción que NO debe ocurrir)
        verify(pasarela, never()).cobrar(any(), any());
        verify(repositorio, never()).guardar(any());
    }

    @Test
    @DisplayName("Primero cobra y recién después persiste el resultado")
    void cobraAntesDeGuardar() {
        // Arrange
        Aporte aporte = new Aporte("AP-1", "padre-7", new BigDecimal("4351.50"));
        when(repositorio.buscarPorId("AP-1")).thenReturn(Optional.of(aporte));
        when(pasarela.cobrar(any(), any())).thenReturn(ResultadoPago.aprobado("MP-TX-001"));

        // Act
        servicio.pagarAporte("AP-1");

        // Assert (orden de la secuencia)
        InOrder orden = inOrder(pasarela, repositorio);
        orden.verify(pasarela).cobrar("AP-1", new BigDecimal("4351.50"));
        orden.verify(repositorio).guardar(aporte);
    }

    @Test
    @DisplayName("Si la pasarela falla, no guarda nada en la base")
    void siFallaLaPasarelaNoPersiste() {
        // Arrange
        Aporte aporte = new Aporte("AP-1", "padre-7", new BigDecimal("4351.50"));
        when(repositorio.buscarPorId("AP-1")).thenReturn(Optional.of(aporte));
        when(pasarela.cobrar(any(), any())).thenThrow(new PasarelaNoDisponibleException("timeout"));

        // Act
        assertThrows(PagoNoProcesadoException.class, () -> servicio.pagarAporte("AP-1"));

        // Assert (interacción)
        verify(repositorio, never()).guardar(any());
    }

    @Test
    @DisplayName("Aporte inexistente: no llama a la pasarela")
    void aporteInexistenteNoCobra() {
        // Arrange
        when(repositorio.buscarPorId("NO-EXISTE")).thenReturn(Optional.empty());

        // Act
        assertThrows(IllegalArgumentException.class, () -> servicio.pagarAporte("NO-EXISTE"));

        // Assert
        verifyNoInteractions(pasarela);
    }
}
