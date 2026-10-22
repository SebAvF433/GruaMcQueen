package solicitud;

import java.time.LocalDateTime;

import auth.Usuario;
import inventario.Grua;
import vehiculo.Vehiculo;

public class Solicitud {

    private final String id;
    private final Usuario cliente;
    private final Vehiculo vehiculo;
    private final String origen;
    private final String destino;
    private final double km;
    private final LocalDateTime fechaHora;
    private EstadoSolicitud estado;
    private Grua grua;

    public Solicitud(String id, Usuario cliente, Vehiculo vehiculo, String origen, String destino, double km) {
        this.id = id;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.origen = origen;
        this.destino = destino;
        this.km = km;
        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoSolicitud.SOLICITADA;
    }

    public String getId() {
        return id;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getKm() {
        return km;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    /** La grúa asignada (null mientras la solicitud no tenga una). */
    public Grua getGrua() {
        return grua;
    }

    public void setGrua(Grua grua) {
        this.grua = grua;
    }
}
