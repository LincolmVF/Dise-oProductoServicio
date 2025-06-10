package appmedi2.controller;

import appmedi2.entity.Medicamento;
import appmedi2.entity.Solicitud;
import appmedi2.service.MedicamentoService;
import appmedi2.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/voz")
public class VozController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private SolicitudService solicitudService;

    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarVoz(@RequestBody Map<String, String> payload) {
        String textoReconocido = payload.get("texto");
        Map<String, Object> response = new HashMap<>();

        try {
            Medicamento medicamento = medicamentoService.encontrarMejorCoincidencia(textoReconocido);

            if (medicamento != null) {
                Solicitud solicitud = solicitudService.crearSolicitud(medicamento, textoReconocido);

                response.put("encontrado", true);
                response.put("medicamento", medicamento.getNombre());
                response.put("disponible", solicitud.getEstado() != Solicitud.EstadoSolicitud.NO_DISPONIBLE);
                response.put("solicitudId", solicitud.getId());
                response.put("mensaje", "Medicamento encontrado: " + medicamento.getNombre());
            } else {
                response.put("encontrado", false);
                response.put("mensaje", "No se encontró el medicamento solicitado: " + textoReconocido);
            }
        } catch (Exception e) {
            response.put("encontrado", false);
            response.put("mensaje", "Error al procesar la solicitud");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarImpresion(@RequestBody Map<String, Long> payload) {
        Long solicitudId = payload.get("solicitudId");
        Map<String, Object> response = new HashMap<>();

        try {
            Solicitud solicitud = solicitudService.actualizarEstado(solicitudId, Solicitud.EstadoSolicitud.IMPRESO);

            if (solicitud != null) {
                // Actualizar stock
                Medicamento medicamento = solicitud.getMedicamento();
                if (medicamento.getStock() != null && medicamento.getStock() > 0) {
                    medicamento.setStock(medicamento.getStock() - 1);
                    medicamentoService.guardar(medicamento);
                }

                response.put("success", true);
                response.put("mensaje", "Medicamento impreso correctamente");
            } else {
                response.put("success", false);
                response.put("mensaje", "No se pudo confirmar la impresión");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al confirmar la impresión");
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }
}