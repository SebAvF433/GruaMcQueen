package util;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import excepcion.EntradaCerradaException;

/**
 * Única puerta de entrada de datos del programa (un solo Scanner en toda la aplicación).
 * Lee siempre con nextLine() y repite la pregunta hasta recibir un dato válido.
 */
public class Consola {

    private final Scanner scanner;

    public Consola() {
        this(System.in);
    }

    public Consola(InputStream entrada) {
        this.scanner = new Scanner(entrada);
    }

    private String leerLinea() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new EntradaCerradaException();
        }
    }

    /** Pide un texto y lo repite mientras llegue vacío. */
    public String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = leerLinea().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Aviso: el dato no puede estar vacío. Intente de nuevo.");
        }
    }

    /** Pide un número entero entre min y max (ambos incluidos). */
    public int leerEntero(String mensaje, int min, int max) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.println("Aviso: la opción está fuera de rango. Escriba un número entre "
                        + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Aviso: \"" + texto + "\" no es un número entero válido.");
            }
        }
    }

    /** Pide un número decimal. Acepta punto o coma como separador (12.5 o 12,5). */
    public double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            if (texto.matches("-?\\d+([.,]\\d+)?")) {
                return Double.parseDouble(texto.replace(',', '.'));
            }
            System.out.println("Aviso: \"" + texto + "\" no es un número decimal válido.");
        }
    }
}
