package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Paciente;
import com.umg.citasmedicas.repository.PacienteRepository;
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
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteController(PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // GET /api/pacientes - listar todos
    @GetMapping
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    // GET /api/pacientes/{id} - obtener uno
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> obtenerPorId(@PathVariable Integer id) {
        return pacienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/pacientes - crear
    @PostMapping
    public ResponseEntity<Paciente> crear(@RequestBody Paciente paciente) {
        paciente.setPassword(passwordEncoder.encode(paciente.getPassword()));
        Paciente guardado = pacienteRepository.save(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // PUT /api/pacientes/{id} - actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Paciente> actualizar(@PathVariable Integer id, @RequestBody Paciente datos) {
        return pacienteRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setApellido(datos.getApellido());
                    existente.setCorreo(datos.getCorreo());
                    // Solo tocamos la contraseña si mandaron una nueva;
                    // si no, se quedaría en null y borraríamos el acceso.
                    if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
                        existente.setPassword(passwordEncoder.encode(datos.getPassword()));
                    }
                    existente.setTelefono(datos.getTelefono());
                    existente.setFechaNacimiento(datos.getFechaNacimiento());
                    existente.setNumeroExpediente(datos.getNumeroExpediente());
                    existente.setTipoSangre(datos.getTipoSangre());
                    existente.setDireccion(datos.getDireccion());
                    return ResponseEntity.ok(pacienteRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/pacientes/{id} - eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!pacienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pacienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}