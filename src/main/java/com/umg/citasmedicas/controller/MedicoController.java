package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Especialidad;
import com.umg.citasmedicas.model.Medico;
import com.umg.citasmedicas.repository.EspecialidadRepository;
import com.umg.citasmedicas.repository.MedicoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/api/medicos")
public class MedicoController {

    private final MedicoRepository medicoRepository;
    private final EspecialidadRepository especialidadRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoController(MedicoRepository medicoRepository,
                            EspecialidadRepository especialidadRepository,
                            PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> obtenerPorId(@PathVariable Integer id) {
        return medicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Medico> crear(@RequestBody Medico medico) {
        // El JSON manda solo el id de la especialidad; la resolvemos
        // contra la base para obtener la entidad real antes de guardar.
        Especialidad especialidad = especialidadRepository.findById(medico.getEspecialidad().getIdEspecialidad())
                .orElseThrow(() -> new IllegalArgumentException("Especialidad no encontrada"));
        medico.setEspecialidad(especialidad);
        medico.setPassword(passwordEncoder.encode(medico.getPassword()));

        Medico guardado = medicoRepository.save(medico);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medico> actualizar(@PathVariable Integer id, @RequestBody Medico datos) {
        return medicoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setApellido(datos.getApellido());
                    existente.setCorreo(datos.getCorreo());
                    if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
                        existente.setPassword(passwordEncoder.encode(datos.getPassword()));
                    }
                    existente.setTelefono(datos.getTelefono());
                    existente.setFechaNacimiento(datos.getFechaNacimiento());
                    existente.setNumeroColegiado(datos.getNumeroColegiado());
                    existente.setAniosExperiencia(datos.getAniosExperiencia());
                    return ResponseEntity.ok(medicoRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!medicoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        medicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}