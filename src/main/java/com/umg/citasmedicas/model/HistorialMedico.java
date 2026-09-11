package com.umg.citasmedicas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historiales_medicos")
public class HistorialMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer idHistorial;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion = LocalDate.now();

    @Column(name = "observaciones_generales")
    private String observacionesGenerales;

    // Asociación unidireccional: el HistorialMedico conoce a su Paciente.
    // Paciente no guarda referencia de vuelta, para no repetir el mismo
    // patrón de ciclo que ya resolvimos en Cita/Consultorio.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false, unique = true)
    private Paciente paciente;

    // Composición: el HistorialMedico es dueño de sus EntradaHistorial.
    // cascade=ALL + orphanRemoval=true: si se borra el historial, se
    // borran sus entradas; si una entrada se quita de la lista, se borra
    // de la base también (a diferencia de la agregación Clinica-Consultorio).
    @OneToMany(mappedBy = "historial", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("historial")
    private List<EntradaHistorial> entradas = new ArrayList<>();

    public HistorialMedico() {
        // Constructor vacío requerido por JPA
    }

    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public List<EntradaHistorial> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaHistorial> entradas) {
        this.entradas = entradas;
    }
}
