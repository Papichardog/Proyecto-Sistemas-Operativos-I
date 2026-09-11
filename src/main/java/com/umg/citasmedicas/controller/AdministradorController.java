package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Administrador;
import com.umg.citasmedicas.repository.AdministradorRepository;
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
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorController(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> obtenerPorId(@PathVariable Integer id) {
        return administradorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Administrador> crear(@RequestBody Administrador administrador) {
        administrador.setPassword(passwordEncoder.encode(administrador.getPassword()));
        Administrador guardado = administradorRepository.save(administrador);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> actualizar(@PathVariable Integer id, @RequestBody Administrador datos) {
        return administradorRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setApellido(datos.getApellido());
                    existente.setCorreo(datos.getCorreo());
                    if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
                        existente.setPassword(passwordEncoder.encode(datos.getPassword()));
                    }
                    existente.setTelefono(datos.getTelefono());
                    existente.setFechaNacimiento(datos.getFechaNacimiento());
                    existente.setNivelAcceso(datos.getNivelAcceso());
                    return ResponseEntity.ok(administradorRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!administradorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        administradorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
