package comun;

/** TEMPORAL: esta clase es del responsable A (carpeta comun). Aquí solo está lo que necesita el Módulo 3. */
public class Validador {

    /** Placa válida: 3 letras + 2 dígitos + 1 dígito o letra. Ej: ABC123, ABC12D. */
    public static boolean placaValida(String placa) {
        return placa != null && placa.trim().toUpperCase().matches("[A-Z]{3}[0-9]{3}");
    }
}
