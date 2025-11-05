package edu.upc.dsa.models;

public class Dron {
    String id;
    String name;
    String fabricante;
    String modelo;
    double horasVuelo;
    String estado;

    public Dron() {
    }

    public Dron(String id, String name, String fabricante, String modelo, double horasVuelo) {
        this. id = id;
        this.name = name;
        this.fabricante = fabricante;
        this.modelo = modelo;
        this.horasVuelo = horasVuelo;
        this.estado = "Disponible";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getHorasVuelo() {
        return horasVuelo;
    }

    public void setHorasVuelo(double horasVuelo) {
        this.horasVuelo = horasVuelo;
    }

    public void actualizarHorasVuelo(double horasVuelo) {
        this.horasVuelo = horasVuelo + this.horasVuelo;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Avion [id=" + id + ", name=" + name + ", fabricante=" + fabricante + ", modelo=" + modelo + "]";
    }
}