import auth.AuthServicio;
import auth.UsuarioRepositorio;
import datos.DatosIniciales;
import excepcion.EntradaCerradaException;
import menu.MenuPrincipal;
import util.Consola;

public class Main {
    public static void main(String[] args) {
        Consola consola = new Consola();
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        DatosIniciales.cargar(usuarios);
        AuthServicio auth = new AuthServicio(usuarios);

        try {
            new MenuPrincipal(consola, auth).iniciar();
        } catch (EntradaCerradaException e) {
            System.out.println("\nSe cerró la entrada de datos. Fin del programa.");
        }
    }
}
