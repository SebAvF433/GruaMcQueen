package solicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** "Base de datos en memoria" de solicitudes. Solo guarda y entrega datos; no aplica reglas. */
public class SolicitudRepositorio {
    private final List<Solicitud> solicitudes = new ArrayList<>();
    private int ultimoNumero = 0;

    /** Entrega el siguiente identificador: SOL-001, SOL-002, ... */
    public String siguienteId() {
        ultimoNumero++;
        return String.format("SOL-%03d", ultimoNumero);
    }

    public void guardar(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    /** Busca por identificador sin distinguir mayúsculas. */
    public Optional<Solicitud> buscarPorId(String id) {
        return solicitudes.stream()
                .filter(s -> s.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    /** Devuelve una copia de la lista: quien la reciba no puede modificar la colección original. */
    public List<Solicitud> listar() {
        return List.copyOf(solicitudes);
    }
}
