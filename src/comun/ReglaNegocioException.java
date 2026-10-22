package comun;

/** TEMPORAL: esta clase es del responsable A (carpeta comun). Se lanza cuando se rompe una regla del negocio. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
