package ar.utn.colectaapp.aportes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * FAKE (otro tipo de doble de prueba): implementación liviana y funcional del repositorio
 * que guarda en un HashMap en vez de MongoDB. Permite leer el estado final del aporte.
 */
class RepositorioAportesEnMemoria implements AporteRepository {

    private final Map<String, Aporte> datos = new HashMap<>();

    @Override
    public Optional<Aporte> buscarPorId(String id) {
        return Optional.ofNullable(datos.get(id));
    }

    @Override
    public void guardar(Aporte aporte) {
        datos.put(aporte.getId(), aporte);
    }
}
