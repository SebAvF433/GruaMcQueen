package excepcion;

/** Se lanza cuando una operación incumple una regla del negocio (usuario duplicado, credenciales incorrectas, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
