package appmedi2.repository;



import appmedi2.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    Optional<Medicamento> findByNombre(String nombre);

    @Query("SELECT m FROM Medicamento m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(m.nombresAlternativos) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Medicamento> buscarPorTexto(@Param("texto") String texto);
}