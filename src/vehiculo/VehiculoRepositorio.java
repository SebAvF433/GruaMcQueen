package vehiculo;

import auth.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** "Despensa" de vehículos en memoria. Solo guarda y entrega datos; no aplica reglas. */
public class VehiculoRepositorio {

    private final List<Vehiculo> vehiculos = new ArrayList<>();

    public void guardar(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
    }

    /** Busca por placa. Optional = "una caja que puede venir vacía" (así no hay error si no existe). */
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return vehiculos.stream()
                .filter(v -> v.getPlaca().equalsIgnoreCase(placa.trim()))
                .findFirst();
    }

    public List<Vehiculo> listarPorCliente(Usuario cliente) {
        return vehiculos.stream()
                .filter(v -> v.getPropietario().equals(cliente))
                .toList();
    }

    /** Devuelve una copia: quien la reciba no puede dañar la lista original. */
    public List<Vehiculo> listarTodos() {
        return List.copyOf(vehiculos);
    }
}
