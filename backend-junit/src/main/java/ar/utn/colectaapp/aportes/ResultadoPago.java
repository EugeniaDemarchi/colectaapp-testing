package ar.utn.colectaapp.aportes;

/** Respuesta de la pasarela de pagos externa (ej. Mercado Pago). */
public record ResultadoPago(boolean aprobado, String idTransaccion, String motivo) {

    public static ResultadoPago aprobado(String idTransaccion) {
        return new ResultadoPago(true, idTransaccion, null);
    }

    public static ResultadoPago rechazado(String motivo) {
        return new ResultadoPago(false, null, motivo);
    }
}
