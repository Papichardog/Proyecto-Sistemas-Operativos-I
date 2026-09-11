package com.umg.citasmedicas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicos")
@PrimaryKeyJoinColumn(name = "id_medico")
public class Medico extends Persona {

    @Column(name = "numero_colegiado", nullable = false, unique = true, length = 30)
    private String numeroColegiado;

    @Column(name = "anios_experiencia", nullable = false)
    private int aniosExperiencia;

    // Asociación unidireccional: Medico conoce su Especialidad,
    // Especialidad no mantiene una lista de médicos.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    // Asociación bidireccional: el Medico conoce sus Citas en el modelo
    // Java, pero la ocultamos por completo del JSON (@JsonIgnore) por el
    // mismo motivo que en Paciente: evitar el ciclo largo Medico->citas->
    // paciente->citas->medico... Las citas de un médico ahora se
    // consultan por su propio endpoint: GET /api/citas/medico/{id}.
    @OneToMany(mappedBy = "medico")
    @JsonIgnore
    private List<Cita> citas = new ArrayList<>();

    public Medico() {
        // Constructor vacío requerido por JPA
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public int getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(int aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
}