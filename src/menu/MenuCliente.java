package menu;

import auth.AuthServicio;
import auth.Usuario;
import excepcion.DatoInvalidoException;
import excepcion.ReglaNegocioException;
import factura.Factura;
import factura.FacturaServicio;
import modelo.Rol;
import solicitud.Solicitud;
import solicitud.SolicitudServicio;
import util.Consola;
import vehiculo.*;

import java.util.List;

/** Menú del cliente conectado a vehículos, solicitudes y facturas. */
public class MenuCliente {
    private final Consola consola;
    private final AuthServicio auth;
    private final VehiculoServicio vehiculos;
    private final SolicitudServicio solicitudes;
    private final FacturaServicio facturas;

    public MenuCliente(Consola consola, AuthServicio auth, VehiculoServicio vehiculos,
                       SolicitudServicio solicitudes, FacturaServicio facturas) {
        this.consola=consola; this.auth=auth; this.vehiculos=vehiculos;
        this.solicitudes=solicitudes; this.facturas=facturas;
    }

    public void mostrar() {
        Usuario cliente=auth.getUsuarioActivo();
        if(cliente==null || cliente.getRol()!=Rol.CLIENTE){System.out.println("Acceso denegado: este menú es solo para clientes con sesión iniciada.");return;}
        int op;
        do {
            System.out.println("\n=== MENÚ CLIENTE | "+cliente.getNombre()+" ===\n1. Mis vehículos\n2. Mis solicitudes\n3. Mis facturas\n4. Cerrar sesión");
            op=consola.leerEntero("Elija una opción: ",1,4);
            try {
                switch(op){case 1->menuVehiculos(cliente);case 2->menuSolicitudes(cliente);case 3->verFacturas(cliente);case 4->{auth.cerrarSesion();System.out.println("Sesión cerrada. Hasta pronto, "+cliente.getNombre()+".");}}
            } catch(ReglaNegocioException | DatoInvalidoException e){System.out.println("Aviso: "+e.getMessage());}
        } while(op!=4);
    }

    private void menuVehiculos(Usuario cliente) {
        int op;
        do {
            System.out.println("\n--- MIS VEHÍCULOS ---\n1. Listar mis vehículos\n2. Registrar vehículo\n3. Volver");
            op=consola.leerEntero("Elija una opción: ",1,3);
            if(op==1) mostrarVehiculos(vehiculos.listarPorCliente(cliente));
            if(op==2){
                String placa=consola.leerTexto("Placa (ej. ABC123): ");
                TipoVehiculo tipo=elegirTipoVehiculo();
                String marca=consola.leerTexto("Marca: ");
                Vehiculo v=vehiculos.registrar(cliente,placa,tipo,marca);
                System.out.println("Vehículo registrado: "+v.getPlaca()+".");
            }
        }while(op!=3);
    }

    private void menuSolicitudes(Usuario cliente) {
        int op;
        do {
            System.out.println("\n--- MIS SOLICITUDES ---\n1. Consultar mis solicitudes\n2. Crear solicitud de grúa\n3. Cancelar solicitud\n4. Volver");
            op=consola.leerEntero("Elija una opción: ",1,4);
            if(op==1) mostrarSolicitudes(solicitudes.listarPorCliente(cliente));
            if(op==2){
                if(vehiculos.listarPorCliente(cliente).isEmpty()){System.out.println("Primero debe registrar un vehículo.");continue;}
                mostrarVehiculos(vehiculos.listarPorCliente(cliente));
                String placa=consola.leerTexto("Placa del vehículo: ");
                String origen=consola.leerTexto("Lugar de recogida/origen: ");
                String destino=consola.leerTexto("Destino: ");
                double km=consola.leerDecimal("Distancia estimada en kilómetros (máximo 500): ");
                Solicitud s=solicitudes.crearSolicitud(cliente,placa,origen,destino,km);
                System.out.println("Solicitud creada con ID "+s.getId()+". Estado: "+s.getEstado().getEtiqueta()+".");
            }
            if(op==3){
                String id=consola.leerTexto("ID de solicitud a cancelar: ");
                solicitudes.cancelarSolicitud(id,cliente);
                System.out.println("Solicitud cancelada correctamente.");
            }
        }while(op!=4);
    }

    private void verFacturas(Usuario cliente){
        List<Factura> lista=facturas.listarPorCliente(cliente);
        if(lista.isEmpty()){System.out.println("Todavía no tienes facturas.");return;}
        for(Factura f:lista) System.out.println(f.getId()+" | Solicitud: "+f.getIdSolicitud()+" | Fecha: "+f.getFecha()+" | Destino: "+f.getDestino()+" | Km: "+f.getKm()+" | Subtotal: "+dinero(f.getSubtotal())+" | Recargo: "+dinero(f.getRecargo())+" | IVA: "+dinero(f.getIva())+" | TOTAL: "+dinero(f.getTotal()));
    }
    private TipoVehiculo elegirTipoVehiculo(){
        TipoVehiculo[] tipos=TipoVehiculo.values();
        for(int i=0;i<tipos.length;i++)System.out.println((i+1)+". "+etiqueta(tipos[i]));
        return tipos[consola.leerEntero("Tipo de vehículo: ",1,tipos.length)-1];
    }
    private String etiqueta(TipoVehiculo t){return switch(t){case MOTO->"Moto";case AUTOMOVIL->"Automóvil";case CAMIONETA->"Camioneta";case CAMION->"Camión";};}
    private void mostrarVehiculos(List<Vehiculo> lista){
        if(lista.isEmpty()){System.out.println("No tienes vehículos registrados.");return;}
        for(Vehiculo v:lista)System.out.println(v.getPlaca()+" | "+etiqueta(v.getTipo())+" | Marca: "+v.getMarca());
    }
    private void mostrarSolicitudes(List<Solicitud> lista){
        if(lista.isEmpty()){System.out.println("No tienes solicitudes.");return;}
        for(Solicitud s:lista)System.out.println(s.getId()+" | "+s.getVehiculo().getPlaca()+" | "+s.getOrigen()+" -> "+s.getDestino()+" | "+s.getKm()+" km | Estado: "+s.getEstado().getEtiqueta()+" | Grúa: "+(s.getGrua()==null?"Pendiente":s.getGrua().getPlaca()));
    }
    private String dinero(long valor){return String.format("$%,d COP",valor).replace(',', '.');}
}
