package ar.utn.colectaapp.montos;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regla de negocio para el monto final que paga cada participante en una colecta.
 *
 *  - D1: el recaudador de turno no aporta a la colecta que él mismo administra.
 *  - D2: si paga fuera de término y NO tiene prórroga, se aplica un recargo del 10%.
 *  - En cualquier otro caso paga el monto congelado.
 *
 * (El recargo es un supuesto de diseño para este práctico.)
 */
public class CalculadoraMontoFinal {

    static final BigDecimal RECARGO = new BigDecimal("1.10");

    public BigDecimal calcular(BigDecimal montoCongelado,
                               boolean esRecaudador,
                               boolean fueraDeTermino,
                               boolean tieneProrroga) {
        if (esRecaudador) {                                  // Decisión D1
            return BigDecimal.ZERO.setScale(2);
        }
        BigDecimal monto = montoCongelado;
        if (fueraDeTermino && !tieneProrroga) {              // Decisión D2 (condición compuesta)
            monto = monto.multiply(RECARGO);
        }
        return monto.setScale(2, RoundingMode.HALF_UP);
    }
}
