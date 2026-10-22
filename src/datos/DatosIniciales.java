package datos;

import auth.Usuario;
import auth.UsuarioRepositorio;
import modelo.Rol;

/** Datos de prueba que se cargan al arrancar el programa. */
public class DatosIniciales {

    public static void cargar(UsuarioRepositorio usuarios) {
        usuarios.guardar(new Usuario("Administrador", "admin", "admin123", Rol.ADMIN));
        usuarios.guardar(new Usuario("Cliente de Prueba", "cliente", "cliente123", Rol.CLIENTE));
    }
}
