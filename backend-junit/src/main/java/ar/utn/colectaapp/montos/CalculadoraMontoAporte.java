package ar.utn.colectaapp.montos;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Módulo 5 - Congelamiento de montos.
 *
 * Convierte el aporte expresado en unidades de referencia (litros de nafta,
 * dólares MEP, cajitas felices) a pesos, usando el precio congelado el día
 * hábil anterior a la colecta.
 */
public class CalculadoraMontoAporte {

    /**
     * @param unidades         cantidad de unidades de referencia que aporta cada participante
     * @param precioReferencia precio en pesos de UNA unidad, congelado el día hábil anterior
     * @return monto en pesos redondeado a 2 decimales (centavos)
     * @throws IllegalArgumentException si algún dato es nulo, cero o negativo
     */
    public BigDecimal calcular(BigDecimal unidades, BigDecimal precioReferencia) {
        if (unidades == null || precioReferencia == null) {
            throw new IllegalArgumentException(
                    "Las unidades y el precio de referencia son obligatorios");
        }
        if (unidades.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Las unidades de referencia deben ser mayores a cero");
        }
        if (precioReferencia.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El precio de referencia debe ser mayor a cero");
        }
        return unidades.multiply(precioReferencia).setScale(2, RoundingMode.HALF_UP);
    }
}
