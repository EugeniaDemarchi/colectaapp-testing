package ar.utn.colectaapp.aportes;

import java.math.BigDecimal;

/**
 * Puerto hacia la API externa de pagos.
 * El servicio depende de esta INTERFAZ y no de Mercado Pago directamente:
 * eso es lo que permite reemplazarla por un doble de prueba en los tests.
 */
public interface PasarelaPagos {

    /**
     * @param referenciaExterna identificador del aporte (sirve para idempotencia en la pasarela)
     * @param monto             monto a cobrar en pesos
     * @throws PasarelaNoDisponibleException si la API no responde
     */
    ResultadoPago cobrar(String referenciaExterna, BigDecimal monto);
}
