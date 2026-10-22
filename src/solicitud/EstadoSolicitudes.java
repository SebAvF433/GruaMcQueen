package solicitud;
    public enum EstadoSolicitudes{
        solicitada("SOLICITADA"), 
        asignada("ASIGNADA"), 
        enCurso("EN CURSO"), 
        finalizada("FINALIZADA"), 
        cancelada("CANCELADA");
    
    private final String etiqueta;
 
    EstadoSolicitudes(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean estadoActivo() {
        return this == solicitada || this == asignada || this == enCurso;
    }
}