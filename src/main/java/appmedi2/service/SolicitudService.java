package appmedi2.service;

import appmedi2.entity.Medicamento;
import appmedi2.entity.Solicitud;
import appmedi2.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    public List<Solicitud> listarTodas() {
        return solicitudRepository.findByOrderByFechaSolicitudDesc();
    }

    public List<Solicitud> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return solicitudRepository.findByFechaSolicitudBetweenOrderByFechaSolicitudDesc(inicio, fin);
    }

    public Optional<Solicitud> buscarPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    public Solicitud guardar(Solicitud solicitud) {
        return solicitudRepository.save(solicitud);
    }

    public Solicitud crearSolicitud(Medicamento medicamento, String textoReconocido) {
        Solicitud solicitud = new Solicitud();
        solicitud.setMedicamento(medicamento);
        solicitud.setTextoReconocido(textoReconocido);

        if (medicamento.getStock() != null && medicamento.getStock() > 0) {
            solicitud.setEstado(Solicitud.EstadoSolicitud.SOLICITADO);
        } else {
            solicitud.setEstado(Solicitud.EstadoSolicitud.NO_DISPONIBLE);
        }

        return guardar(solicitud);
    }

    public Solicitud actualizarEstado(Long id, Solicitud.EstadoSolicitud estado) {
        Optional<Solicitud> solicitudOpt = buscarPorId(id);
        if (solicitudOpt.isPresent()) {
            Solicitud solicitud = solicitudOpt.get();
            solicitud.setEstado(estado);
            return guardar(solicitud);
        }
        return null;
    }
}