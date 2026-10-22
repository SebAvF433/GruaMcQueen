package solicitud;

import auth.Usuario;
import vehiculo.Vehiculo;
import vehiculo.VehiculoServicio;

/** Reglas de negocio de las solicitudes de servicio. */
public class SolicitudServicio {

    private final SolicitudRepositorio repositorio;
    private final VehiculoServicio vehiculoServicio;

    public SolicitudServicio(SolicitudRepositorio repositorio, VehiculoServicio vehiculoServicio) {
        this.repositorio = repositorio;
        this.vehiculoServicio = vehiculoServicio;
    }

    /**
     * Crea una solicitud en estado SOLICITADA.
     * Si algo no se cumple lanza una excepción y NO guarda nada.
     */

    public Solicitud crearSolicitud(Usuario cliente, String placa, String origen, String destino) {
        // 1. Datos básicos
        if (placa.isEmpty()) {
            System.out.println("Debe indicar la placa del vehículo.");
        }
        if (origen.isEmpty()) {
            System.out.println("Debe indicar el origen.");
        }

        if (origen.trim().equalsIgnoreCase(destino.trim())) {
            System.out.println("El origen y el destino no pueden ser iguales.");
        }

        // 2. El vehículo debe existir y ser del cliente que solicita
        String placaNormalizada = placa.trim().toUpperCase();
        Vehiculo vehiculo = vehiculoServicio.buscarPorPlaca(placaNormalizada)
                .filter(v -> v.getPropietario().getUsuario().equals(cliente.getUsuario()))
                .orElseThrow(() -> 
                System.out.println("No se encontró un vehículo con la placa " + placaNormalizada + " entre sus vehículos."));

        // 3. El vehículo no puede tener otra solicitud activa
        if (tieneSolicitudActiva(placaNormalizada)) {
            System.out.println("El vehículo " + placaNormalizada + " ya tiene una solicitud activa.");
        }

        // 4. Todo está bien: se crea y se guarda
        Solicitud solicitud = new Solicitud(
            repositorio.siguienteId(), cliente, vehiculo, origen.trim(), destino.trim());
            repositorio.guardar(solicitud);
            return solicitud;
    }

    private boolean tieneSolicitudActiva(String placa) {
        return repositorio.listaSolicitudes().stream()
                .anyMatch(s -> s.getVehiculo().getPlaca().equals(placa)
                            && s.getEstado().estadoActivo());
    }
}