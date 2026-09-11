package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Clinica;
import com.umg.citasmedicas.model.Consultorio;
import com.umg.citasmedicas.repository.ClinicaRepository;
import com.umg.citasmedicas.repository.ConsultorioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/consultorios")
public class ConsultorioController {

    private final ConsultorioRepository consultorioRepository;
    private final ClinicaRepository clinicaRepository;

    public ConsultorioController(ConsultorioRepository consultorioRepository,
                                 ClinicaRepository clinicaRepository) {
        this.consultorioRepository = consultorioRepository;
        this.clinicaRepository = clinicaRepository;
    }

    @GetMapping
    public List<Consultorio> listarTodos() {
        return consultorioRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultorio> obtenerPorId(@PathVariable Integer id) {
        return consultorioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Consultorio> crear(@RequestBody Consultorio consultorio) {
        // El JSON manda solo el id de la clínica dentro de "clinica";
        // aquí lo resolvemos contra la base para obtener la entidad real.
        Clinica clinica = clinicaRepository.findById(consultorio.getClinica().getIdClinica())
                .orElseThrow(() -> new IllegalArgumentException("Clinica no encontrada"));
        consultorio.setClinica(clinica);

        Consultorio guardado = consultorioRepository.save(consultorio);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultorio> actualizar(@PathVariable Integer id, @RequestBody Consultorio datos) {
        return consultorioRepository.findById(id)
                .map(existente -> {
                    existente.setNumero(datos.getNumero());
                    existente.setPiso(datos.getPiso());
                    existente.setEquipoDisponible(datos.getEquipoDisponible());
                    return ResponseEntity.ok(consultorioRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!consultorioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        consultorioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}