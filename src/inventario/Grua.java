package inventario;

public class Grua {
    private final String placa;
    private TipoGrua tipo;
    private EstadoGrua estado;

    public Grua(String placa, TipoGrua tipo) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa de la grúa es obligatoria.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de grúa es obligatorio.");
        }
        this.placa = placa.trim().toUpperCase();
        this.tipo = tipo;
        this.estado = EstadoGrua.DISPONIBLE; // Por defecto nace disponible (HU-10)
    }

    public String getPlaca() {
        return placa;
    }

    public TipoGrua getTipo() {
        return tipo;
    }

    public EstadoGrua getEstado() {
        return estado;
    }

    public void setEstado(EstadoGrua estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }
}