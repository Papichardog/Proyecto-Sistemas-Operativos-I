package com.umg.citasmedicas.repository;

import com.umg.citasmedicas.model.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Integer> {
    // Método derivado: Spring Data lo implementa solo a partir del nombre.
    // Navega HistorialMedico -> paciente -> idPersona (heredado de Persona).
    Optional<HistorialMedico> findByPacienteIdPersona(Integer idPersona);
}