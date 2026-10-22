package test;

import auth.Usuario;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
import factura.Factura;
import factura.FacturaRepositorio;
import factura.FacturaServicio;
import factura.TarifaServicio;
import solicitud.EstadoSolicitudes;
import solicitud.Solicitud;
import vehiculo.TipoVehiculo;
import vehiculo.Vehiculo;

import java.time.LocalDateTime;

/** Pruebas del Módulo 6 (HU-19 a HU-21). Se ejecuta con main: cada prueba imprime OK o FALLO. */
public class FacturaTest {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        ejecutar();
        System.out.println("\nFacturaTest: " + pasaron + " OK, " + fallaron + " con FALLO");
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    public static void ejecutar() {
        Usuario ana = new Usuario("Ana", "ana", "1234");
        Usuario luis = new Usuario("Luis", "luis", "1234");
        Vehiculo auto = new Vehiculo("ABC123", TipoVehiculo.AUTOMOVIL, "Mazda", ana);
        Vehiculo camion = new Vehiculo("DEF456", TipoVehiculo.CAMION, "Hino", luis);

        // ---- HU-19: la tarifa ----
        TarifaServicio tarifa = new TarifaServicio();

        TarifaServicio.Desglose d1 = tarifa.calcular(10, TipoVehiculo.AUTOMOVIL, false);
        comprobar("HU-19 automóvil, 10 km, diurno = $119.000", d1.getTotal() == 119_000);
        comprobar("HU-19 el desglose trae subtotal, recargo, IVA y total",
                d1.getSubtotal() == 100_000 && d1.getRecargo() == 0 && d1.getIva() == 19_000);

        TarifaServicio.Desglose d2 = tarifa.calcular(10, TipoVehiculo.CAMION, false);
        comprobar("HU-19 camión, 10 km, diurno = $154.700",
                d2.getTotal() == 154_700 && d2.getRecargo() == 30_000);

        TarifaServicio.Desglose d3 = tarifa.calcular(10, TipoVehiculo.AUTOMOVIL, true);
        comprobar("HU-19 automóvil, 10 km, nocturno = $142.800",
                d3.getTotal() == 142_800 && d3.getRecargo() == 20_000);

        comprobar("HU-19 kilómetros en cero o negativos se rechazan",
                lanza(DatoInvalidoException.class, () -> tarifa.calcular(0, TipoVehiculo.MOTO, false))
                        && lanza(DatoInvalidoException.class, () -> tarifa.calcular(-5, TipoVehiculo.MOTO, false)));

        // ---- HU-19: generar la factura ----
        FacturaServicio fs = nuevoServicio();
        Solicitud s1 = solicitud("SLC-001", ana, auto, 10, hora(10));
        s1.setEstado(EstadoSolicitudes.finalizada);
        Factura f1 = fs.generar(s1);
        comprobar("HU-19 al finalizar se crea la factura con subtotal, recargo, IVA y total",
                f1.getId().equals("FAC-001") && f1.getTotal() == 119_000 && f1.getCliente() == ana
                        && f1.getIdSolicitud().equals("SLC-001"));

        FacturaServicio fsNoche = nuevoServicio();
        Solicitud sNoche = solicitud("SLC-002", ana, auto, 10, hora(22));
        sNoche.setEstado(EstadoSolicitudes.finalizada);
        comprobar("HU-19 una solicitud de las 22:00 se factura como nocturna ($142.800)",
                fsNoche.generar(sNoche).getTotal() == 142_800);

        FacturaServicio fsMadrugada = nuevoServicio();
        Solicitud sMadrugada = solicitud("SLC-003", ana, auto, 10, hora(3));
        sMadrugada.setEstado(EstadoSolicitudes.finalizada);
        comprobar("HU-19 una solicitud de las 03:00 también es nocturna",
                fsMadrugada.generar(sMadrugada).getTotal() == 142_800);

        FacturaServicio fsCamion = nuevoServicio();
        Solicitud sCamion = solicitud("SLC-004", luis, camion, 10, hora(10));
        sCamion.setEstado(EstadoSolicitudes.finalizada);
        comprobar("HU-19 un camión finalizado se factura con su recargo ($154.700)",
                fsCamion.generar(sCamion).getTotal() == 154_700);

        FacturaServicio fs2 = nuevoServicio();
        Solicitud pendiente = solicitud("SLC-005", ana, auto, 10, hora(10));
        comprobar("HU-19 no se factura una solicitud que no está FINALIZADA (y no se guarda nada)",
                lanza(ReglaNegocioException.class, () -> fs2.generar(pendiente)) && fs2.listar().isEmpty());

        comprobar("HU-19 no se generan dos facturas para la misma solicitud",
                lanza(ReglaNegocioException.class, () -> fs.generar(s1)) && fs.listar().size() == 1);

        // ---- HU-20: facturas del cliente ----
        FacturaServicio fs3 = nuevoServicio();
        Solicitud a = solicitud("SLC-010", ana, auto, 10, hora(10));
        Solicitud l = solicitud("SLC-011", luis, camion, 10, hora(10));
        a.setEstado(EstadoSolicitudes.finalizada);
        l.setEstado(EstadoSolicitudes.finalizada);
        fs3.generar(a);
        fs3.generar(l);

        comprobar("HU-20 cada cliente ve solo sus facturas",
                fs3.listarPorCliente(ana).size() == 1 && fs3.listarPorCliente(ana).get(0).getCliente() == ana
                        && fs3.listarPorCliente(luis).size() == 1);

        comprobar("HU-20 un cliente sin facturas recibe lista vacía",
                fs3.listarPorCliente(new Usuario("Nuevo", "nuevo", "1234")).isEmpty());

        // ---- HU-21: facturas del administrador ----
        comprobar("HU-21 el listado trae todas las facturas", fs3.listar().size() == 2);

        comprobar("HU-21 buscar por identificador muestra el detalle",
                fs3.buscarPorId("FAC-002").map(f -> f.getTotal() == 154_700).orElse(false));

        comprobar("HU-21 identificador inexistente devuelve vacío sin error",
                fs3.buscarPorId("FAC-999").isEmpty());

        comprobar("HU-21 sin facturas registradas el listado está vacío",
                nuevoServicio().listar().isEmpty());
    }

    // ----- ayudantes -----
    private static FacturaServicio nuevoServicio() {
        return new FacturaServicio(new FacturaRepositorio(), new TarifaServicio());
    }

    private static LocalDateTime hora(int hora) {
        return LocalDateTime.of(2026, 1, 15, hora, 0);
    }

    /** Crea una solicitud con la fecha y hora que queramos (para probar de día y de noche). */
    private static Solicitud solicitud(String id, Usuario cliente, Vehiculo vehiculo, double km, LocalDateTime momento) {
        return new Solicitud(id, cliente, vehiculo, "Origen", "Destino", km) {
            @Override
            public LocalDateTime getFechaHora() {
                return momento;
            }
        };
    }

    private static boolean lanza(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (RuntimeException e) {
            return tipo.isInstance(e);
        }
    }

    private static void comprobar(String nombre, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  OK     " + nombre);
        } else {
            fallaron++;
            System.out.println("  FALLO  " + nombre);
        }
    }
}
