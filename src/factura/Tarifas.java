package factura;

/**
 * Los "precios de la tienda". Aquí viven TODOS los números de la tarifa.
 * Si mañana cambia un precio, se cambia solo aquí y todo el sistema se actualiza.
 * Valores en pesos colombianos.
 */
public class Tarifas {

    /** Lo que se cobra por salir a atender el servicio, sin contar los kilómetros. */
    public static final long TARIFA_BASE = 50_000;

    /** Lo que se cobra por cada kilómetro recorrido. */
    public static final long VALOR_POR_KM = 5_000;

    /** Recargo por mover un camión (30 % del subtotal). */
    public static final double RECARGO_CAMION = 0.30;

    /** Recargo por servicio nocturno (20 % del subtotal). */
    public static final double RECARGO_NOCTURNO = 0.20;

    /** Impuesto (19 %), se aplica sobre subtotal + recargo. */
    public static final double IVA = 0.19;

    /** Es de noche desde las 19:00 hasta las 05:59. */
    public static final int HORA_INICIO_NOCTURNO = 19;
    public static final int HORA_FIN_NOCTURNO = 6;

    /** Constructor privado: esta clase solo guarda constantes, no se crean objetos de ella. */
    private Tarifas() {
    }
}
