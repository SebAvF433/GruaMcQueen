package vehiculo;

import auth.Usuario;
import comun.DatoInvalidoException;
import comun.ReglaNegocioException;
import comun.Validador;

import java.util.List;
import java.util.Optional;

/** "Cocinero" de vehículos: aquí viven las reglas del negocio. */
public class VehiculoServicio {

    private final VehiculoRepositorio repositorio;

    public VehiculoServicio(VehiculoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * HU-07. Registra un vehículo para el cliente activo.
     * Primero revisa TODO; solo si todo está bien lo guarda (así un rechazo no cambia la lista).
     */
    public Vehiculo registrar(Usuario cliente, String placa, TipoVehiculo tipo, String marca) {
        if (placa == null || placa.isBlank()) {
            throw new DatoInvalidoException("La placa del vehículo es obligatoria.");
        }
        if (marca == null || marca.isBlank()) {
            throw new DatoInvalidoException("La marca no puede estar vacía.");
        }
        if (tipo == null) {
            throw new DatoInvalidoException("Debe elegir un tipo de vehículo.");
        }
        if (!Validador.placaValida(placa)) {
            throw new DatoInvalidoException("Placa inválida. Use 3 letras, 2 dígitos y un dígito (ej: ABC123).");
        }

        String placaNormalizada = placa.trim().toUpperCase();
        if (repositorio.buscarPorPlaca(placaNormalizada).isPresent()) {
            throw new ReglaNegocioException("La placa " + placaNormalizada + " ya está registrada.");
        }

        Vehiculo vehiculo = new Vehiculo(placaNormalizada, tipo, marca.trim(), cliente);
        repositorio.guardar(vehiculo);
        return vehiculo;
    }

    /** HU-08. Solo los vehículos de ese cliente (lista vacía si no tiene). */
    public List<Vehiculo> listarPorCliente(Usuario cliente) {
        return repositorio.listarPorCliente(cliente);
    }

    /** HU-09. Devuelve vacío (no error) si la placa no existe. */
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return Optional.empty();
        }
        return repositorio.buscarPorPlaca(placa);
    }

    /** HU-09. Solo los vehículos de ese tipo. */
    public List<Vehiculo> filtrarPorTipo(TipoVehiculo tipo) {
        return repositorio.listarTodos().stream()
                .filter(v -> v.getTipo() == tipo)
                .toList();
    }

    /** HU-09. Solo los vehículos de ese cliente. */
    public List<Vehiculo> filtrarPorCliente(Usuario cliente) {
        return repositorio.listarPorCliente(cliente);
    }
}
