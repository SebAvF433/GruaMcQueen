import auth.AuthServicio;
import auth.UsuarioRepositorio;
import datos.DatosIniciales;
import excepcion.EntradaCerradaException;
import factura.FacturaRepositorio;
import factura.FacturaServicio;
import factura.TarifaServicio;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import menu.MenuPrincipal;
import reporte.ReporteServicio;
import solicitud.SolicitudRepositorio;
import solicitud.SolicitudServicio;
import util.Consola;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;

public class Main {
    public static void main(String[] args) {
        Consola consola = new Consola();
        UsuarioRepositorio usuarios = new UsuarioRepositorio();
        DatosIniciales.cargar(usuarios);

        VehiculoRepositorio vehiculoRepositorio = new VehiculoRepositorio();
        VehiculoServicio vehiculoServicio = new VehiculoServicio(vehiculoRepositorio);
        GruaRepositorio gruaRepositorio = new GruaRepositorio();
        GruaServicio gruaServicio = new GruaServicio(gruaRepositorio);
        SolicitudRepositorio solicitudRepositorio = new SolicitudRepositorio();
        SolicitudServicio solicitudServicio = new SolicitudServicio(solicitudRepositorio, vehiculoServicio, gruaServicio);
        FacturaRepositorio facturaRepositorio = new FacturaRepositorio();
        FacturaServicio facturaServicio = new FacturaServicio(facturaRepositorio, new TarifaServicio());
        ReporteServicio reporteServicio = new ReporteServicio();
        AuthServicio auth = new AuthServicio(usuarios);

        try {
            new MenuPrincipal(consola, auth, vehiculoServicio, gruaServicio,
                    solicitudServicio, facturaServicio, reporteServicio).iniciar();
        } catch (EntradaCerradaException e) {
            System.out.println("Se cerró la entrada de datos. Fin del programa.");
        }
    }
}
