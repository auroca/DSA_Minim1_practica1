package edu.upc.dsa.models;

public class Reserva {

    String idDron;
    String fecha;
    String tiempoReserva;
    String posInicial;
    String posFinal;
    String idPiloto;

    public Reserva() {

    }

    public Reserva(String idDron, String fecha, String tiempoReserva,  String posInicial, String posFinal, String idPiloto) {
        this.idDron = idDron;
        this.fecha = fecha;
        this.tiempoReserva = tiempoReserva;
        this.posInicial = posInicial;
        this.posFinal = posFinal;
        this.idPiloto = idPiloto;
    }

    public String getIdDron() {
        return idDron;
    }

    public void setIdDron(String idDron) {
        this.idDron = idDron;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTiempoReserva() {
        return tiempoReserva;
    }

    public void setTiempoReserva(String tiempoReserva) {
        this.tiempoReserva = tiempoReserva;
    }

    public String getPosInicial() {
        return posInicial;
    }

    public void setPosInicial(String posInicial) {
        this.posInicial = posInicial;
    }

    public String getPosFinal() {
        return posFinal;
    }

    public void setPosFinal(String posFinal) {
        this.posFinal = posFinal;
    }

    public String getIdPiloto() {
        return idPiloto;
    }

    public void setIdPiloto(String idPiloto) {
        this.idPiloto = idPiloto;
    }
}

