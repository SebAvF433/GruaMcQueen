package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import factura.Factura;
import factura.FacturaServicio;
import inventario.*;
import modelo.Rol;
import reporte.ReporteServicio;
import solicitud.*;
import util.Consola;

import java.util.List;
import java.util.Map;

/** Menú del administrador. Mantiene la navegación original y conecta cada opción con sus servicios. */
public class MenuAdmin {
    private final Consola consola;
    private final AuthServicio auth;
    private final GruaServicio gruas;
    private final SolicitudServicio solicitudes;
    private final FacturaServicio facturas;
    private final ReporteServicio reportes;

    public MenuAdmin(Consola consola, AuthServicio auth, GruaServicio gruas,
                     SolicitudServicio solicitudes, FacturaServicio facturas, ReporteServicio reportes) {
        this.consola = consola; this.auth = auth; this.gruas = gruas;
        this.solicitudes = solicitudes; this.facturas = facturas; this.reportes = reportes;
    }

    public void mostrar() {
        Usuario admin = auth.getUsuarioActivo();
        if (admin == null || admin.getRol() != Rol.ADMIN) {
            System.out.println("Acceso denegado: este menú es solo para administradores."); return;
        }
        int opcion;
        do {
            System.out.println("\n=== MENÚ ADMINISTRADOR | " + admin.getNombre() + " ===");
            System.out.println("1. Grúas\n2. Solicitudes\n3. Facturas\n4. Reportes\n5. Cerrar sesión");
            opcion = consola.leerEntero("Elija una opción: ", 1, 5);
            try {
                switch (opcion) {
                    case 1 -> menuGruas();
                    case 2 -> menuSolicitudes();
                    case 3 -> menuFacturas();
                    case 4 -> menuReportes();
                    case 5 -> { auth.cerrarSesion(); System.out.println("Sesión cerrada. Hasta pronto, " + admin.getNombre() + "."); }
                }
            } catch (ReglaNegocioException | DatoInvalidoException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        } while (opcion != 5);
    }

    private void menuGruas() {
        int op;
        do {
            System.out.println("\n--- GRÚAS ---\n1. Registrar grúa\n2. Consultar inventario\n3. Cambiar estado de grúa\n4. Volver");
            op = consola.leerEntero("Elija una opción: ", 1, 4);
            switch (op) {
                case 1 -> {
                    String placa = consola.leerTexto("Placa de la grúa: ");
                    TipoGrua tipo = elegirTipoGrua();
                    if (gruas.registrar(placa, tipo)) System.out.println("Grúa registrada y disponible.");
                    else System.out.println("Ya existe una grúa con esa placa.");
                }
                case 2 -> mostrarGruas(gruas.listar());
                case 3 -> {
                    String placa = consola.leerTexto("Placa de la grúa: ");
                    EstadoGrua estado = elegirEstadoGrua();
                    gruas.cambiarEstado(placa, estado);
                    System.out.println("Estado actualizado correctamente.");
                }
            }
        } while (op != 4);
    }

    private void menuSolicitudes() {
        int op;
        do {
            System.out.println("\n--- SOLICITUDES ---\n1. Consultar todas\n2. Filtrar por estado\n3. Asignar grúa\n4. Iniciar servicio\n5. Finalizar servicio\n6. Volver");
            op = consola.leerEntero("Elija una opción: ", 1, 6);
            switch (op) {
                case 1 -> mostrarSolicitudes(solicitudes.listar());
                case 2 -> mostrarSolicitudes(solicitudes.filtrarPorEstado(elegirEstadoSolicitud()));
                case 3 -> { solicitudes.asignarGrua(consola.leerTexto("ID de solicitud (ej. SOL-001): ")); System.out.println("Grúa asignada."); }
                case 4 -> { solicitudes.iniciarServicio(consola.leerTexto("ID de solicitud: ")); System.out.println("Servicio iniciado."); }
                case 5 -> {
                    String id = consola.leerTexto("ID de solicitud: ");
                    solicitudes.finalizarServicio(id);
                    Solicitud s = solicitudes.listar().stream().filter(x -> x.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
                    if (s != null) {
                        try { Factura f = facturas.generar(s); System.out.println("Servicio finalizado. Factura generada: " + f.getId() + " | Total: " + dinero(f.getTotal())); }
                        catch (ReglaNegocioException e) { System.out.println("Servicio finalizado. No se generó factura: " + e.getMessage()); }
                    }
                }
            }
        } while (op != 6);
    }

    private void menuFacturas() {
        int op;
        do {
            System.out.println("\n--- FACTURAS ---\n1. Listar facturas\n2. Buscar factura por identificador\n3. Volver");
            op = consola.leerEntero("Elija una opción: ", 1, 3);
            if (op == 1) mostrarFacturas(facturas.listar());
            if (op == 2) {
                String id = consola.leerTexto("ID de factura (ej. FAC-001): ");
                Factura f = facturas.buscarPorId(id).orElse(null);
                if (f == null) System.out.println("No existe una factura con ese identificador."); else mostrarFactura(f);
            }
        } while (op != 3);
    }

    private void menuReportes() {
        int op;
        do {
            System.out.println("\n--- REPORTES ---\n1. Reporte de ingresos\n2. Reporte de servicios\n3. Volver");
            op = consola.leerEntero("Elija una opción: ", 1, 3);
            if (op == 1) {
                List<Factura> lista = facturas.listar();
                System.out.println("Ingresos totales: " + dinero(Math.round(reportes.calcularIngresosTotales(lista))));
                for (Map.Entry<String, Double> e : reportes.obtenerFacturacionPorCliente(lista).entrySet())
                    System.out.println("Cliente " + e.getKey() + ": " + dinero(Math.round(e.getValue())));
            } else if (op == 2) {
                System.out.println("Servicios por estado:");
                reportes.obtenerServiciosPorEstado(solicitudes.listar()).forEach((k,v) -> System.out.println("- " + k + ": " + v));
                System.out.println("Servicios por grúa:");
                reportes.obtenerServiciosPorGrua(solicitudes.listar()).forEach((k,v) -> System.out.println("- Grúa " + k + ": " + v));
            }
        } while (op != 3);
    }

    private TipoGrua elegirTipoGrua() {
        System.out.println("Tipo de grúa: 1. Plataforma  2. Arrastre  3. Pesada");
        return TipoGrua.values()[consola.leerEntero("Seleccione: ", 1, 3)-1];
    }
    private EstadoGrua elegirEstadoGrua() {
        System.out.println("Estado: 1. Disponible  2. En mantenimiento  3. En servicio");
        return EstadoGrua.values()[consola.leerEntero("Seleccione: ", 1, 3)-1];
    }
    private EstadoSolicitud elegirEstadoSolicitud() {
        EstadoSolicitud[] estados = EstadoSolicitud.values();
        for (int i=0;i<estados.length;i++) System.out.println((i+1)+". "+estados[i].getEtiqueta());
        return estados[consola.leerEntero("Seleccione estado: ",1,estados.length)-1];
    }
    private void mostrarGruas(List<Grua> lista) {
        if (lista.isEmpty()) { System.out.println("No hay grúas registradas."); return; }
        for (Grua g:lista) System.out.println(g.getPlaca()+" | "+g.getTipo()+" | "+g.getEstado());
    }
    private void mostrarSolicitudes(List<Solicitud> lista) {
        if (lista.isEmpty()) { System.out.println("No hay solicitudes para mostrar."); return; }
        for (Solicitud s:lista) System.out.println(s.getId()+" | Cliente: "+s.getCliente().getUsuario()+" | Vehículo: "+s.getVehiculo().getPlaca()+" | "+s.getOrigen()+" -> "+s.getDestino()+" | "+s.getKm()+" km | Estado: "+s.getEstado().getEtiqueta()+" | Grúa: "+(s.getGrua()==null?"Sin asignar":s.getGrua().getPlaca()));
    }
    private void mostrarFacturas(List<Factura> lista) {
        if (lista.isEmpty()) { System.out.println("No hay facturas registradas."); return; }
        for (Factura f:lista) mostrarFactura(f);
    }
    private void mostrarFactura(Factura f) {
        System.out.println("\nFactura "+f.getId()+" | Solicitud: "+f.getIdSolicitud()+" | Cliente: "+f.getCliente().getUsuario()+" | Fecha: "+f.getFecha()+" | Destino: "+f.getDestino()+" | Km: "+f.getKm()+" | Subtotal: "+dinero(f.getSubtotal())+" | Recargo: "+dinero(f.getRecargo())+" | IVA: "+dinero(f.getIva())+" | TOTAL: "+dinero(f.getTotal()));
    }
    private String dinero(long valor) { return String.format("$%,d COP", valor).replace(',', '.'); }
}
