package auth;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** "Base de datos en memoria" de usuarios. Solo guarda y entrega datos; no aplica reglas. */
public class UsuarioRepositorio {
    private final Map<String, Usuario> usuarios = new LinkedHashMap<>();

    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getUsuario(), usuario);
    }

    public Optional<Usuario> buscarPorUsuario(String usuario) {
        if (usuario == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarios.get(usuario.trim().toLowerCase(Locale.ROOT)));
    }

    /** Devuelve una copia: quien la reciba no puede modificar la colección original. */
    public List<Usuario> listar() {
        return List.copyOf(usuarios.values());
    }
}
