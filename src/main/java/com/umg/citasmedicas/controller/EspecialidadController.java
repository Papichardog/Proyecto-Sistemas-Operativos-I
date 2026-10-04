package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.Especialidad;
import com.umg.citasmedicas.repository.EspecialidadRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@RequestMapping("/api/especialidades")
public class EspecialidadController {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadController(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    // @Cacheable: la primera vez que alguien pide la lista, se guarda en
    // Redis bajo la clave "especialidades". Las siguientes peticiones
    // (de app1 o app2, cualquiera) se sirven directo desde Redis sin
    // tocar Postgres — tiene sentido acá porque las especialidades
    // cambian poco y se consultan seguido (cada vez que alguien abre el
    // formulario de agendar cita o registrar médico).
    @Cacheable("especialidades")
    @GetMapping
    public List<Especialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Especialidad> obtenerPorId(@PathVariable Integer id) {
        return especialidadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @CacheEvict: cualquier cambio invalida la caché completa, para que
    // la próxima lectura traiga datos frescos de Postgres y los vuelva
    // a guardar en Redis.
    @CacheEvict(value = "especialidades", allEntries = true)
    @PostMapping
    public ResponseEntity<Especialidad> crear(@RequestBody Especialidad especialidad) {
        Especialidad guardada = especialidadRepository.save(especialidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @CacheEvict(value = "especialidades", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<Especialidad> actualizar(@PathVariable Integer id, @RequestBody Especialidad datos) {
        return especialidadRepository.findById(id)
                .map(existente -> {
                    existente.setNombreEspecialidad(datos.getNombreEspecialidad());
                    existente.setDescripcion(datos.getDescripcion());
                    return ResponseEntity.ok(especialidadRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @CacheEvict(value = "especialidades", allEntries = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!especialidadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        especialidadRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
