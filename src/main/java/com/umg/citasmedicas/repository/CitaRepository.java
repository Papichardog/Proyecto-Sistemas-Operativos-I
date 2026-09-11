package com.umg.citasmedicas.repository;

import com.umg.citasmedicas.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    // Reemplazan la necesidad de exponer Paciente.citas / Medico.citas
    // en el JSON: cada uno trae la lista de citas ya con su medico,
    // paciente y consultorio completos, sin ciclos.
    List<Cita> findByPacienteIdPersona(Integer idPersona);

    List<Cita> findByMedicoIdPersona(Integer idPersona);
}
