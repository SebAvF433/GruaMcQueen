package excepcion;

/** Se lanza cuando un dato recibido no es válido (vacío, con formato incorrecto, etc.). */
public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
