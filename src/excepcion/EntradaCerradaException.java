package excepcion;

/** Se lanza cuando ya no hay más entrada disponible (por ejemplo, Ctrl+D / Ctrl+Z). Permite terminar el programa sin errores. */
public class EntradaCerradaException extends RuntimeException {
    public EntradaCerradaException() {
        super("La entrada de datos se cerró.");
    }
}
