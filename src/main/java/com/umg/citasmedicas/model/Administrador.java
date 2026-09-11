package com.umg.citasmedicas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "id_administrador")
public class Administrador extends Persona {

    @Column(name = "nivel_acceso", nullable = false)
    private int nivelAcceso;

    public Administrador() {
        // Constructor vacío requerido por JPA
    }

    public int getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(int nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }
}
