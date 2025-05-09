package medicamento.medicamentovoz.repository;

import medicamento.medicamentovoz.entity.Peticion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeticionRepository extends JpaRepository<Peticion, Long> {
    // Aquí puedes añadir métodos personalizados de consulta si los necesitas
}