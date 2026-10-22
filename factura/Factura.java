package factura;

import auth.Usuario;

import java.time.LocalDateTime;

/** La factura de un servicio. Una vez creada no se modifica (todo es final). */
public class Factura {

    private final String id;
    private final String idSolicitud;
    private final Usuario cliente;
    private final LocalDateTime fecha;
    private final long subtotal;
    private final long recargo;
    private final long iva;
    private final long total;

    public Factura(String id, String idSolicitud, Usuario cliente, LocalDateTime fecha,
                   long subtotal, long recargo, long iva, long total) {
        this.id = id;
        this.idSolicitud = idSolicitud;
        this.cliente = cliente;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.recargo = recargo;
        this.iva = iva;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public String getIdSolicitud() {
        return idSolicitud;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public long getSubtotal() {
        return subtotal;
    }

    public long getRecargo() {
        return recargo;
    }

    public long getIva() {
        return iva;
    }

    public long getTotal() {
        return total;
    }
}
