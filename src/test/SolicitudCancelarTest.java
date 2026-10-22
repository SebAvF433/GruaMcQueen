package test;

import modelo.Rol;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import inventario.EstadoGrua;
import inventario.Grua;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import inventario.TipoGrua;
import solicitud.EstadoSolicitud;
import solicitud.Solicitud;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import vehiculo.TipoVehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;

/** Pruebas de la HU-15 (cancelar una solicitud). Se puede ejecutar sola con main(). */
public class SolicitudCancelarTest {

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
        System.out.println("\n== Solicitudes (HU-15) ==");

        // --- Preparación (si algún nombre de su proyecto es distinto, se ajusta solo aquí) ---
        Usuario ana = new Usuario("Ana", "ana", "1234", Rol.CLIENTE);
        Usuario luis = new Usuario("Luis", "luis", "1234", Rol.CLIENTE);

        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        vehiculos.registrar(ana, "AAA111", TipoVehiculo.AUTOMOVIL, "Renault");
        vehiculos.registrar(ana, "BBB222", TipoVehiculo.MOTO, "Yamaha");
        vehiculos.registrar(ana, "CCC333", TipoVehiculo.CAMIONETA, "Toyota");
        vehiculos.registrar(ana, "DDD444", TipoVehiculo.AUTOMOVIL, "Mazda");
        vehiculos.registrar(ana, "EEE555", TipoVehiculo.AUTOMOVIL, "Kia");

        GruaServicio gruas = new GruaServicio(new GruaRepositorio());
        Grua grua = new Grua("GRU111", TipoGrua.PLATAFORMA);
        gruas.registrar(grua);

        SolicitudServicio servicio =
                new SolicitudServicio(new SolicitudRepositorio(), vehiculos, gruas);

        Solicitud sSolicitada = servicio.crearSolicitud(ana, "AAA111", "Calle 1", "Calle 2", 10);
        Solicitud sAsignada   = servicio.crearSolicitud(ana, "BBB222", "Calle 3", "Calle 4", 10);
        Solicitud sEnCurso    = servicio.crearSolicitud(ana, "CCC333", "Calle 5", "Calle 6", 10);
        Solicitud sFinalizada = servicio.crearSolicitud(ana, "DDD444", "Calle 7", "Calle 8", 10);

        // Se deja cada solicitud en el estado que se quiere probar (la asignación real es la HU-16)
        grua.setEstado(EstadoGrua.EN_SERVICIO);
        sAsignada.setGrua(grua);
        sAsignada.setEstado(EstadoSolicitud.ASIGNADA);
        sEnCurso.setEstado(EstadoSolicitud.EN_CURSO);
        sFinalizada.setEstado(EstadoSolicitud.FINALIZADA);

        // --- Cancelar SOLICITADA ---
        servicio.cancelarSolicitud(sSolicitada.getId(), ana);
        verificar("SOLICITADA: pasa a CANCELADA",
                sSolicitada.getEstado() == EstadoSolicitud.CANCELADA);

        // --- Cancelar ASIGNADA: la grúa vuelve a DISPONIBLE ---
        verificar("Antes de cancelar ASIGNADA, la grúa está EN_SERVICIO",
                grua.getEstado() == EstadoGrua.EN_SERVICIO);
        servicio.cancelarSolicitud(sAsignada.getId(), ana);
        verificar("ASIGNADA: pasa a CANCELADA",
                sAsignada.getEstado() == EstadoSolicitud.CANCELADA);
        verificar("ASIGNADA: su grúa vuelve a DISPONIBLE",
                grua.getEstado() == EstadoGrua.DISPONIBLE);

        // --- EN_CURSO y FINALIZADA no se pueden cancelar ---
        grua.setEstado(EstadoGrua.EN_SERVICIO); // simula la grúa ocupada con el servicio en curso
        sEnCurso.setGrua(grua);
        verificar("EN_CURSO: se rechaza",
                lanza(() -> servicio.cancelarSolicitud(sEnCurso.getId(), ana)));
        verificar("EN_CURSO: no cambia el estado ni libera la grúa",
                sEnCurso.getEstado() == EstadoSolicitud.EN_CURSO
                && grua.getEstado() == EstadoGrua.EN_SERVICIO);
        verificar("FINALIZADA: se rechaza",
                lanza(() -> servicio.cancelarSolicitud(sFinalizada.getId(), ana)));
        verificar("FINALIZADA: no cambia el estado",
                sFinalizada.getEstado() == EstadoSolicitud.FINALIZADA);

        // --- Ya cancelada ---
        verificar("Ya CANCELADA: se rechaza",
                lanza(() -> servicio.cancelarSolicitud(sSolicitada.getId(), ana)));

        // --- Ajena, inexistente e identificador vacío ---
        Solicitud sAjena = servicio.crearSolicitud(ana, "EEE555", "Calle 9", "Calle 10", 10);
        verificar("Ajena: otro cliente no puede cancelarla",
                lanza(() -> servicio.cancelarSolicitud(sAjena.getId(), luis)));
        verificar("Ajena: sigue SOLICITADA (sin modificar nada)",
                sAjena.getEstado() == EstadoSolicitud.SOLICITADA);
        verificar("Inexistente: se rechaza",
                lanza(() -> servicio.cancelarSolicitud("SOL-999", ana)));
        verificar("Identificador vacío: se rechaza",
                lanza(() -> servicio.cancelarSolicitud("  ", ana)));

        // --- El identificador no distingue mayúsculas, y tras cancelar se puede volver a solicitar ---
        servicio.cancelarSolicitud(sAjena.getId().toLowerCase(), ana);
        verificar("Identificador en minúsculas: también cancela",
                sAjena.getEstado() == EstadoSolicitud.CANCELADA);
        Solicitud nueva = servicio.crearSolicitud(ana, "EEE555", "Calle 9", "Calle 10", 10);
        verificar("Tras cancelar, el vehículo puede solicitar otra grúa",
                nueva.getEstado() == EstadoSolicitud.SOLICITADA);
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
