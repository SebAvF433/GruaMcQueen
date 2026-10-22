package test;

import auth.Rol;
import auth.Usuario;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
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

/** Pruebas de la HU-16 (asignar una grúa a una solicitud). Se puede ejecutar sola con main(). */
public class SolicitudAsignarTest {

    static int ok = 0, fallos = 0;

    static void verificar(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("[OK]    " + nombre); }
        else { fallos++; System.out.println("[FALLO] " + nombre); }
    }

    /** Un entorno limpio para cada caso, así los casos no se afectan entre sí. */
    private static class Entorno {
        // Si algún nombre de su proyecto es distinto, se ajusta solo en esta clase
        final Usuario ana = new Usuario("U1", "Ana", "ana", "1234", Rol.CLIENTE);
        final VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        final GruaServicio gruas = new GruaServicio(new GruaRepositorio());
        final SolicitudServicio servicio =
                new SolicitudServicio(new SolicitudRepositorio(), vehiculos, gruas);

        Solicitud solicitar(String placa, TipoVehiculo tipo) {
            vehiculos.registrar(ana, placa, tipo, "Marca");
            return servicio.crearSolicitud(ana, placa, "Calle 1", "Calle 2");
        }

        Grua grua(String placa, TipoGrua tipo, EstadoGrua estado) {
            Grua g = new Grua(placa, tipo);
            gruas.registrar(g);
            g.setEstado(estado);
            return g;
        }
    }

    public static void main(String[] args) {
        ejecutar();
        System.out.println("\nPasaron: " + ok + " | Fallaron: " + fallos);
    }

    public static void ejecutar() {
        System.out.println("\n== Solicitudes (HU-16) ==");

        // --- Asignación normal ---
        Entorno e = new Entorno();
        Solicitud s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        Grua g = e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        verificar("Normal: la solicitud pasa a ASIGNADA", s.getEstado() == EstadoSolicitud.ASIGNADA);
        verificar("Normal: la solicitud guarda su grúa", s.getGrua() == g);
        verificar("Normal: la grúa pasa a EN_SERVICIO", g.getEstado() == EstadoGrua.EN_SERVICIO);

        // --- Compatibilidad ---
        e = new Entorno();
        s = e.solicitar("CAM001", TipoVehiculo.CAMION);
        g = e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        e.grua("GRU002", TipoGrua.ARRASTRE, EstadoGrua.DISPONIBLE);
        final Entorno e1 = e; final Solicitud s1 = s;
        verificar("Camión con grúas no pesadas: se rechaza",
                lanza(() -> e1.servicio.asignarGrua(s1.getId())));
        verificar("Camión con grúas no pesadas: la solicitud sigue SOLICITADA y la grúa DISPONIBLE",
                s.getEstado() == EstadoSolicitud.SOLICITADA && g.getEstado() == EstadoGrua.DISPONIBLE);

        e = new Entorno();
        s = e.solicitar("CAM001", TipoVehiculo.CAMION);
        e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        g = e.grua("GRU002", TipoGrua.PESADA, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        verificar("Camión: se le asigna la grúa PESADA aunque haya otra disponible",
                s.getGrua() == g && s.getEstado() == EstadoSolicitud.ASIGNADA);

        e = new Entorno();
        s = e.solicitar("MOT001", TipoVehiculo.MOTO);
        e.grua("GRU001", TipoGrua.PESADA, EstadoGrua.DISPONIBLE);
        final Entorno e2 = e; final Solicitud s2 = s;
        verificar("Moto con solo una grúa PESADA: se rechaza",
                lanza(() -> e2.servicio.asignarGrua(s2.getId())));

        e = new Entorno();
        s = e.solicitar("MOT001", TipoVehiculo.MOTO);
        g = e.grua("GRU001", TipoGrua.ARRASTRE, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        verificar("Moto: una grúa ARRASTRE también sirve", s.getGrua() == g);

        // --- Sin grúas disponibles ---
        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        final Entorno e3 = e; final Solicitud s3 = s;
        verificar("Sin grúas registradas: se rechaza",
                lanza(() -> e3.servicio.asignarGrua(s3.getId())));
        verificar("Sin grúas registradas: la solicitud sigue SOLICITADA",
                s.getEstado() == EstadoSolicitud.SOLICITADA);

        // --- Grúa en mantenimiento o en servicio ---
        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.MANTENIMIENTO);
        final Entorno e4 = e; final Solicitud s4 = s;
        verificar("Grúa en MANTENIMIENTO: no se asigna",
                lanza(() -> e4.servicio.asignarGrua(s4.getId())));

        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.EN_SERVICIO);
        final Entorno e5 = e; final Solicitud s5 = s;
        verificar("Grúa EN_SERVICIO: no se asigna",
                lanza(() -> e5.servicio.asignarGrua(s5.getId())));

        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.EN_SERVICIO);
        e.grua("GRU002", TipoGrua.PLATAFORMA, EstadoGrua.MANTENIMIENTO);
        g = e.grua("GRU003", TipoGrua.ARRASTRE, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        verificar("Entre varias grúas, se asigna solo la que está DISPONIBLE", s.getGrua() == g);

        // --- Solicitud en otro estado, inexistente o identificador vacío ---
        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        e.grua("GRU002", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        Grua primera = s.getGrua();
        final Entorno e6 = e; final Solicitud s6 = s;
        verificar("Solicitud ya ASIGNADA: no se vuelve a asignar",
                lanza(() -> e6.servicio.asignarGrua(s6.getId())));
        verificar("Solicitud ya ASIGNADA: conserva su grúa original", s.getGrua() == primera);
        verificar("Solicitud inexistente: se rechaza",
                lanza(() -> e6.servicio.asignarGrua("SOL-999")));
        verificar("Identificador vacío: se rechaza",
                lanza(() -> e6.servicio.asignarGrua("  ")));

        // --- Integración con la HU-15: cancelar una solicitud asignada libera la grúa ---
        e = new Entorno();
        s = e.solicitar("AAA111", TipoVehiculo.AUTOMOVIL);
        g = e.grua("GRU001", TipoGrua.PLATAFORMA, EstadoGrua.DISPONIBLE);
        e.servicio.asignarGrua(s.getId());
        e.servicio.cancelarSolicitud(s.getId(), e.ana);
        verificar("Asignar y luego cancelar: la grúa vuelve a DISPONIBLE",
                g.getEstado() == EstadoGrua.DISPONIBLE && s.getEstado() == EstadoSolicitud.CANCELADA);
    }

    private static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (ReglaNegocioException | DatoInvalidoException ex) {
            return true;
        }
    }
}
