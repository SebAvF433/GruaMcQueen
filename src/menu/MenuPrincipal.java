package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import factura.FacturaServicio;
import inventario.GruaServicio;
import reporte.ReporteServicio;
import solicitud.SolicitudServicio;
import util.Consola;
import vehiculo.VehiculoServicio;

/** Menú de entrada: registro, inicio de sesión y redirección según el rol. */
public class MenuPrincipal {
    private final Consola consola;
    private final AuthServicio auth;
    private final MenuCliente menuCliente;
    private final MenuAdmin menuAdmin;

    public MenuPrincipal(Consola consola, AuthServicio auth, VehiculoServicio vehiculos,
                         GruaServicio gruas, SolicitudServicio solicitudes,
                         FacturaServicio facturas, ReporteServicio reportes) {
        this.consola=consola; this.auth=auth;
        this.menuCliente=new MenuCliente(consola,auth,vehiculos,solicitudes,facturas);
        this.menuAdmin=new MenuAdmin(consola,auth,gruas,solicitudes,facturas,reportes);
    }

    public void iniciar(){
        int opcion;
        do {
            System.out.println("\n=== GRÚA McQUEEN ===\n1. Registrarse\n2. Iniciar sesión\n3. Salir");
            opcion=consola.leerEntero("Elija una opción: ",1,3);
            switch(opcion){case 1->registrarse();case 2->iniciarSesion();case 3->System.out.println("¡Hasta pronto!");}
        }while(opcion!=3);
    }

    private void registrarse(){
        System.out.println("--- REGISTRO DE CLIENTE ---");
        String nombre=consola.leerTexto("Nombre: ");
        String usuario=consola.leerTexto("Usuario: ");
        String clave=consola.leerTexto("Clave: ");
        try {Usuario nuevo=auth.registrarCliente(nombre,usuario,clave);System.out.println("Cuenta creada. Ya puede iniciar sesión con el usuario \""+nuevo.getUsuario()+"\".");}
        catch(ReglaNegocioException | DatoInvalidoException e){System.out.println("No se pudo registrar: "+e.getMessage());}
    }
    private void iniciarSesion(){
        System.out.println("\n--- INICIAR SESIÓN ---");
        while(true){
            String usuario=consola.leerTexto("Usuario: ");
            String clave=consola.leerTexto("Clave: ");
            try {
                Usuario activo=auth.iniciarSesion(usuario,clave);
                if(activo.getRol()==modelo.Rol.ADMIN)menuAdmin.mostrar();else menuCliente.mostrar();
                auth.cerrarSesion();
                return;
            }catch(ReglaNegocioException | DatoInvalidoException e){
                System.out.println("Aviso: "+e.getMessage()+"\n1. Intentar de nuevo\n2. Volver al menú principal");
                if(consola.leerEntero("Elija una opción: ",1,2)==2)return;
            }
        }
    }
}
