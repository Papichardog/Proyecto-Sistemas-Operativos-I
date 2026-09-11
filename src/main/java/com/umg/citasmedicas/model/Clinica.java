package com.umg.citasmedicas.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clinicas")
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clinica")
    private Integer idClinica;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "direccion", nullable = false, length = 255)
    private String direccion;

    // Agregación: la Clinica contiene Consultorios, pero no los "posee" en
    // exclusiva (por eso no hay cascade = REMOVE: si se borra la clinica,
    // los consultorios no deberían borrarse solos).
    // @JsonIgnoreProperties corta el ciclo: al serializar esta lista,
    // cada Consultorio omite su propio campo "clinica".
    @OneToMany(mappedBy = "clinica", cascade = CascadeType.PERSIST)
    @JsonIgnoreProperties("clinica")
    private List<Consultorio> consultorios = new ArrayList<>();

    public Clinica() {
        // Constructor vacío requerido por JPA
    }

    public Integer getIdClinica() {
        return idClinica;
    }

    public void setIdClinica(Integer idClinica) {
        this.idClinica = idClinica;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<Consultorio> getConsultorios() {
        return consultorios;
    }

    public void setConsultorios(List<Consultorio> consultorios) {
        this.consultorios = consultorios;
    }
}