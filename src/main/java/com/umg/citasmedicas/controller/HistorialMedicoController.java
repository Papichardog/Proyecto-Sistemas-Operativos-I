package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.HistorialMedico;
import com.umg.citasmedicas.model.Paciente;
import com.umg.citasmedicas.repository.HistorialMedicoRepository;
import com.umg.citasmedicas.repository.PacienteRepository;
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
@RequestMapping("/api/historiales")
public class HistorialMedicoController {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final PacienteRepository pacienteRepository;

    public HistorialMedicoController(HistorialMedicoRepository historialMedicoRepository,
                                     PacienteRepository pacienteRepository) {
        this.historialMedicoRepository = historialMedicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public List<HistorialMedico> listarTodos() {
        return historialMedicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialMedico> obtenerPorId(@PathVariable Integer id) {
        return historialMedicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Como Paciente no guarda referencia de vuelta a su historial,
    // esta es la forma de encontrarlo: por el id del paciente.
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<HistorialMedico> obtenerPorPaciente(@PathVariable Integer idPaciente) {
        return historialMedicoRepository.findByPacienteIdPersona(idPaciente)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HistorialMedico> crear(@RequestBody HistorialMedico historial) {
        Paciente paciente = pacienteRepository.findById(historial.getPaciente().getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        historial.setPaciente(paciente);

        HistorialMedico guardado = historialMedicoRepository.save(historial);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialMedico> actualizar(@PathVariable Integer id, @RequestBody HistorialMedico datos) {
        return historialMedicoRepository.findById(id)
                .map(existente -> {
                    existente.setObservacionesGenerales(datos.getObservacionesGenerales());
                    return ResponseEntity.ok(historialMedicoRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!historialMedicoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        historialMedicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
