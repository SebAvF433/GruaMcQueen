package test;

import auth.Rol;
import auth.Usuario;
import comun.DatoInvalidoException;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import java.util.List;
import solicitud.EstadoSolicitud;
import solicitud.Solicitud;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import vehiculo.TipoVehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;

/** Pruebas de la HU-18 (consultar y filtrar solicitudes). Se puede ejecutar sola con main(). */
public class SolicitudConsultaTest {

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
        System.out.println("\n== Solicitudes (HU-18) ==");

        // --- Preparación (si algún nombre de su proyecto es distinto, se ajusta solo aquí) ---
        Usuario ana = new Usuario("U1", "Ana", "ana", "1234", Rol.CLIENTE);
        Usuario luis = new Usuario("U2", "Luis", "luis", "1234", Rol.CLIENTE);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        GruaServicio gruas = new GruaServicio(new GruaRepositorio());
        SolicitudServicio servicio = new SolicitudServicio(new SolicitudRepositorio(), vehiculos, gruas);

        // Sin solicitudes todavía
        verificar("Listar sin solicitudes: devuelve una lista vacía, sin error",
                servicio.listar().isEmpty());

        vehiculos.registrar(ana, "AAA111", TipoVehiculo.AUTOMOVIL, "Renault");
        vehiculos.registrar(ana, "BBB222", TipoVehiculo.MOTO, "Yamaha");
        vehiculos.registrar(luis, "CCC333", TipoVehiculo.CAMIONETA, "Toyota");

        Solicitud s1 = servicio.crearSolicitud(ana, "AAA111", "Calle 1", "Calle 2");  // SOLICITADA
        Solicitud s2 = servicio.crearSolicitud(ana, "BBB222", "Calle 3", "Calle 4");  // CANCELADA
        Solicitud s3 = servicio.crearSolicitud(luis, "CCC333", "Calle 5", "Calle 6"); // FINALIZADA
        s2.setEstado(EstadoSolicitud.CANCELADA);
        s3.setEstado(EstadoSolicitud.FINALIZADA);

        // --- Listado completo ---
        List<Solicitud> todas = servicio.listar();
        verificar("Listado completo: muestra las 3 solicitudes de todos los estados", todas.size() == 3);
        verificar("Listado completo: en orden de creación",
                todas.get(0) == s1 && todas.get(1) == s2 && todas.get(2) == s3);

        // --- Filtro por estado ---
        List<Solicitud> solicitadas = servicio.filtrarPorEstado(EstadoSolicitud.SOLICITADA);
        verificar("Filtro por estado SOLICITADA: solo la SOL-001",
                solicitadas.size() == 1 && solicitadas.get(0) == s1);
        verificar("Filtro por estado FINALIZADA: solo la SOL-003",
                servicio.filtrarPorEstado(EstadoSolicitud.FINALIZADA).size() == 1);
        verificar("Filtro por estado EN_CURSO (sin resultados): lista vacía, sin error",
                servicio.filtrarPorEstado(EstadoSolicitud.EN_CURSO).isEmpty());

        // --- Filtro por cliente ---
        List<Solicitud> deAna = servicio.filtrarPorCliente("ana");
        verificar("Filtro por cliente ana: sus 2 solicitudes",
                deAna.size() == 2
                && deAna.stream().allMatch(s -> s.getCliente().getUsuario().equals("ana")));
        verificar("Filtro por cliente luis: su única solicitud", servicio.filtrarPorCliente("luis").size() == 1);
        verificar("Filtro por cliente no distingue mayúsculas (LUIS)",
                servicio.filtrarPorCliente("LUIS").size() == 1);
        verificar("Filtro por cliente sin solicitudes (marta): lista vacía, sin error",
                servicio.filtrarPorCliente("marta").isEmpty());

        boolean rechazaVacio;
        try {
            servicio.filtrarPorCliente("  ");
            rechazaVacio = false;
        } catch (DatoInvalidoException e) {
            rechazaVacio = true;
        }
        verificar("Filtro por cliente con texto vacío: se rechaza", rechazaVacio);
    }
}
