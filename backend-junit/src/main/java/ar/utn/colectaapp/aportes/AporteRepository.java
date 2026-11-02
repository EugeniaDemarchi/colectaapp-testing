package ar.utn.colectaapp.aportes;

import java.util.Optional;

/** Acceso a datos de aportes (en producción: MongoDB Atlas). */
public interface AporteRepository {
    Optional<Aporte> buscarPorId(String id);
    void guardar(Aporte aporte);
}
