package test;

import modelo.Rol;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import solicitud.EstadoSolicitud;
import solicitud.Solicitud;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import vehiculo.TipoVehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;
import java.util.List;

/** Pruebas de las solicitudes: HU-13 (solicitar) y HU-14 (consultar mis solicitudes). */
public class SolicitudTest {

    static int ok = 0, fallos = 0;

    static void verificar(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("[OK]    " + nombre); }
        else { fallos++; System.out.println("[FALLO] " + nombre); }
    }

    public static void main(String[] args) {
        ejecutar();
        System.out.println("\nPasaron: " + ok + " | Fallaron: " + fallos);
    }

    public static void ejecutar() {
        probarSolicitar();
        probarListarPorCliente();
    }

    // ---------------------------------------------------------------- HU-13
    private static void probarSolicitar() {
        System.out.println("\n== Solicitudes (HU-13) ==");

        Usuario ana = new Usuario("Ana", "ana", "1234", Rol.CLIENTE);
        Usuario luis = new Usuario("Luis", "luis", "1234", Rol.CLIENTE);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        vehiculos.registrar(ana, "ABC123", TipoVehiculo.AUTOMOVIL, "Renault");
        vehiculos.registrar(luis, "XYZ789", TipoVehiculo.CAMION, "Chevrolet");

        SolicitudRepositorio repo = new SolicitudRepositorio();
        SolicitudServicio servicio = new SolicitudServicio(repo, vehiculos, new GruaServicio(new GruaRepositorio()));

        Solicitud s1 = servicio.crearSolicitud(ana, "ABC123", "Calle 10", "Calle 80", 10);
        verificar("Normal: la solicitud queda SOLICITADA",
                s1.getEstado() == EstadoSolicitud.SOLICITADA);
        verificar("Normal: se guarda y recibe el id SOL-001",
                repo.listar().size() == 1 && s1.getId().equals("SOL-001"));

        verificar("Activa duplicada: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "ABC123", "Calle 1", "Calle 2", 10)));
        verificar("Vehículo ajeno: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "XYZ789", "Calle 1", "Calle 2", 10)));
        verificar("Vehículo inexistente: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "QQQ000", "Calle 1", "Calle 2", 10)));
        verificar("Origen vacío: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "ABC123", "  ", "Calle 2", 10)));
        verificar("Destino vacío: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "ABC123", "Calle 1", "", 10)));
        verificar("Origen igual al destino: se rechaza",
                lanza(() -> servicio.crearSolicitud(ana, "ABC123", "Calle 1", "calle 1", 10)));
        verificar("Rechazadas: no se guarda ninguna (sigue 1)",
                repo.listar().size() == 1);

        s1.setEstado(EstadoSolicitud.CANCELADA);
        Solicitud s2 = servicio.crearSolicitud(ana, "abc123", "Calle 5", "Calle 50", 10);
        verificar("Tras cancelar, el vehículo puede solicitar otra (placa en minúsculas)",
                s2.getVehiculo().getPlaca().equals("ABC123"));
        verificar("Las rechazadas no consumen ids: la siguiente es SOL-002",
                s2.getId().equals("SOL-002") && repo.listar().size() == 2);
    }

    // ---------------------------------------------------------------- HU-14
    private static void probarListarPorCliente() {
        System.out.println("\n== Solicitudes (HU-14) ==");

        // Tres clientes: Ana (2 vehículos), Luis (1 vehículo) y Marta (sin solicitudes)
        Usuario ana = new Usuario("Ana", "ana", "1234", Rol.CLIENTE);
        Usuario luis = new Usuario("Luis", "luis", "1234", Rol.CLIENTE);
        Usuario marta = new Usuario("Marta", "marta", "1234", Rol.CLIENTE);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        vehiculos.registrar(ana, "ABC123", TipoVehiculo.AUTOMOVIL, "Renault");
        vehiculos.registrar(ana, "DEF456", TipoVehiculo.MOTO, "Yamaha");
        vehiculos.registrar(luis, "XYZ789", TipoVehiculo.CAMION, "Chevrolet");

        SolicitudServicio servicio = new SolicitudServicio(new SolicitudRepositorio(), vehiculos, new GruaServicio(new GruaRepositorio()));
        Solicitud a1 = servicio.crearSolicitud(ana, "ABC123", "Calle 10", "Calle 80", 10);
        servicio.crearSolicitud(ana, "DEF456", "Carrera 7", "Calle 100", 10);
        servicio.crearSolicitud(luis, "XYZ789", "Avenida Boyacá", "Calle 26", 10);

        // Con solicitudes
        List<Solicitud> deAna = servicio.listarPorCliente(ana);
        verificar("Con solicitudes: Ana ve sus 2 solicitudes", deAna.size() == 2);
        verificar("Con solicitudes: salen en orden de creación (SOL-001, SOL-002)",
                deAna.get(0).getId().equals("SOL-001") && deAna.get(1).getId().equals("SOL-002"));

        // Dos clientes distintos
        verificar("Dos clientes: todas las solicitudes de Ana son de Ana",
                deAna.stream().allMatch(s -> s.getCliente().getUsuario().equals("ana")));
        List<Solicitud> deLuis = servicio.listarPorCliente(luis);
        verificar("Dos clientes: Luis ve solo la suya (SOL-003)",
                deLuis.size() == 1 && deLuis.get(0).getId().equals("SOL-003"));

        // Sin solicitudes
        verificar("Sin solicitudes: Marta recibe una lista vacía",
                servicio.listarPorCliente(marta).isEmpty());

        // Estado actual
        a1.setEstado(EstadoSolicitud.CANCELADA);
        verificar("Estado actual: el listado refleja el cambio de estado",
                servicio.listarPorCliente(ana).get(0).getEstado() == EstadoSolicitud.CANCELADA);
    }

    /** true si la acción lanza una de las excepciones de validación del proyecto. */
    private static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (ReglaNegocioException | DatoInvalidoException e) {
            return true;
        }
    }
}
