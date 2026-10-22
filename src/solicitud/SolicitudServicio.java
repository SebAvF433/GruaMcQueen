package solicitud;

import auth.Usuario;
import vehiculo.Vehiculo;
import vehiculo.VehiculoServicio;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
import inventario.EstadoGrua;
import inventario.Grua;
import inventario.GruaServicio;
import java.util.List;
import java.util.stream.Collectors;

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
     * Crea una solicitud en estado SOLICITADA.
     * Si algo no se cumple lanza una excepción y NO guarda nada.
     */

    public Solicitud crearSolicitud(Usuario cliente, String placa, String origen, String destino) {
        // 1. Datos básicos
        if (placa.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar la placa del vehículo.");
        }
        if (origen.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el origen.");
        }
        if (destino.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el destino.");
        }
        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            throw new DatoInvalidoException("El origen y el destino no pueden ser iguales.");
        }

        // 2. El vehículo debe existir y ser del cliente que solicita
        String placaNormalizada = placa.trim().toUpperCase();
        Vehiculo vehiculo = vehiculoServicio.buscarPorPlaca(placaNormalizada)
                .filter(v -> v.getPropietario().getUsuario().equals(cliente.getUsuario()))
                .orElseThrow(() -> 
                new ReglaNegocioException("No se encontró un vehículo con la placa " + placaNormalizada + " entre sus vehículos."));

        // 3. El vehículo no puede tener otra solicitud activa
        if (solicitudActiva(placaNormalizada)) {
            throw new ReglaNegocioException("El vehículo " + placaNormalizada + " ya tiene una solicitud activa.");
        }

        // 4. Todo está bien: se crea y se guarda
        Solicitud solicitud = new Solicitud(
            repositorio.siguienteId(), cliente, vehiculo, origen.trim(), destino.trim());
            repositorio.guardar(solicitud);
            return solicitud;
    }

    /** Solicitudes del cliente (vacía si todavía no ha hecho ninguna). */
    public List<Solicitud> listarPorCliente(Usuario cliente) {
        return repositorio.listarPorCliente(cliente);
    }

    /** Todas las solicitudes, en el orden en que se crearon. */
    public List<Solicitud> listar() {
        return repositorio.listaSolicitudes();
    }

    /** Solicitudes que están en el estado indicado (lista vacía si no hay ninguna). */
    public List<Solicitud> filtrarPorEstado(EstadoSolicitud estado) {
        return repositorio.listaSolicitudes().stream()
                .filter(s -> s.getEstado() == estado)
                .collect(Collectors.toList());
    }

    /** Solicitudes del cliente con ese nombre de usuario (lista vacía si no tiene ninguna). */
    public List<Solicitud> filtrarPorCliente(String usuario) {
        if (usuario.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el usuario del cliente.");
        }
        return repositorio.listaSolicitudes().stream()
                .filter(s -> s.getCliente().getUsuario().equalsIgnoreCase(usuario.trim()))
                .collect(Collectors.toList());
    }

    /**
     * Cancela una solicitud del cliente mientras el servicio no haya empezado.
     * Si algo no se cumple lanza una excepción y NO modifica nada.
     */
    public Solicitud cancelarSolicitud(String id, Usuario cliente) {
        // 1. Datos básicos
        if (id.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el identificador de la solicitud.");
        }

        // 2. La solicitud debe existir y ser del cliente (mismo mensaje para ajena e inexistente)
        Solicitud solicitud = repositorio.listaSolicitudes().stream()
                .filter(s -> s.getId().equalsIgnoreCase(id.trim()))
                .filter(s -> s.getCliente().getUsuario().equals(cliente.getUsuario()))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "No se encontró la solicitud " + id.trim() + " entre sus solicitudes."));

        // 3. Solo se puede cancelar antes de que el servicio empiece
        EstadoSolicitud estado = solicitud.getEstado();
        if (estado == EstadoSolicitud.EN_CURSO) {
            throw new ReglaNegocioException("No se puede cancelar: el servicio ya está en curso.");
        }
        if (estado == EstadoSolicitud.FINALIZADA) {
            throw new ReglaNegocioException("No se puede cancelar: el servicio ya fue finalizado.");
        }
        if (estado == EstadoSolicitud.CANCELADA) {
            throw new ReglaNegocioException("La solicitud ya estaba cancelada.");
        }

        // 4. Si ya tenía grúa asignada, la grúa vuelve a quedar disponible
        if (estado == EstadoSolicitud.ASIGNADA && solicitud.getGrua() != null) {
            gruaServicio.cambiarEstado(solicitud.getGrua().getPlaca(), EstadoGrua.DISPONIBLE);
        }

        // 5. Se cancela (al final, para que un fallo anterior no deje la solicitud a medias)
        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        return solicitud;
    }

    /**
     * Asigna a una solicitud SOLICITADA la primera grúa disponible y compatible con su vehículo.
     * Si algo no se cumple lanza una excepción y NO modifica nada.
     */
    public Solicitud asignarGrua(String idSolicitud) {
        // 1. Datos básicos
        if (idSolicitud.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el identificador de la solicitud.");
        }

        // 2. La solicitud debe existir (el administrador puede asignar cualquiera)
        Solicitud solicitud = repositorio.listaSolicitudes().stream()
                .filter(s -> s.getId().equalsIgnoreCase(idSolicitud.trim()))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe la solicitud " + idSolicitud.trim() + "."));

        // 3. Solo se asigna una solicitud que todavía está SOLICITADA
        if (solicitud.getEstado() != EstadoSolicitud.SOLICITADA) {
            throw new ReglaNegocioException("Solo se puede asignar una solicitud en estado SOLICITADA; "
                    + "esta está en estado " + solicitud.getEstado() + ".");
        }

        // 4. Debe haber una grúa disponible y compatible con el tipo de vehículo
        Grua grua = gruaServicio.buscarDisponibleCompatible(solicitud.getVehiculo().getTipo())
                .orElseThrow(() -> new ReglaNegocioException(
                        "No hay una grúa disponible compatible con el vehículo ("
                        + solicitud.getVehiculo().getTipo() + "). La solicitud sigue en SOLICITADA."));

        // 5. Se actualizan la grúa y la solicitud (la grúa primero: si falla, la solicitud no cambia)
        gruaServicio.cambiarEstado(grua.getPlaca(), EstadoGrua.EN_SERVICIO);
        solicitud.setGrua(grua);
        solicitud.setEstado(EstadoSolicitud.ASIGNADA);
        return solicitud;
    }

    /**
     * Pasa una solicitud de ASIGNADA a EN_CURSO.
     * Si algo no se cumple lanza una excepción y NO modifica nada.
     */
    public Solicitud iniciarServicio(String id) {
        Solicitud solicitud = buscarPorId(id);

        if (solicitud.getEstado() != EstadoSolicitud.ASIGNADA) {
            throw new ReglaNegocioException("Solo se puede iniciar una solicitud en estado ASIGNADA; "
                    + "esta está en estado " + solicitud.getEstado() + ".");
        }

        solicitud.setEstado(EstadoSolicitud.EN_CURSO); // la grúa sigue EN_SERVICIO
        return solicitud;
    }

    /**
     * Pasa una solicitud de EN_CURSO a FINALIZADA y devuelve su grúa a DISPONIBLE.
     * Si algo no se cumple lanza una excepción y NO modifica nada.
     */
    public Solicitud finalizarServicio(String id) {
        Solicitud solicitud = buscarPorId(id);

        if (solicitud.getEstado() != EstadoSolicitud.EN_CURSO) {
            throw new ReglaNegocioException("Solo se puede finalizar una solicitud en estado EN_CURSO; "
                    + "esta está en estado " + solicitud.getEstado() + ".");
        }

        // La grúa primero: si falla, la solicitud no queda FINALIZADA con la grúa todavía ocupada
        if (solicitud.getGrua() != null) {
            gruaServicio.cambiarEstado(solicitud.getGrua().getPlaca(), EstadoGrua.DISPONIBLE);
        }
        solicitud.setEstado(EstadoSolicitud.FINALIZADA);

        // HU-19: aquí se generará la factura de la solicitud ya finalizada:
        // facturaServicio.generar(solicitud);

        return solicitud;
    }

    /** Busca una solicitud por su identificador (sin distinguir mayúsculas). */
    private Solicitud buscarPorId(String id) {
        if (id.trim().isEmpty()) {
            throw new DatoInvalidoException("Debe indicar el identificador de la solicitud.");
        }
        return repositorio.listaSolicitudes().stream()
                .filter(s -> s.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe la solicitud " + id.trim() + "."));
    }

    private boolean solicitudActiva(String placa) {
        return repositorio.listaSolicitudes().stream()
                .anyMatch(s -> s.getVehiculo().getPlaca().equals(placa)
                            && s.getEstado().estadoActivo());
    }
}