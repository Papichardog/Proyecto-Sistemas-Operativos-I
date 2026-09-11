package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Cita;
import com.umg.citasmedicas.model.Consultorio;
import com.umg.citasmedicas.model.Medico;
import com.umg.citasmedicas.model.Paciente;
import com.umg.citasmedicas.repository.CitaRepository;
import com.umg.citasmedicas.repository.ConsultorioRepository;
import com.umg.citasmedicas.repository.MedicoRepository;
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
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultorioRepository consultorioRepository;

    public CitaController(CitaRepository citaRepository,
                          PacienteRepository pacienteRepository,
                          MedicoRepository medicoRepository,
                          ConsultorioRepository consultorioRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.consultorioRepository = consultorioRepository;
    }

    @GetMapping
    public List<Cita> listarTodas() {
        return citaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerPorId(@PathVariable Integer id) {
        return citaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Reemplaza la necesidad de leer Paciente.citas (que ahora está
    // oculto del JSON). Cada cita viene con su medico y consultorio
    // completos, sin ciclos.
    @GetMapping("/paciente/{idPaciente}")
    public List<Cita> listarPorPaciente(@PathVariable Integer idPaciente) {
        return citaRepository.findByPacienteIdPersona(idPaciente);
    }

    // Mismo caso para el dashboard de médico: reemplaza Medico.citas.
    @GetMapping("/medico/{idMedico}")
    public List<Cita> listarPorMedico(@PathVariable Integer idMedico) {
        return citaRepository.findByMedicoIdPersona(idMedico);
    }

    @PostMapping
    public ResponseEntity<Cita> crear(@RequestBody Cita cita) {
        // El JSON manda paciente, medico y consultorio solo con su id;
        // resolvemos cada uno contra la base antes de guardar la cita.
        Paciente paciente = pacienteRepository.findById(cita.getPaciente().getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        Medico medico = medicoRepository.findById(cita.getMedico().getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("Medico no encontrado"));
        Consultorio consultorio = consultorioRepository.findById(cita.getConsultorio().getIdConsultorio())
                .orElseThrow(() -> new IllegalArgumentException("Consultorio no encontrado"));

        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setConsultorio(consultorio);

        Cita guardada = citaRepository.save(cita);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(@PathVariable Integer id, @RequestBody Cita datos) {
        return citaRepository.findById(id)
                .map(existente -> {
                    existente.setFecha(datos.getFecha());
                    existente.setHora(datos.getHora());
                    existente.setEstado(datos.getEstado());
                    existente.setMotivoConsulta(datos.getMotivoConsulta());
                    return ResponseEntity.ok(citaRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!citaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        citaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}