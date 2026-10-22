package test;

import auth.Usuario;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
import vehiculo.TipoVehiculo;
import vehiculo.Vehiculo;
import vehiculo.VehiculoRepositorio;
import vehiculo.VehiculoServicio;

import java.util.List;

/** Pruebas del Módulo 3 (HU-07 a HU-09). Se ejecuta con main: cada prueba imprime OK o FALLO. */
public class VehiculoTest {

    private static int pasaron = 0;
    private static int fallaron = 0;

    public static void main(String[] args) {
        ejecutar();
        System.out.println("VehiculoTest: " + pasaron + " OK, " + fallaron + " con FALLO");
        if (fallaron > 0) {
            System.exit(1);
        }
    }

    public static void ejecutar() {
        // ---- HU-07 Registrar ----
        VehiculoServicio s = nuevoServicio();
        Usuario ana = new Usuario("Ana", "ana", "1234");

        Vehiculo v = s.registrar(ana, "abc123", TipoVehiculo.automovil, "Mazda");
        comprobar("HU-07 normal: queda registrado y asociado al cliente",
                v.getPlaca().equals("ABC123") && v.getPropietario() == ana
                        && s.listarPorCliente(ana).size() == 1);

        comprobar("HU-07 placa duplicada se rechaza y no modifica la lista",
                lanza(ReglaNegocioException.class, () -> s.registrar(ana, "ABC123", TipoVehiculo.moto, "Yamaha"))
                        && s.listarPorCliente(ana).size() == 1);

        comprobar("HU-07 placa con formato inválido (12AB) se rechaza",
                lanza(DatoInvalidoException.class, () -> s.registrar(ana, "12AB", TipoVehiculo.moto, "Yamaha"))
                        && s.listarPorCliente(ana).size() == 1);

        comprobar("HU-07 campo vacío se rechaza",
                lanza(DatoInvalidoException.class, () -> s.registrar(ana, "", TipoVehiculo.moto, "Yamaha"))
                        && lanza(DatoInvalidoException.class, () -> s.registrar(ana, "XYZ987", TipoVehiculo.moto, "  "))
                        && lanza(DatoInvalidoException.class, () -> s.registrar(ana, "XYZ987", null, "Yamaha")));

        comprobar("HU-07 placa con dígito final o letra final es válida",
                s.registrar(ana, "DEF45G", TipoVehiculo.camion, "Chevrolet") != null);

        // ---- HU-08 Consultar mis vehículos ----
        VehiculoServicio s2 = nuevoServicio();
        Usuario luis = new Usuario("Luis", "luis", "1234");
        Usuario eva = new Usuario("Eva", "eva", "1234");
        s2.registrar(luis, "AAA111", TipoVehiculo.moto, "Honda");
        s2.registrar(luis, "BBB222", TipoVehiculo.camioneta, "Toyota");
        s2.registrar(eva, "CCC333", TipoVehiculo.automovil, "Kia");

        comprobar("HU-08 cliente con vehículos ve solo los suyos",
                s2.listarPorCliente(luis).size() == 2 && s2.listarPorCliente(eva).size() == 1);

        comprobar("HU-08 cliente sin vehículos recibe lista vacía",
                s2.listarPorCliente(new Usuario("Nuevo", "nuevo", "1234")).isEmpty());

        comprobar("HU-08 los vehículos de otros clientes no aparecen",
                s2.listarPorCliente(eva).stream().allMatch(x -> x.getPropietario() == eva));

        // ---- HU-09 Buscar y filtrar ----
        comprobar("HU-09 placa existente devuelve el vehículo y su propietario",
                s2.buscarPorPlaca("aaa111").map(x -> x.getPropietario() == luis).orElse(false));

        comprobar("HU-09 placa inexistente devuelve vacío sin error",
                s2.buscarPorPlaca("ZZZ999").isEmpty());

        List<Vehiculo> motos = s2.filtrarPorTipo(TipoVehiculo.moto);
        comprobar("HU-09 filtrar por tipo muestra solo ese tipo",
                motos.size() == 1 && motos.get(0).getTipo() == TipoVehiculo.moto);

        comprobar("HU-09 filtrar por tipo sin resultados devuelve lista vacía",
                s2.filtrarPorTipo(TipoVehiculo.camion).isEmpty());

        comprobar("HU-09 filtrar por cliente muestra solo los de ese cliente",
                s2.filtrarPorCliente(luis).size() == 2);
    }

    // ----- ayudantes -----
    private static VehiculoServicio nuevoServicio() {
        return new VehiculoServicio(new VehiculoRepositorio());
    }

    private static boolean lanza(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (RuntimeException e) {
            return tipo.isInstance(e);
        }
    }

    private static void comprobar(String nombre, boolean condicion) {
        if (condicion) {
            pasaron++;
            System.out.println("  OK     " + nombre);
        } else {
            fallaron++;
            System.out.println("  FALLO  " + nombre);
        }
    }
}
