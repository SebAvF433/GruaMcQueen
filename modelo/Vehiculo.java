package modelo;

public class Vehiculo {
    String marca;
    String modelo;
    String placa;
    String color;

    public Vehiculo(String marca, String modelo, String placa, String color) {
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
        this.color = color;
    }

    void mostrarVehiculo() {
        System.out.println("Marca: " + marca);
        System.out.println("Modelo: " + modelo);
        System.out.println("Placa: " + placa);
        System.out.println("Color: " + color);
    }
}
