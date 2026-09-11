package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.model.EntradaHistorial;
import com.umg.citasmedicas.model.HistorialMedico;
import com.umg.citasmedicas.repository.EntradaHistorialRepository;
import com.umg.citasmedicas.repository.HistorialMedicoRepository;
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
@RequestMapping("/api/entradas-historial")
public class EntradaHistorialController {

    private final EntradaHistorialRepository entradaHistorialRepository;
    private final HistorialMedicoRepository historialMedicoRepository;

    public EntradaHistorialController(EntradaHistorialRepository entradaHistorialRepository,
                                      HistorialMedicoRepository historialMedicoRepository) {
        this.entradaHistorialRepository = entradaHistorialRepository;
        this.historialMedicoRepository = historialMedicoRepository;
    }

    @GetMapping
    public List<EntradaHistorial> listarTodas() {
        return entradaHistorialRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntradaHistorial> obtenerPorId(@PathVariable Integer id) {
        return entradaHistorialRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/historial/{idHistorial}")
    public List<EntradaHistorial> listarPorHistorial(@PathVariable Integer idHistorial) {
        return entradaHistorialRepository.findByHistorialIdHistorial(idHistorial);
    }

    @PostMapping
    public ResponseEntity<EntradaHistorial> crear(@RequestBody EntradaHistorial entrada) {
        HistorialMedico historial = historialMedicoRepository.findById(entrada.getHistorial().getIdHistorial())
                .orElseThrow(() -> new IllegalArgumentException("Historial medico no encontrado"));
        entrada.setHistorial(historial);

        EntradaHistorial guardada = entradaHistorialRepository.save(entrada);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntradaHistorial> actualizar(@PathVariable Integer id, @RequestBody EntradaHistorial datos) {
        return entradaHistorialRepository.findById(id)
                .map(existente -> {
                    existente.setFecha(datos.getFecha());
                    existente.setDiagnostico(datos.getDiagnostico());
                    existente.setTratamiento(datos.getTratamiento());
                    return ResponseEntity.ok(entradaHistorialRepository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!entradaHistorialRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        entradaHistorialRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
