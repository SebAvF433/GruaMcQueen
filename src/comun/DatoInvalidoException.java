package comun;

/** TEMPORAL: esta clase es del responsable A (carpeta comun). Se lanza cuando un dato escrito no es válido. */
public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
