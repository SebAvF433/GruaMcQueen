package solicitud;

public enum EstadoSolicitud {
    SOLICITADA("SOLICITADA"),
    ASIGNADA("ASIGNADA"),
    EN_CURSO("EN CURSO"),
    FINALIZADA("FINALIZADA"),
    CANCELADA("CANCELADA");

    private final String etiqueta;

    EstadoSolicitud(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Una solicitud activa es la que todavía no terminó ni se canceló. */
    public boolean estadoActivo() {
        return this == SOLICITADA || this == ASIGNADA || this == EN_CURSO;
    }
}
