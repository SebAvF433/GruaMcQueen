package auth;

public class Usuario {
    String nombre;
    String correo;
    String contraseña;

    public Usuario(String nombre, String correo, String contraseña){
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
    }

    void mostrarPerfil() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Correo: " + correo);
    }
}
