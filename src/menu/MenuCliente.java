package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import modelo.Rol;
import util.Consola;

/** Menú numerado del cliente. */
public class MenuCliente {

    private final Consola consola;
    private final AuthServicio auth;

    public MenuCliente(Consola consola, AuthServicio auth) {
        this.consola = consola;
        this.auth = auth;
    }

    public void mostrar() {
        Usuario cliente = auth.getUsuarioActivo();
        if (cliente == null || cliente.getRol() != Rol.CLIENTE) {
            System.out.println("Acceso denegado: este menú es solo para clientes con sesión iniciada.");
            return;
        }

        int opcion;
        do {
            System.out.println("\n=== MENÚ CLIENTE | " + cliente.getNombre() + " ===");
            System.out.println("1. Mis vehículos");
            System.out.println("2. Mis solicitudes");
            System.out.println("3. Mis facturas");
            System.out.println("4. Cerrar sesión");
            opcion = consola.leerEntero("Elija una opción: ", 1, 4);

            try {
                switch (opcion) {
                    case 1:
                        pendiente("Mis vehículos");
                        break;
                    case 2:
                        pendiente("Mis solicitudes");
                        break;
                    case 3:
                        pendiente("Mis facturas");
                        break;
                    case 4:
                        auth.cerrarSesion();
                        System.out.println("Sesión cerrada. Hasta pronto, " + cliente.getNombre() + ".");
                        break;
                    default:
                        break;
                }
            } catch (ReglaNegocioException | DatoInvalidoException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        } while (opcion != 4);
    }

    /** Punto de conexión para los módulos de vehículos, solicitudes y facturas (aún sin implementar). */
    private void pendiente(String funcion) {
        System.out.println("\"" + funcion + "\" estará disponible cuando se implemente su módulo.");
    }
}
