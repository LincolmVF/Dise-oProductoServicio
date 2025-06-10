package appmedi2.controller;

import appmedi2.entity.Medicamento;
import appmedi2.entity.Solicitud;
import appmedi2.service.MedicamentoService;
import appmedi2.service.PdfService;
import appmedi2.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MedicamentoService medicamentoService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/medicamentos")
    public String listarMedicamentos(Model model) {
        model.addAttribute("medicamentos", medicamentoService.listarTodos());
        model.addAttribute("medicamento", new Medicamento());
        return "admin/medicamentos";
    }

    @PostMapping("/medicamentos")
    public String guardarMedicamento(@ModelAttribute Medicamento medicamento, RedirectAttributes redirectAttributes) {
        try {
            medicamentoService.guardar(medicamento);
            redirectAttributes.addFlashAttribute("mensaje", "Medicamento guardado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el medicamento: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/medicamentos";
    }

    @GetMapping("/medicamentos/{id}")
    public String editarMedicamento(@PathVariable Long id, Model model) {
        Optional<Medicamento> medicamentoOpt = medicamentoService.buscarPorId(id);
        if (medicamentoOpt.isPresent()) {
            model.addAttribute("medicamento", medicamentoOpt.get());
        } else {
            model.addAttribute("medicamento", new Medicamento());
        }
        model.addAttribute("medicamentos", medicamentoService.listarTodos());
        return "admin/medicamentos";
    }

    @GetMapping("/medicamentos/eliminar/{id}")
    public String eliminarMedicamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicamentoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Medicamento eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el medicamento: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/medicamentos";
    }

    @GetMapping("/historial")
    public String verHistorial(Model model) {
        List<Solicitud> solicitudes = solicitudService.listarTodas();
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("totalSolicitudes", solicitudes.size());
        return "admin/historial";
    }

    @GetMapping("/historial/pdf")
    public ResponseEntity<InputStreamResource> exportarHistorialPdf() {
        try {
            List<Solicitud> solicitudes = solicitudService.listarTodas();

            ByteArrayInputStream bis = pdfService.generarHistorialPdf(solicitudes);

            HttpHeaders headers = new HttpHeaders();
            String filename = "historial_medicamentos_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            headers.add("Content-Disposition", "inline; filename=" + filename);

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historial/filtrar")
    public String filtrarHistorial(@RequestParam String fechaInicio,
                                   @RequestParam String fechaFin,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            LocalDateTime inicioDateTime = inicio.atStartOfDay();
            LocalDateTime finDateTime = fin.atTime(LocalTime.MAX);

            List<Solicitud> solicitudes = solicitudService.listarPorRangoFechas(inicioDateTime, finDateTime);
            model.addAttribute("solicitudes", solicitudes);
            model.addAttribute("totalSolicitudes", solicitudes.size());
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("filtroAplicado", true);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al filtrar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/historial";
        }

        return "admin/historial";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas básicas
        List<Medicamento> medicamentos = medicamentoService.listarTodos();
        List<Solicitud> solicitudes = solicitudService.listarTodas();

        model.addAttribute("totalMedicamentos", medicamentos.size());
        model.addAttribute("totalSolicitudes", solicitudes.size());

        // Medicamentos con stock bajo (menos de 5)
        long medicamentosStockBajo = medicamentos.stream()
                .filter(m -> m.getStock() != null && m.getStock() < 5)
                .count();
        model.addAttribute("medicamentosStockBajo", medicamentosStockBajo);

        return "admin/dashboard";
    }


    // Agregar este método al AdminController.java
    @PostMapping("/historial/cambiarEstado")
    public String cambiarEstadoSolicitud(@RequestParam Long solicitudId,
                                         @RequestParam Solicitud.EstadoSolicitud nuevoEstado,
                                         RedirectAttributes redirectAttributes) {
        try {
            Solicitud solicitud = solicitudService.actualizarEstado(solicitudId, nuevoEstado);

            if (solicitud != null) {
                // Si cambia a IMPRESO y no estaba impreso antes, actualizar stock
                if (nuevoEstado == Solicitud.EstadoSolicitud.IMPRESO &&
                        solicitud.getEstado() != Solicitud.EstadoSolicitud.IMPRESO) {

                    Medicamento medicamento = solicitud.getMedicamento();
                    if (medicamento.getStock() != null && medicamento.getStock() > 0) {
                        medicamento.setStock(medicamento.getStock() - 1);
                        medicamentoService.guardar(medicamento);
                    }
                }

                redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "No se encontró la solicitud");
                redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar estado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }

        return "redirect:/admin/historial";
    }
}