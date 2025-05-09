package medicamento.medicamentovoz.controller;

import medicamento.medicamentovoz.entity.Peticion;
import medicamento.medicamentovoz.repository.PeticionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PeticionController {

    @Autowired
    private PeticionRepository peticionRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/peticiones")
    public String listarPeticiones(Model model) {
        model.addAttribute("peticiones", peticionRepository.findAll());
        return "peticiones";
    }

    @PostMapping("/guardar-peticion")
    @ResponseBody
    public String guardarPeticion(@RequestParam String contenido) {
        Peticion peticion = new Peticion(contenido);
        peticionRepository.save(peticion);
        return "Petición guardada con éxito";
    }

    @PostMapping("/eliminar-peticion/{id}")
    @ResponseBody
    public String eliminarPeticion(@PathVariable Long id) {
        try {
            peticionRepository.deleteById(id);
            return "Petición eliminada con éxito";
        } catch (Exception e) {
            return "Error al eliminar la petición: " + e.getMessage();
        }
    }
}
