package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import modelo.Rol;
import util.Consola;

/** Menú numerado del administrador, con un submenú por módulo. */
public class MenuAdmin {

    private final Consola consola;
    private final AuthServicio auth;

    public MenuAdmin(Consola consola, AuthServicio auth) {
        this.consola = consola;
        this.auth = auth;
    }

    public void mostrar() {
        Usuario admin = auth.getUsuarioActivo();
        if (admin == null || admin.getRol() != Rol.ADMIN) {
            System.out.println("Acceso denegado: este menú es solo para administradores.");
            return;
        }

        int opcion;
        do {
            System.out.println("\n=== MENÚ ADMINISTRADOR | " + admin.getNombre() + " ===");
            System.out.println("1. Grúas");
            System.out.println("2. Solicitudes");
            System.out.println("3. Facturas");
            System.out.println("4. Reportes");
            System.out.println("5. Cerrar sesión");
            opcion = consola.leerEntero("Elija una opción: ", 1, 5);

            try {
                switch (opcion) {
                    case 1:
                        submenu("Grúas",
                                "Registrar grúa", "Consultar inventario", "Cambiar estado de grúa");
                        break;
                    case 2:
                        submenu("Solicitudes",
                                "Consultar y filtrar solicitudes", "Asignar grúa",
                                "Iniciar servicio", "Finalizar servicio");
                        break;
                    case 3:
                        submenu("Facturas",
                                "Listar facturas", "Buscar factura por identificador");
                        break;
                    case 4:
                        submenu("Reportes",
                                "Reportes de ingresos", "Reportes de servicios");
                        break;
                    case 5:
                        auth.cerrarSesion();
                        System.out.println("Sesión cerrada. Hasta pronto, " + admin.getNombre() + ".");
                        break;
                    default:
                        break;
                }
            } catch (ReglaNegocioException | DatoInvalidoException e) {
                System.out.println("Aviso: " + e.getMessage());
            }
        } while (opcion != 5);
    }

    /**
     * Submenú de un módulo. Al terminar (opción "Volver") se regresa al menú de administrador.
     * Cada función se conectará con su servicio cuando se implemente su módulo.
     */
    private void submenu(String titulo, String... opciones) {
        int volver = opciones.length + 1;
        int opcion;
        do {
            System.out.println("\n--- " + titulo.toUpperCase() + " ---");
            for (int i = 0; i < opciones.length; i++) {
                System.out.println((i + 1) + ". " + opciones[i]);
            }
            System.out.println(volver + ". Volver");
            opcion = consola.leerEntero("Elija una opción: ", 1, volver);

            if (opcion != volver) {
                System.out.println("\"" + opciones[opcion - 1]
                        + "\" estará disponible cuando se implemente su módulo.");
            }
        } while (opcion != volver);
    }
}
