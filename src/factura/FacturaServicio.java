package factura;

import auth.Usuario;
import excepcion.ReglaNegocioException;
import solicitud.EstadoSolicitud;
import solicitud.Solicitud;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** "Cocinero" de facturas: decide cuándo se puede facturar y arma la factura. */
public class FacturaServicio {

    private final FacturaRepositorio repositorio;
    private final TarifaServicio tarifaServicio;

    public FacturaServicio(FacturaRepositorio repositorio, TarifaServicio tarifaServicio) {
        this.repositorio = repositorio;
        this.tarifaServicio = tarifaServicio;
    }

    /**
     * HU-19. Genera la factura de una solicitud FINALIZADA.
     * Se rechaza si no está finalizada o si ya tiene factura (y entonces no se guarda nada).
     */
    public Factura generar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new ReglaNegocioException("No hay solicitud para facturar.");
        }
        if (solicitud.getEstado() != EstadoSolicitud.FINALIZADA) {
            throw new ReglaNegocioException(
                    "Solo se factura una solicitud FINALIZADA (la solicitud " + solicitud.getId() + " no lo está).");
        }
        if (repositorio.buscarPorSolicitud(solicitud.getId()).isPresent()) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getId() + " ya tiene factura.");
        }

        boolean nocturno = esNocturno(solicitud.getFechaHora());
        TarifaServicio.Desglose d = tarifaServicio.calcular(
                solicitud.getKm(), solicitud.getVehiculo().getTipo(), nocturno);

        Factura factura = new Factura(
                repositorio.siguienteId(), solicitud.getId(), solicitud.getCliente(), LocalDateTime.now(),
                solicitud.getDestino(), solicitud.getKm(),
                d.getSubtotal(), d.getRecargo(), d.getIva(), d.getTotal());
        repositorio.guardar(factura);
        return factura;
    }

    /** HU-20. Solo las facturas de ese cliente. */
    public List<Factura> listarPorCliente(Usuario cliente) {
        return repositorio.listar().stream()
                .filter(f -> f.getCliente().equals(cliente))
                .toList();
    }

    /** HU-21. Todas las facturas (lista vacía si no hay). */
    public List<Factura> listar() {
        return repositorio.listar();
    }

    /** HU-21. Devuelve una "caja vacía" (sin error) si el identificador no existe. */
    public Optional<Factura> buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return repositorio.buscarPorId(id);
    }

    /** Es de noche entre las 19:00 y las 05:59. */
    private boolean esNocturno(LocalDateTime momento) {
        int hora = momento.getHour();
        return hora >= Tarifas.HORA_INICIO_NOCTURNO || hora < Tarifas.HORA_FIN_NOCTURNO;
    }
}
