package comun;

/** Validaciones de formato reutilizables. */
public class Validador {

    private static final double KM_MAXIMO = 500;

    /** Placa válida: 3 letras + 2 dígitos + 1 dígito o letra. Ej: ABC123, ABC12D. */
    public static boolean placaValida(String placa) {
        return placa != null && placa.trim().toUpperCase().matches("[A-Z]{3}[0-9]{2}[0-9A-Z]");
    }

    /** Kilómetros válidos: mayores a 0 y de máximo 500. */
    public static boolean kmValido(double km) {
        return km > 0 && km <= KM_MAXIMO;
    }
}
