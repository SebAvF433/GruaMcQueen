package test;

import inventario.EstadoGrua;
import inventario.GruaRepositorio;
import inventario.GruaServicio;
import inventario.TipoGrua;

public class InventarioTest {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE INVENTARIO ===");
        GruaServicio servicio = new GruaServicio(new GruaRepositorio());

        // Prueba 1: Registrar una grúa con datos válidos (HU-10)
        boolean reg1 = servicio.registrar("GUA001", TipoGrua.PLATAFORMA);
        System.out.println("Prueba 1 (Registro exitoso): " + (reg1 ? "EXITOSO" : "FALLÓ"));

        // Prueba 2: Intentar registrar una grúa con placa duplicada (HU-10)
        boolean reg2 = servicio.registrar("GUA001", TipoGrua.PESADA);
        System.out.println("Prueba 2 (Rechazar placa duplicada): " + (!reg2 ? "EXITOSO" : "FALLÓ"));

        // Prueba 3: Cambiar estado a EN_SERVICIO y probar restricción a MANTENIMIENTO (HU-12)
        servicio.cambiarEstado("GUA001", EstadoGrua.EN_SERVICIO);
        try {
            servicio.cambiarEstado("GUA001", EstadoGrua.EN_MANTENIMIENTO);
            System.out.println("Prueba 3 (Regla de negocio EN_SERVICIO -> MANTENIMIENTO): FALLÓ (Debería bloquearlo)");
        } catch (IllegalStateException e) {
            System.out.println("Prueba 3 (Regla de negocio EN_SERVICIO -> MANTENIMIENTO): EXITOSO -> " + e.getMessage());
        }

        System.out.println("=== FIN DE PRUEBAS DE INVENTARIO ===");
    }
}