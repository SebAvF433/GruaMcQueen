package inventario;

import java.util.ArrayList;
import java.util.List;

public class GruaServicio {
    private final GruaRepositorio repositorio = new GruaRepositorio();

    public boolean registrar(String placa, TipoGrua tipo) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de grúa es obligatorio.");
        }
        Grua grua = new Grua(placa, tipo);
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

    public void cambiarEstado(String placa, EstadoGrua nuevoEstado) {
        Grua grua = repositorio.buscarPorPlaca(placa);
        if (grua == null) {
            throw new IllegalArgumentException("Grúa no encontrada con placa: " + placa);
        }
        // Regla de negocio fundamental de la HU-12: Una grúa EN_SERVICIO no puede pasar a mantenimiento
        if (grua.getEstado() == EstadoGrua.EN_SERVICIO && nuevoEstado == EstadoGrua.MANTENIMIENTO) {
            throw new IllegalStateException("Una grúa EN_SERVICIO no puede pasar a mantenimiento mientras tenga un servicio activo.");
        }
        grua.setEstado(nuevoEstado);
    }
}