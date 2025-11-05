package edu.upc.dsa.models;

public class Piloto {

    String id;
    String name;
    String surname;
    double horasVuelo;

    public Piloto() {

    }

    public Piloto(String id, String name, String surname, double horasVuelo) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.horasVuelo = horasVuelo;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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
}
