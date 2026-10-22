package test;

import auth.Usuario;
import excepcion.ReglaNegocioException;
import factura.Factura;
import factura.FacturaRepositorio;
import factura.FacturaServicio;
import factura.TarifaServicio;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import modelo.Rol;
import reporte.ReporteServicio;
import solicitud.EstadoSolicitud;
import solicitud.Solicitud;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import vehiculo.TipoVehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;

import java.util.List;

/** Pruebas de la HU-19 (tarifa y factura) y de la HU-22 (ingresos). Se puede ejecutar sola con main(). */
public class FacturaTest {

    static int ok = 0, fallos = 0;

    static void verificar(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("[OK]    " + nombre); }
        else { fallos++; System.out.println("[FALLO] " + nombre); }
    }

    public static void main(String[] args) {
        System.out.println("\n== Facturas (HU-19 y HU-22) ==");

        // --- Tarifa: valores de los criterios de aceptación de la HU-19 ---
        TarifaServicio tarifa = new TarifaServicio();
        verificar("Automóvil, 10 km, diurno: total $119.000",
                tarifa.calcular(10, TipoVehiculo.AUTOMOVIL, false).getTotal() == 119_000);
        verificar("Camión, 10 km, diurno: total $154.700",
                tarifa.calcular(10, TipoVehiculo.CAMION, false).getTotal() == 154_700);
        verificar("Automóvil, 10 km, nocturno: total $142.800",
                tarifa.calcular(10, TipoVehiculo.AUTOMOVIL, true).getTotal() == 142_800);

        // --- Factura de una solicitud ---
        Usuario ana = new Usuario("Ana", "ana", "1234", Rol.CLIENTE);
        VehiculoServicio vehiculos = new VehiculoServicio(new VehiculoRepositorio());
        SolicitudServicio solicitudes = new SolicitudServicio(
                new SolicitudRepositorio(), vehiculos, new GruaServicio(new GruaRepositorio()));
        FacturaServicio facturas = new FacturaServicio(new FacturaRepositorio(), tarifa);

        vehiculos.registrar(ana, "AAA111", TipoVehiculo.AUTOMOVIL, "Renault");
        Solicitud s = solicitudes.crearSolicitud(ana, "AAA111", "Calle 1", "Calle 2", 10);

        verificar("Solicitud no finalizada: no se factura",
                lanza(() -> facturas.generar(s)) && facturas.listar().isEmpty());

        s.setEstado(EstadoSolicitud.FINALIZADA);
        Factura f = facturas.generar(s);
        verificar("Finalizada: se crea la factura con los datos de la solicitud",
                f.getIdSolicitud().equals(s.getId()) && f.getDestino().equals("Calle 2")
                && f.getKm() == 10 && f.getCliente() == ana);
        verificar("La factura cuadra: total = subtotal + recargo + IVA",
                f.getTotal() == f.getSubtotal() + f.getRecargo() + f.getIva());
        verificar("Misma solicitud dos veces: se rechaza la segunda factura",
                lanza(() -> facturas.generar(s)) && facturas.listar().size() == 1);
        verificar("Cliente ve sus facturas; otro cliente no ve ninguna",
                facturas.listarPorCliente(ana).size() == 1
                && facturas.listarPorCliente(new Usuario("Luis", "luis", "1234", Rol.CLIENTE)).isEmpty());
        verificar("Buscar por identificador (FAC-001) y uno inexistente",
                facturas.buscarPorId("fac-001").isPresent() && facturas.buscarPorId("FAC-999").isEmpty());

        // --- Reporte de ingresos (HU-22): $119.000 + $154.700 = $273.700 ---
        List<Factura> dos = List.of(
                new Factura("FAC-001", "SOL-001", ana, null, "X", 10, 100_000, 0, 19_000, 119_000),
                new Factura("FAC-002", "SOL-002", ana, null, "Y", 10, 100_000, 30_000, 24_700, 154_700));
        ReporteServicio reporte = new ReporteServicio();
        verificar("Ingresos totales de las facturas conocidas: $273.700",
                reporte.calcularIngresosTotales(dos) == 273_700);
        verificar("Facturación por cliente: ana suma $273.700",
                reporte.obtenerFacturacionPorCliente(dos).get("ana") == 273_700);

        System.out.println("\nPasaron: " + ok + " | Fallaron: " + fallos);
    }

    private static boolean lanza(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (ReglaNegocioException e) {
            return true;
        }
    }
}
