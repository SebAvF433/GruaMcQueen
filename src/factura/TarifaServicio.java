package factura;

import excepcion.DatoInvalidoException;
import vehiculo.TipoVehiculo;

/** La "calculadora" de la tarifa. No guarda nada: recibe datos y devuelve el desglose del precio. */
public class TarifaServicio {

    /** El resultado del cálculo: las 4 cifras de la factura. */
    public static class Desglose {
        private final long subtotal;
        private final long recargo;
        private final long iva;
        private final long total;

        public Desglose(long subtotal, long recargo, long iva, long total) {
            this.subtotal = subtotal;
            this.recargo = recargo;
            this.iva = iva;
            this.total = total;
        }

        public long getSubtotal() {
            return subtotal;
        }

        public long getRecargo() {
            return recargo;
        }

        public long getIva() {
            return iva;
        }

        public long getTotal() {
            return total;
        }
    }

    /**
     * Calcula la tarifa de un servicio.
     *   subtotal = base + (km x valor por km)
     *   recargo  = subtotal x (30 % si es camión + 20 % si es nocturno)
     *   iva      = 19 % de (subtotal + recargo)
     *   total    = subtotal + recargo + iva
     */
    public Desglose calcular(double km, TipoVehiculo tipoVehiculo, boolean nocturno) {
        if (km <= 0) {
            throw new DatoInvalidoException("Los kilómetros deben ser mayores a cero.");
        }
        if (tipoVehiculo == null) {
            throw new DatoInvalidoException("Falta el tipo de vehículo.");
        }

        long subtotal = Math.round(Tarifas.TARIFA_BASE + km * Tarifas.VALOR_POR_KM);

        double porcentajeRecargo = 0;
        if (tipoVehiculo == TipoVehiculo.CAMION) {
            porcentajeRecargo += Tarifas.RECARGO_CAMION;
        }
        if (nocturno) {
            porcentajeRecargo += Tarifas.RECARGO_NOCTURNO;
        }
        long recargo = Math.round(subtotal * porcentajeRecargo);

        long iva = Math.round((subtotal + recargo) * Tarifas.IVA);
        long total = subtotal + recargo + iva;

        return new Desglose(subtotal, recargo, iva, total);
    }
}
