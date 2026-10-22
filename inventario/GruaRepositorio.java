package inventario;

import java.util.ArrayList;
import java.util.List;

public class GruaRepositorio {
    private final List<Grua> gruas = new ArrayList<>();

    public boolean guardar(Grua nuevaGrua) {
        if (nuevaGrua == null || buscarPorPlaca(nuevaGrua.getPlaca()) != null) {
            return false; // Retorna falso si la grúa es nula o si la placa ya existe (HU-10)
        }
        return gruas.add(nuevaGrua);
    }

    public Grua buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return null;
        }
        for (Grua grua : gruas) {
            if (grua.getPlaca().equalsIgnoreCase(placa.trim())) {
                return grua;
            }
        }
        return null;
    }

    public List<Grua> listar() {
        return new ArrayList<>(gruas); // Devuelve una copia para proteger la lista interna
    }
}