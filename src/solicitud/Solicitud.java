package solicitud;

import java.time.LocalDateTime;

import auth.Usuario;
import vehiculo.Vehiculo;

public class Solicitud {

    private final String id;
    private final Usuario cliente;
    private final Vehiculo vehiculo;
    private final String origen;
    private final LocalDateTime fechaHora;
    private EstadoSolicitudes estado;

    public Solicitud(String id, Usuario cliente, Vehiculo vehiculo, String origen, String destino){
        this.id = id;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.origen = origen;
        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoSolicitudes.solicitada;
    }

    public String getId(){
        return id;
    }

    public Usuario getCliente(){
        return cliente;
    }

    public Vehiculo getVehiculo(){
        return vehiculo;
    }

    public String getOrigen(){
        return origen;
    }

    public LocalDateTime getFechaHora(){
        return fechaHora;
    }

    public EstadoSolicitudes getEstado(){
        return estado;
    }

    public void setEstado(EstadoSolicitudes estado){
        this.estado = estado;
    }
}