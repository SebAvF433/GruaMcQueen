package auth;

import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import modelo.Rol;

/** Reglas de negocio de la autenticación: registro, inicio y cierre de sesión. */
public class AuthServicio {

    private final UsuarioRepositorio repositorio;
    private Usuario usuarioActivo;

    public AuthServicio(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Crea una cuenta con rol CLIENTE.
     * Si algo no se cumple lanza una excepción y NO guarda nada.
     */
    public Usuario registrarCliente(String nombre, String usuario, String clave) {
        exigirTexto(nombre, "El nombre no puede estar vacío.");
        exigirTexto(usuario, "El usuario no puede estar vacío.");
        exigirTexto(clave, "La clave no puede estar vacía.");

        if (repositorio.buscarPorUsuario(usuario).isPresent()) {
            throw new ReglaNegocioException(
                "El usuario \"" + usuario.trim() + "\" ya está registrado. Elija otro nombre de usuario.");
        }

        Usuario nuevo = new Usuario(nombre.trim(), usuario, clave, Rol.CLIENTE);
        repositorio.guardar(nuevo);
        return nuevo;
    }

    /**
     * Valida las credenciales y deja al usuario como sesión activa.
     * Ante usuario inexistente o clave incorrecta el mensaje es el mismo, para no revelar cuál dato falló.
     */
    public Usuario iniciarSesion(String usuario, String clave) {
        exigirTexto(usuario, "El usuario no puede estar vacío.");
        exigirTexto(clave, "La clave no puede estar vacía.");

        Usuario encontrado = repositorio.buscarPorUsuario(usuario)
                .filter(u -> u.claveCoincide(clave))
                .orElseThrow(() -> new ReglaNegocioException("Credenciales incorrectas."));

        usuarioActivo = encontrado;
        return encontrado;
    }

    public void cerrarSesion() {
        usuarioActivo = null;
    }

    public Usuario getUsuarioActivo() {
        return usuarioActivo;
    }

    public boolean haySesionActiva() {
        return usuarioActivo != null;
    }

    private void exigirTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new DatoInvalidoException(mensaje);
        }
    }
}
