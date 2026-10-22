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

/** Pruebas de la HU-17 (iniciar y finalizar un servicio). Se puede ejecutar sola con main(). */
public class SolicitudEstadosTest {

    static int ok = 0, fallos = 0;

    static void verificar(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("[OK]    " + nombre); }
        else { fallos++; System.out.println("[FALLO] " + nombre); }
    }

    /** Un entorno limpio para cada caso. Si algún nombre de su proyecto es distinto, se ajusta aquí. */
    private static class Entorno {
        final Usuario ana = new Usuario("Ana", "ana", "1234", Rol.CLIENTE);
        final VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        final GruaServicio gruas = new GruaServicio(new GruaRepositorio());
        final SolicitudServicio servicio =
                new SolicitudServicio(new SolicitudRepositorio(), vehiculos, gruas);

        Solicitud solicitar(String placa) {
            vehiculos.registrar(ana, placa, TipoVehiculo.AUTOMOVIL, "Marca");
            return servicio.crearSolicitud(ana, placa, "Calle 1", "Calle 2", 10);
        }

        Grua grua(String placa) {
            Grua g = new Grua(placa, TipoGrua.PLATAFORMA);
            gruas.registrar(g);
            return g;
        }
    }

    public static void main(String[] args) {
        ejecutar();
        System.out.println("\nPasaron: " + ok + " | Fallaron: " + fallos);
    }

    public static void ejecutar() {
        System.out.println("\n== Solicitudes (HU-17) ==");

        // --- Secuencia completa: SOLICITADA -> ASIGNADA -> EN_CURSO -> FINALIZADA ---
        Entorno e = new Entorno();
        Solicitud s = e.solicitar("AAA111");
        Grua g = e.grua("GRU001");
        e.servicio.asignarGrua(s.getId());
        verificar("Secuencia: tras asignar, está ASIGNADA y la grúa EN_SERVICIO",
                s.getEstado() == EstadoSolicitud.ASIGNADA && g.getEstado() == EstadoGrua.EN_SERVICIO);

        e.servicio.iniciarServicio(s.getId());
        verificar("Iniciar: ASIGNADA pasa a EN_CURSO",
                s.getEstado() == EstadoSolicitud.EN_CURSO);
        verificar("Iniciar: la grúa sigue EN_SERVICIO", g.getEstado() == EstadoGrua.EN_SERVICIO);

        e.servicio.finalizarServicio(s.getId());
        verificar("Finalizar: EN_CURSO pasa a FINALIZADA",
                s.getEstado() == EstadoSolicitud.FINALIZADA);
        verificar("Finalizar: la grúa vuelve a DISPONIBLE", g.getEstado() == EstadoGrua.DISPONIBLE);

        // --- Iniciar una solicitud que no está ASIGNADA ---
        e = new Entorno();
        s = e.solicitar("AAA111");
        final Entorno e1 = e; final Solicitud s1 = s;
        verificar("Iniciar sin asignar (SOLICITADA): se rechaza",
                lanza(() -> e1.servicio.iniciarServicio(s1.getId())));
        verificar("Iniciar sin asignar: sigue SOLICITADA", s.getEstado() == EstadoSolicitud.SOLICITADA);

        s.setEstado(EstadoSolicitud.FINALIZADA);
        verificar("Iniciar una solicitud ya FINALIZADA: se rechaza",
                lanza(() -> e1.servicio.iniciarServicio(s1.getId())));
        verificar("Iniciar una FINALIZADA: sigue FINALIZADA", s.getEstado() == EstadoSolicitud.FINALIZADA);

        s.setEstado(EstadoSolicitud.CANCELADA);
        verificar("Iniciar una solicitud CANCELADA: se rechaza",
                lanza(() -> e1.servicio.iniciarServicio(s1.getId())));

        // --- Finalizar una solicitud que no está EN_CURSO ---
        e = new Entorno();
        s = e.solicitar("AAA111");
        final Entorno e2 = e; final Solicitud s2 = s;
        verificar("Finalizar sin asignar (SOLICITADA): se rechaza",
                lanza(() -> e2.servicio.finalizarServicio(s2.getId())));

        g = e.grua("GRU001");
        e.servicio.asignarGrua(s.getId());
        verificar("Finalizar una ASIGNADA sin iniciarla: se rechaza",
                lanza(() -> e2.servicio.finalizarServicio(s2.getId())));
        verificar("Finalizar rechazado: la solicitud sigue ASIGNADA y la grúa EN_SERVICIO",
                s.getEstado() == EstadoSolicitud.ASIGNADA && g.getEstado() == EstadoGrua.EN_SERVICIO);

        // --- Repetir una operación ya hecha ---
        e.servicio.iniciarServicio(s.getId());
        verificar("Iniciar dos veces: la segunda se rechaza",
                lanza(() -> e2.servicio.iniciarServicio(s2.getId())));
        verificar("Iniciar dos veces: sigue EN_CURSO", s.getEstado() == EstadoSolicitud.EN_CURSO);

        e.servicio.finalizarServicio(s.getId());
        verificar("Finalizar dos veces: la segunda se rechaza",
                lanza(() -> e2.servicio.finalizarServicio(s2.getId())));

        // --- Inexistente e identificador vacío ---
        verificar("Iniciar solicitud inexistente: se rechaza",
                lanza(() -> e2.servicio.iniciarServicio("SOL-999")));
        verificar("Finalizar solicitud inexistente: se rechaza",
                lanza(() -> e2.servicio.finalizarServicio("SOL-999")));
        verificar("Iniciar con identificador vacío: se rechaza",
                lanza(() -> e2.servicio.iniciarServicio("  ")));

        // --- La grúa liberada se puede asignar a otra solicitud ---
        Solicitud otra = e.solicitar("BBB222");
        e.servicio.asignarGrua(otra.getId());
        verificar("Tras finalizar, la misma grúa puede atender otra solicitud",
                otra.getEstado() == EstadoSolicitud.ASIGNADA && otra.getGrua() == g);
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
