package ar.utn.colectaapp.aportes;

import java.math.BigDecimal;

/** Aporte de un participante a una colecta (documento embebido en colecta_cumpleanos). */
public class Aporte {

    private final String id;
    private final String participanteId;
    private final BigDecimal montoCongelado;
    private EstadoAporte estado = EstadoAporte.PENDIENTE;
    private String idTransaccion;
    private String motivoRechazo;

    public Aporte(String id, String participanteId, BigDecimal montoCongelado) {
        this.id = id;
        this.participanteId = participanteId;
        this.montoCongelado = montoCongelado;
    }

    public void marcarPagado(String idTransaccion) {
        this.estado = EstadoAporte.PAGADO;
        this.idTransaccion = idTransaccion;
        this.motivoRechazo = null;
    }

    public void marcarRechazado(String motivo) {
        this.estado = EstadoAporte.RECHAZADO;
        this.motivoRechazo = motivo;
    }

    public boolean estaPagado() { return estado == EstadoAporte.PAGADO; }

    public String getId() { return id; }
    public String getParticipanteId() { return participanteId; }
    public BigDecimal getMontoCongelado() { return montoCongelado; }
    public EstadoAporte getEstado() { return estado; }
    public String getIdTransaccion() { return idTransaccion; }
    public String getMotivoRechazo() { return motivoRechazo; }
}
