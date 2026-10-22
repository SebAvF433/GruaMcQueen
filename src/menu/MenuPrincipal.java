package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import util.Consola;

/** Menú de entrada: registrarse, iniciar sesión o salir. Desde aquí se redirige según el rol. */
public class MenuPrincipal {

    private final Consola consola;
    private final AuthServicio auth;
    private final MenuCliente menuCliente;
    private final MenuAdmin menuAdmin;

    public MenuPrincipal(Consola consola, AuthServicio auth) {
        this.consola = consola;
        this.auth = auth;
        this.menuCliente = new MenuCliente(consola, auth);
        this.menuAdmin = new MenuAdmin(consola, auth);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== GRÚA McQUEEN ===");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            opcion = consola.leerEntero("Elija una opción: ", 1, 3);

            switch (opcion) {
                case 1:
                    registrarse();
                    break;
                case 2:
                    iniciarSesion();
                    break;
                case 3:
                    System.out.println("¡Hasta pronto!");
                    break;
                default:
                    break;
            }
        } while (opcion != 3);
    }

    private void registrarse() {
        System.out.println("\n--- REGISTRO DE CLIENTE ---");
        String nombre = consola.leerTexto("Nombre: ");
        String usuario = consola.leerTexto("Usuario: ");
        String clave = consola.leerTexto("Clave: ");

        try {
            Usuario nuevo = auth.registrarCliente(nombre, usuario, clave);
            System.out.println("Cuenta creada. Ya puede iniciar sesión con el usuario \""
                    + nuevo.getUsuario() + "\".");
        } catch (ReglaNegocioException | DatoInvalidoException e) {
            System.out.println("No se pudo registrar: " + e.getMessage());
        }
    }

    private void iniciarSesion() {
        System.out.println("\n--- INICIAR SESIÓN ---");
        Usuario activo = null;

        while (activo == null) {
            String usuario = consola.leerTexto("Usuario: ");
            String clave = consola.leerTexto("Clave: ");
            try {
                activo = auth.iniciarSesion(usuario, clave);
            } catch (ReglaNegocioException | DatoInvalidoException e) {
                System.out.println("Aviso: " + e.getMessage());
                System.out.println("1. Intentar de nuevo");
                System.out.println("2. Volver al menú principal");
                if (consola.leerEntero("Elija una opción: ", 1, 2) == 2) {
                    return;
                }
            }
        }

        switch (activo.getRol()) {
            case ADMIN:
                menuAdmin.mostrar();
                break;
            case CLIENTE:
                menuCliente.mostrar();
                break;
            default:
                break;
        }
        // Al volver del menú la sesión debe quedar cerrada, pase lo que pase.
        auth.cerrarSesion();
    }
}
