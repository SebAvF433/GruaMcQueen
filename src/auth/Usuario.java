package auth;

import java.util.Locale;

import modelo.Rol;

public class Usuario {
    private final String nombre;
    private final String usuario;
    private final String clave;
    private final Rol rol;

    public Usuario(String nombre, String usuario, String clave, Rol rol) {
        this.nombre = nombre;
        // El usuario no distingue mayúsculas: "Ana" y "ana" son la misma cuenta.
        this.usuario = usuario.trim().toLowerCase(Locale.ROOT);
        this.clave = clave;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public Rol getRol() {
        return rol;
    }

    /** La clave nunca se entrega hacia afuera: solo se puede comprobar si coincide. */
    public boolean claveCoincide(String intento) {
        return clave.equals(intento);
    }

    void mostrarPerfil() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Usuario: " + usuario);
    }
}
