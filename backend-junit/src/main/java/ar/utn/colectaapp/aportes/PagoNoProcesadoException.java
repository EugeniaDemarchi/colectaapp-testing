package ar.utn.colectaapp.aportes;

/** Excepción controlada que el servicio expone hacia arriba cuando no pudo procesar el pago. */
public class PagoNoProcesadoException extends RuntimeException {
    public PagoNoProcesadoException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
