package appmedi2.repository;

import appmedi2.entity.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByOrderByFechaSolicitudDesc();

    List<Solicitud> findByFechaSolicitudBetweenOrderByFechaSolicitudDesc(
            LocalDateTime inicio, LocalDateTime fin);
}