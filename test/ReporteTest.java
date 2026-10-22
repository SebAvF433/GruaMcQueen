package test;

import java.util.ArrayList;
import java.util.Map;
import reporte.ReporteServicio;

public class ReporteTest {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE REPORTES ===");
        ReporteServicio reporteServicio = new ReporteServicio();

        // Prueba 1: Calcular ingresos con lista vacía (HU-22)
        double ingresosVacios = reporteServicio.calcularIngresosTotales(new ArrayList<>());
        System.out.println("Prueba 1 (Ingresos con lista vacía): " + (ingresosVacios == 0.0 ? "EXITOSO" : "FALLÓ"));

        // Prueba 2: Verificar que el reporte de facturación por cliente retorne un mapa inicializado (HU-22)
        Map<String, Double> facturacionCliente = reporteServicio.obtenerFacturacionPorCliente(null);
        System.out.println("Prueba 2 (Mapa de facturación seguro ante nulos): " + (facturacionCliente != null ? "EXITOSO" : "FALLÓ"));

        // Prueba 3: Verificar reporte de servicios por estado (HU-23)
        Map<String, Integer> serviciosEstado = reporteServicio.obtenerServiciosPorEstado(new ArrayList<>());
        System.out.println("Prueba 3 (Reporte de servicios por estado vacío): " + (serviciosEstado != null ? "EXITOSO" : "FALLÓ"));

        System.out.println("=== FIN DE PRUEBAS DE REPORTES ===");
    }
}