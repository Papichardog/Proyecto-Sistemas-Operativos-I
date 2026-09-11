package com.umg.citasmedicas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacientes")
@PrimaryKeyJoinColumn(name = "id_paciente")
public class Paciente extends Persona {

    @Column(name = "numero_expediente", nullable = false, unique = true, length = 30)
    private String numeroExpediente;

    @Column(name = "tipo_sangre", length = 5)
    private String tipoSangre;

    @Column(name = "direccion", length = 255)
    private String direccion;

    // Asociación bidireccional: el Paciente conoce sus Citas en el
    // modelo Java, pero la ocultamos por completo del JSON (@JsonIgnore).
    // Motivo: Cita también carga a Medico completo, y Medico también
    // tiene su propia lista de citas — un @JsonIgnoreProperties de un
    // solo salto no alcanza a cortar ese camino más largo. Las citas de
    // un paciente ahora se consultan por su propio endpoint dedicado:
    // GET /api/citas/paciente/{id}.
    @OneToMany(mappedBy = "paciente")
    @JsonIgnore
    private List<Cita> citas = new ArrayList<>();

    public Paciente() {
        // Constructor vacío requerido por JPA
    }

    public String getNumeroExpediente() {
        return numeroExpediente;
    }

    public void setNumeroExpediente(String numeroExpediente) {
        this.numeroExpediente = numeroExpediente;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Cita> getCitas() {
        return citas;
    }

    public void setCitas(List<Cita> citas) {
        this.citas = citas;
    }
}