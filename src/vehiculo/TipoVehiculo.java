package vehiculo;

/** Los 4 tipos de vehículo que acepta el sistema. Un enum es una lista cerrada de opciones. */
public enum TipoVehiculo {
    moto("Moto"),
    automovil("Automóvil"),
    camioneta("Camioneta"),
    camion("Camión");

    private final String t_vehiculo;

    TipoVehiculo(String t_vehiculo){
        this.t_vehiculo = t_vehiculo;
    }

    public String getTipoVehiculo(){
        return t_vehiculo;
    }
}
