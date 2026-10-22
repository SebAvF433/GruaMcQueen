package reporte;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import factura.Factura;
import solicitud.Solicitud;

public class ReporteServicio {

    // HU-22: Calcular los ingresos totales sumando todas las facturas registradas
    public double calcularIngresosTotales(List<Factura> facturas) {
        double totalIngresos = 0.0;
        if (facturas == null || facturas.isEmpty()) {
            return 0.0;
        }
        for (Factura factura : facturas) {
            totalIngresos += factura.getTotal();
        }
        return totalIngresos;
    }

    // HU-22: Agrupar la facturación total por cada cliente
    public Map<String, Double> obtenerFacturacionPorCliente(List<Factura> facturas) {
        Map<String, Double> facturacionCliente = new HashMap<>();
        if (facturas == null) {
            return facturacionCliente;
        }
        for (Factura factura : facturas) {
            String cliente = factura.getCliente().getUsuario();
            double subTotalActual = facturacionCliente.getOrDefault(cliente, 0.0);
            facturacionCliente.put(cliente, subTotalActual + factura.getTotal());
        }
        return facturacionCliente;
    }

    // HU-23: Reporte de cantidad de servicios agrupados por su estado actual
    public Map<String, Integer> obtenerServiciosPorEstado(List<Solicitud> solicitudes) {
        Map<String, Integer> serviciosEstado = new HashMap<>();
        if (solicitudes == null) {
            return serviciosEstado;
        }
        for (Solicitud solicitud : solicitudes) {
            String estado = solicitud.getEstado().name();
            int cantidadActual = serviciosEstado.getOrDefault(estado, 0);
            serviciosEstado.put(estado, cantidadActual + 1);
        }
        return serviciosEstado;
    }

    // HU-23: Reporte de cantidad de servicios atendidos por cada grúa
    public Map<String, Integer> obtenerServiciosPorGrua(List<Solicitud> solicitudes) {
        Map<String, Integer> serviciosGrua = new HashMap<>();
        if (solicitudes == null) {
            return serviciosGrua;
        }
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getGrua() != null) {
                String placaGrua = solicitud.getGrua().getPlaca();
                int cantidadActual = serviciosGrua.getOrDefault(placaGrua, 0);
                serviciosGrua.put(placaGrua, cantidadActual + 1);
            }
        }
        return serviciosGrua;
    }
}