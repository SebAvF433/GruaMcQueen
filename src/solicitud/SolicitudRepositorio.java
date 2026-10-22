package solicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import auth.Usuario;

/** "Base de datos en memoria" de solicitudes. Solo guarda y entrega datos; no aplica reglas. */
public class SolicitudRepositorio {
    private final List<Solicitud> solicitudes = new ArrayList<>();
    private int ultimoNumero = 0;

    /** Entrega el siguiente identificador: SOL-001, SOL-002, ... */
    public String siguienteId() {
        ultimoNumero++;
        return String.format("SLC-%03d", ultimoNumero);
    }

    public void guardar(Solicitud solicitud){
        solicitudes.add(solicitud);
    }

    /** Devuelve una copia de la lista: quien la reciba no puede modificar la colección original. */
    public List<Solicitud> listaSolicitudes() {
        return List.copyOf(solicitudes);
    }

    public List<Solicitud> listaCliente(Usuario cliente){
    return solicitudes.stream()
            .filter(s -> s.getCliente().getUsuario().equals(cliente.getUsuario()))
            .collect(Collectors.toList());
    }

}