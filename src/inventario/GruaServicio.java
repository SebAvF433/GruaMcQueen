package inventario;

import vehiculo.TipoVehiculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GruaServicio {
    private final GruaRepositorio repositorio;

    public GruaServicio(GruaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** HU-10. Crea la grúa (nace DISPONIBLE) y la guarda. Devuelve false si la placa ya existe. */
    public boolean registrar(String placa, TipoGrua tipo) {
        return registrar(new Grua(placa, tipo));
    }

    public boolean registrar(Grua grua) {
        return repositorio.guardar(grua);
    }

    public Grua buscarPorPlaca(String placa) {
        return repositorio.buscarPorPlaca(placa);
    }

    public List<Grua> listar() {
        return repositorio.listar();
    }

    public List<Grua> filtrarPorEstado(EstadoGrua estado) {
        List<Grua> resultado = new ArrayList<>();
        for (Grua grua : repositorio.listar()) {
            if (grua.getEstado() == estado) {
                resultado.add(grua);
            }
        }
        return resultado;
    }

    public List<Grua> filtrarPorTipo(TipoGrua tipo) {
        List<Grua> resultado = new ArrayList<>();
        for (Grua grua : repositorio.listar()) {
            if (grua.getTipo() == tipo) {
                resultado.add(grua);
            }
        }
        return resultado;
    }

    /**
     * HU-16. Primera grúa DISPONIBLE que sirve para ese tipo de vehículo:
     * al camión solo le sirve la PESADA; a los demás, la PLATAFORMA o la de ARRASTRE.
     */
    public Optional<Grua> buscarDisponibleCompatible(TipoVehiculo tipoVehiculo) {
        for (Grua grua : repositorio.listar()) {
            if (grua.getEstado() == EstadoGrua.DISPONIBLE && esCompatible(grua.getTipo(), tipoVehiculo)) {
                return Optional.of(grua);
            }
        }
        return Optional.empty();
    }

    private boolean esCompatible(TipoGrua tipoGrua, TipoVehiculo tipoVehiculo) {
        if (tipoVehiculo == TipoVehiculo.CAMION) {
            return tipoGrua == TipoGrua.PESADA;
        }
        return tipoGrua == TipoGrua.PLATAFORMA || tipoGrua == TipoGrua.ARRASTRE;
    }

    public void cambiarEstado(String placa, EstadoGrua nuevoEstado) {
        Grua grua = repositorio.buscarPorPlaca(placa);
        if (grua == null) {
            throw new IllegalArgumentException("Grúa no encontrada con placa: " + placa);
        }
        // Regla de negocio fundamental de la HU-12: Una grúa EN_SERVICIO no puede pasar a mantenimiento
        if (grua.getEstado() == EstadoGrua.EN_SERVICIO && nuevoEstado == EstadoGrua.EN_MANTENIMIENTO) {
            throw new IllegalStateException("Una grúa EN_SERVICIO no puede pasar a mantenimiento mientras tenga un servicio activo.");
        }
        grua.setEstado(nuevoEstado);
    }
}
