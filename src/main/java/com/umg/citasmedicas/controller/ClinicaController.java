package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Clinica;
import com.umg.citasmedicas.repository.ClinicaRepository;
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
@RequestMapping("/api/clinicas")
public class ClinicaController {

    private final ClinicaRepository clinicaRepository;

    public ClinicaController(ClinicaRepository clinicaRepository) {
        this.clinicaRepository = clinicaRepository;
    }

    @GetMapping
    public List<Clinica> listarTodas() {
        return clinicaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clinica> obtenerPorId(@PathVariable Integer id) {
        return clinicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Clinica> crear(@RequestBody Clinica clinica) {
        Clinica guardada = clinicaRepository.save(clinica);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clinica> actualizar(@PathVariable Integer id, @RequestBody Clinica datos) {
        return clinicaRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(datos.getNombre());
                    existente.setDireccion(datos.getDireccion());
                    return ResponseEntity.ok(clinicaRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!clinicaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clinicaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
