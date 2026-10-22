package solicitud;

import auth.Usuario;
import comun.Validador;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import inventario.EstadoGrua;
import inventario.Grua;
import inventario.GruaServicio;
import vehiculo.Vehiculo;
import vehiculo.VehiculoServicio;

import java.util.List;

/** Reglas de negocio de las solicitudes de servicio. */
public class SolicitudServicio {

    private final SolicitudRepositorio repositorio;
    private final VehiculoServicio vehiculoServicio;
    private final GruaServicio gruaServicio;

    public SolicitudServicio(SolicitudRepositorio repositorio, VehiculoServicio vehiculoServicio,
                             GruaServicio gruaServicio) {
        this.repositorio = repositorio;
        this.vehiculoServicio = vehiculoServicio;
        this.gruaServicio = gruaServicio;
    }

    /**
     * HU-13. Crea una solicitud en estado SOLICITADA.
     * Si algo no se cumple lanza una excepción y NO guarda nada.
     */
    public Solicitud crearSolicitud(Usuario cliente, String placa, String origen, String destino, double km) {
        // 1. Datos básicos
        if (placa == null || placa.isBlank()) {
            throw new DatoInvalidoException("Debe indicar la placa del vehículo.");
        }
        if (origen == null || origen.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el origen.");
        }
        if (destino == null || destino.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el destino.");
        }
        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            throw new DatoInvalidoException("El origen y el destino no pueden ser iguales.");
        }
        if (!Validador.kmValido(km)) {
            throw new DatoInvalidoException("Los kilómetros deben ser mayores a 0 y no superar 500.");
        }

        // 2. El vehículo debe existir y ser del cliente que solicita
        String placaNormalizada = placa.trim().toUpperCase();
        Vehiculo vehiculo = vehiculoServicio.buscarPorPlaca(placaNormalizada)
                .filter(v -> v.getPropietario().getUsuario().equals(cliente.getUsuario()))
                .orElseThrow(() -> new ReglaNegocioException(
                        "No se encontró un vehículo con la placa " + placaNormalizada + " entre sus vehículos."));

        // 3. El vehículo no puede tener otra solicitud activa
        if (tieneSolicitudActiva(placaNormalizada)) {
            throw new ReglaNegocioException("El vehículo " + placaNormalizada + " ya tiene una solicitud activa.");
        }

        // 4. Todo está bien: se crea y se guarda
        Solicitud solicitud = new Solicitud(
                repositorio.siguienteId(), cliente, vehiculo, origen.trim(), destino.trim(), km);
        repositorio.guardar(solicitud);
        return solicitud;
    }

    /** HU-14. Solo las solicitudes de ese cliente, en orden de creación. */
    public List<Solicitud> listarPorCliente(Usuario cliente) {
        return repositorio.listar().stream()
                .filter(s -> s.getCliente().getUsuario().equals(cliente.getUsuario()))
                .toList();
    }

    /**
     * HU-15. Cancela una solicitud SOLICITADA o ASIGNADA del propio cliente.
     * Si estaba ASIGNADA, su grúa vuelve a DISPONIBLE.
     */
    public void cancelarSolicitud(String id, Usuario cliente) {
        Solicitud solicitud = buscarSolicitud(id);
        if (!solicitud.getCliente().getUsuario().equals(cliente.getUsuario())) {
            throw new ReglaNegocioException("No se encontró la solicitud " + id.trim() + " entre sus solicitudes.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.SOLICITADA
                && solicitud.getEstado() != EstadoSolicitud.ASIGNADA) {
            throw new ReglaNegocioException("No se puede cancelar una solicitud " + solicitud.getEstado().getEtiqueta()
                    + ". Solo se cancelan las SOLICITADAS o ASIGNADAS.");
        }

        liberarGrua(solicitud);
        solicitud.setEstado(EstadoSolicitud.CANCELADA);
    }

    /** HU-16. Asigna a una solicitud SOLICITADA una grúa disponible y compatible con su vehículo. */
    public void asignarGrua(String id) {
        Solicitud solicitud = buscarSolicitud(id);
        if (solicitud.getEstado() != EstadoSolicitud.SOLICITADA) {
            throw new ReglaNegocioException("Solo se asigna grúa a una solicitud SOLICITADA (la solicitud "
                    + solicitud.getId() + " está " + solicitud.getEstado().getEtiqueta() + ").");
        }

        Grua grua = gruaServicio.buscarDisponibleCompatible(solicitud.getVehiculo().getTipo())
                .orElseThrow(() -> new ReglaNegocioException(
                        "No hay una grúa disponible compatible con el vehículo " + solicitud.getVehiculo().getPlaca()
                        + ". La solicitud sigue SOLICITADA."));

        gruaServicio.cambiarEstado(grua.getPlaca(), EstadoGrua.EN_SERVICIO);
        solicitud.setGrua(grua);
        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
    }

    /** HU-17. ASIGNADA pasa a EN_CURSO. */
    public void iniciarServicio(String id) {
        Solicitud solicitud = buscarSolicitud(id);
        if (solicitud.getEstado() != EstadoSolicitud.ASIGNADA) {
            throw new ReglaNegocioException("Solo se inicia una solicitud ASIGNADA (la solicitud "
                    + solicitud.getId() + " está " + solicitud.getEstado().getEtiqueta() + ").");
        }
        solicitud.setEstado(EstadoSolicitud.EN_CURSO);
    }

    /** HU-17. EN_CURSO pasa a FINALIZADA y su grúa vuelve a DISPONIBLE. */
    public void finalizarServicio(String id) {
        Solicitud solicitud = buscarSolicitud(id);
        if (solicitud.getEstado() != EstadoSolicitud.EN_CURSO) {
            throw new ReglaNegocioException("Solo se finaliza una solicitud EN CURSO (la solicitud "
                    + solicitud.getId() + " está " + solicitud.getEstado().getEtiqueta() + ").");
        }

        liberarGrua(solicitud);
        solicitud.setEstado(EstadoSolicitud.FINALIZADA);
    }

    /** HU-18. Todas las solicitudes, en orden de creación (lista vacía si no hay). */
    public List<Solicitud> listar() {
        return repositorio.listar();
    }

    /** HU-18. Solo las solicitudes en ese estado. */
    public List<Solicitud> filtrarPorEstado(EstadoSolicitud estado) {
        return repositorio.listar().stream()
                .filter(s -> s.getEstado() == estado)
                .toList();
    }

    /** HU-18. Solo las solicitudes de ese usuario (no distingue mayúsculas). */
    public List<Solicitud> filtrarPorCliente(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el usuario del cliente.");
        }
        return repositorio.listar().stream()
                .filter(s -> s.getCliente().getUsuario().equalsIgnoreCase(usuario.trim()))
                .toList();
    }

    private Solicitud buscarSolicitud(String id) {
        if (id == null || id.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el identificador de la solicitud.");
        }
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ReglaNegocioException("No existe la solicitud " + id.trim() + "."));
    }

    /** Devuelve la grúa de la solicitud (si tiene) al estado DISPONIBLE. */
    private void liberarGrua(Solicitud solicitud) {
        if (solicitud.getGrua() != null) {
            gruaServicio.cambiarEstado(solicitud.getGrua().getPlaca(), EstadoGrua.DISPONIBLE);
        }
    }

    private boolean tieneSolicitudActiva(String placa) {
        return repositorio.listar().stream()
                .anyMatch(s -> s.getVehiculo().getPlaca().equals(placa)
                            && s.getEstado().estadoActivo());
    }
}
