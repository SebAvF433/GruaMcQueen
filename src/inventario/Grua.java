package inventario;

public class Grua {
    String nombre_conductor;
    String modelo;
    String marca;
    String placa;
    String color;

    public Grua(String nombre_conductor, String modelo, String marca, String placa, String color){
        this.nombre_conductor = nombre_conductor;
        this.modelo = modelo;
        this.marca = marca;
        this.placa = placa;
        this.color = color;
    }

    void mostrarGrua(){
        System.out.println("Conductor: " + nombre_conductor);
        System.out.println("Marca: " + marca);
        System.out.println("Modelo: " + modelo);
        System.out.println("Placa: " + placa);
        System.out.println("Color: " + color);
    }
}
