package ar.utn.colectaapp.aportes;

/**
 * Módulo 6 - Aportes y Pagos.
 * Coordina el cobro de un aporte contra la pasarela externa.
 */
public class AporteService {

    private final AporteRepository repositorio;
    private final PasarelaPagos pasarela;

    // Inyección de dependencias por constructor: el test decide qué implementación recibe.
    public AporteService(AporteRepository repositorio, PasarelaPagos pasarela) {
        this.repositorio = repositorio;
        this.pasarela = pasarela;
    }

    public Aporte pagarAporte(String aporteId) {
        Aporte aporte = repositorio.buscarPorId(aporteId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el aporte " + aporteId));

        // Regla de idempotencia: un aporte ya pagado NUNCA se vuelve a cobrar.
        if (aporte.estaPagado()) {
            return aporte;
        }

        ResultadoPago resultado;
        try {
            // Se cobra el monto CONGELADO, no uno recalculado con el precio de hoy.
            resultado = pasarela.cobrar(aporte.getId(), aporte.getMontoCongelado());
        } catch (PasarelaNoDisponibleException e) {
            // El aporte queda PENDIENTE (no se guarda nada) y se informa con una excepción controlada.
            throw new PagoNoProcesadoException(
                    "No se pudo procesar el pago del aporte " + aporteId + ": pasarela no disponible", e);
        }

        if (resultado.aprobado()) {
            aporte.marcarPagado(resultado.idTransaccion());
        } else {
            aporte.marcarRechazado(resultado.motivo());
        }
        repositorio.guardar(aporte);
        return aporte;
    }
}
