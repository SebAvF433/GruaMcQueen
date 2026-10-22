package vehiculo;

import auth.Usuario;

/** Un vehículo registrado por un cliente. Es como la "ficha" del vehículo. */
public class Vehiculo {

    private final String placa;
    private final TipoVehiculo tipo;
    private final String marca;
    private final Usuario propietario;

    public Vehiculo(String placa, TipoVehiculo tipo, String marca, Usuario propietario) {
        this.placa = placa;
        this.tipo = tipo;
        this.marca = marca;
        this.propietario = propietario;
    }

    public String getPlaca() {
        return placa;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public String getMarca() {
        return marca;
    }

    public Usuario getPropietario() {
        return propietario;
    }
}
