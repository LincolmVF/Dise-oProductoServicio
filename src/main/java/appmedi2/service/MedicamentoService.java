package appmedi2.service;

import appmedi2.entity.Medicamento;
import appmedi2.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    public Optional<Medicamento> buscarPorId(Long id) {
        return medicamentoRepository.findById(id);
    }

    public Optional<Medicamento> buscarPorNombre(String nombre) {
        return medicamentoRepository.findByNombre(nombre);
    }

    public List<Medicamento> buscarPorTexto(String texto) {
        return medicamentoRepository.buscarPorTexto(texto);
    }

    public Medicamento guardar(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    public void eliminar(Long id) {
        medicamentoRepository.deleteById(id);
    }

    public boolean existeMedicamento(String texto) {
        List<Medicamento> medicamentos = buscarPorTexto(texto);
        return !medicamentos.isEmpty();
    }

    public Medicamento encontrarMejorCoincidencia(String texto) {
        List<Medicamento> coincidencias = buscarPorTexto(texto);
        if (coincidencias.isEmpty()) {
            return null;
        }
        // Devuelve la primera coincidencia (podría implementarse un algoritmo más sofisticado)
        return coincidencias.get(0);
    }
}