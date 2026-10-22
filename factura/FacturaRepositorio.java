package factura;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** "Despensa" de facturas en memoria. Solo guarda y entrega datos; no aplica reglas. */
public class FacturaRepositorio {

    private final List<Factura> facturas = new ArrayList<>();
    private int ultimoNumero = 0;

    /** Entrega el siguiente identificador: FAC-001, FAC-002, ... */
    public String siguienteId() {
        ultimoNumero++;
        return String.format("FAC-%03d", ultimoNumero);
    }

    public void guardar(Factura factura) {
        facturas.add(factura);
    }

    /** Devuelve una copia: quien la reciba no puede dañar la lista original. */
    public List<Factura> listar() {
        return List.copyOf(facturas);
    }

    public Optional<Factura> buscarPorId(String id) {
        return facturas.stream()
                .filter(f -> f.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public Optional<Factura> buscarPorSolicitud(String idSolicitud) {
        return facturas.stream()
                .filter(f -> f.getIdSolicitud().equals(idSolicitud))
                .findFirst();
    }
}
