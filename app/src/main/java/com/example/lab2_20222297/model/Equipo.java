package com.example.lab2_20222297.model;

import java.io.Serializable;

public class Equipo implements Serializable {
    private String codigo;
    private String nombre;
    private String tipo;
    private String estado;
    private String observaciones;

    public Equipo(String codigo, String nombre, String tipo, String estado, String observaciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
