package medicamento.medicamentovoz.service;

import medicamento.medicamentovoz.entity.Peticion;
import medicamento.medicamentovoz.repository.PeticionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeticionService {

    private final PeticionRepository peticionRepository;

    @Autowired
    public PeticionService(PeticionRepository peticionRepository) {
        this.peticionRepository = peticionRepository;
    }

    public List<Peticion> obtenerTodas() {
        return peticionRepository.findAll();
    }

    public Optional<Peticion> obtenerPorId(Long id) {
        return peticionRepository.findById(id);
    }

    public Peticion crear(Peticion peticion) {
        return peticionRepository.save(peticion);
    }

    public Peticion actualizar(Long id, Peticion peticionActualizada) {
        return peticionRepository.findById(id).map(peticionExistente -> {
            peticionExistente.setContenido(peticionActualizada.getContenido());
            peticionExistente.setEstado(peticionActualizada.getEstado());
            return peticionRepository.save(peticionExistente);
        }).orElseThrow(() -> new RuntimeException("Petición no encontrada con ID: " + id));
    }

    public void eliminar(Long id) {
        if (!peticionRepository.existsById(id)) {
            throw new RuntimeException("Petición no encontrada con ID: " + id);
        }
        peticionRepository.deleteById(id);
    }
}