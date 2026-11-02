package ar.utn.colectaapp.aportes;

/** La API externa de pagos no respondió (timeout, caída, error de red). */
public class PasarelaNoDisponibleException extends RuntimeException {
    public PasarelaNoDisponibleException(String mensaje) { super(mensaje); }
}
