package test;

import auth.Rol;
import auth.Usuario;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
import solicitud.EstadoSolicitudes;
import solicitud.Solicitud;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import vehiculo.TipoVehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;
import java.util.List;

/** Pruebas de las solicitudes: HU-13 (solicitar) y HU-14 (consultar mis solicitudes). */
public class SolicitudTest {

    public static void ejecutar() {
        probarSolicitar();
        probarListarPorCliente();
    }

    // ---------------------------------------------------------------- HU-13
    private static void probarSolicitar() {
        System.out.println("\n== Solicitudes (HU-13) ==");

        Usuario ana = new Usuario("U1", "Ana", "ana", "1234", Rol.cliente);
        Usuario luis = new Usuario("U2", "Luis", "luis", "1234", Rol.cliente);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        vehiculos.registrar(ana, "ABC123", TipoVehiculo.automovil, "Renault");
        vehiculos.registrar(luis, "XYZ789", TipoVehiculo.camion, "Chevrolet");

        SolicitudRepositorio repo = new SolicitudRepositorio();
        SolicitudServicio servicio = new SolicitudServicio(repo, vehiculos);

        Solicitud s1 = servicio.crear(ana, "ABC123", "Calle 10", "Calle 80");
        TestRunner.verificar("Normal: la solicitud queda SOLICITADA",
                s1.getEstado() == EstadoSolicitudes.solicitada);
        TestRunner.verificar("Normal: se guarda y recibe el id SOL-001",
                repo.listar().size() == 1 && s1.getId().equals("SOL-001"));

        TestRunner.verificar("Activa duplicada: se rechaza",
                lanza(() -> servicio.crear(ana, "ABC123", "Calle 1", "Calle 2")));
        TestRunner.verificar("Vehículo ajeno: se rechaza",
                lanza(() -> servicio.crear(ana, "XYZ789", "Calle 1", "Calle 2")));
        TestRunner.verificar("Vehículo inexistente: se rechaza",
                lanza(() -> servicio.crear(ana, "QQQ000", "Calle 1", "Calle 2")));
        TestRunner.verificar("Origen vacío: se rechaza",
                lanza(() -> servicio.crear(ana, "ABC123", "  ", "Calle 2")));
        TestRunner.verificar("Destino vacío: se rechaza",
                lanza(() -> servicio.crear(ana, "ABC123", "Calle 1", "")));
        TestRunner.verificar("Origen igual al destino: se rechaza",
                lanza(() -> servicio.crear(ana, "ABC123", "Calle 1", "calle 1")));
        TestRunner.verificar("Rechazadas: no se guarda ninguna (sigue 1)",
                repo.listar().size() == 1);

        s1.setEstado(EstadoSolicitud.CANCELADA);
        Solicitud s2 = servicio.crear(ana, "abc123", "Calle 5", "Calle 50");
        TestRunner.verificar("Tras cancelar, el vehículo puede solicitar otra (placa en minúsculas)",
                s2.getVehiculo().getPlaca().equals("ABC123"));
        TestRunner.verificar("Las rechazadas no consumen ids: la siguiente es SOL-002",
                s2.getId().equals("SOL-002") && repo.listar().size() == 2);
    }

    // ---------------------------------------------------------------- HU-14
    private static void probarListarPorCliente() {
        System.out.println("\n== Solicitudes (HU-14) ==");

        // Tres clientes: Ana (2 vehículos), Luis (1 vehículo) y Marta (sin solicitudes)
        Usuario ana = new Usuario("U1", "Ana", "ana", "1234", Rol.CLIENTE);
        Usuario luis = new Usuario("U2", "Luis", "luis", "1234", Rol.CLIENTE);
        Usuario marta = new Usuario("U3", "Marta", "marta", "1234", Rol.CLIENTE);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        vehiculos.registrar(ana, "ABC123", TipoVehiculo.AUTOMOVIL, "Renault");
        vehiculos.registrar(ana, "DEF456", TipoVehiculo.MOTO, "Yamaha");
        vehiculos.registrar(luis, "XYZ789", TipoVehiculo.CAMION, "Chevrolet");

        SolicitudServicio servicio = new SolicitudServicio(new SolicitudRepositorio(), vehiculos);
        Solicitud a1 = servicio.crear(ana, "ABC123", "Calle 10", "Calle 80");
        servicio.crear(ana, "DEF456", "Carrera 7", "Calle 100");
        servicio.crear(luis, "XYZ789", "Avenida Boyacá", "Calle 26");

        // Con solicitudes
        List<Solicitud> deAna = servicio.listarPorCliente(ana);
        TestRunner.verificar("Con solicitudes: Ana ve sus 2 solicitudes", deAna.size() == 2);
        TestRunner.verificar("Con solicitudes: salen en orden de creación (SOL-001, SOL-002)",
                deAna.get(0).getId().equals("SOL-001") && deAna.get(1).getId().equals("SOL-002"));

        // Dos clientes distintos
        TestRunner.verificar("Dos clientes: todas las solicitudes de Ana son de Ana",
                deAna.stream().allMatch(s -> s.getCliente().getUsuario().equals("ana")));
        List<Solicitud> deLuis = servicio.listarPorCliente(luis);
        TestRunner.verificar("Dos clientes: Luis ve solo la suya (SOL-003)",
                deLuis.size() == 1 && deLuis.get(0).getId().equals("SOL-003"));

        // Sin solicitudes
        TestRunner.verificar("Sin solicitudes: Marta recibe una lista vacía",
                servicio.listarPorCliente(marta).isEmpty());

        // Estado actual
        a1.setEstado(EstadoSolicitud.CANCELADA);
        TestRunner.verificar("Estado actual: el listado refleja el cambio de estado",
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
