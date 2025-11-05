package edu.upc.dsa;

import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Piloto;
import edu.upc.dsa.models.Reserva;

import java.util.List;

public interface DronesManager {

    public Dron addDron(String id, String name, String fabricante, String modelo, double horasVuelo);

    public Piloto addPiloto(String id, String name, String surname, double horasVuelo);

    public List<Dron> getDronesByHours();

    public List<Piloto> getPilotosByHours();

    public void guardarDronAlmacen(String idDron);

    public Dron mantenimientoDron();

    public void addReserva(String idDron, String fecha, String tiempoReserva, String posInicio, String posDestino, String idPiloto);

    public List<Reserva> ReservasPiloto(String idPiloto);

    public List<Reserva> ReservasDron(String idDron);

    public Dron getDron(String idDron);

    public Piloto getPiloto(String idPiloto);

    public List<Dron> getAllDrones();

    public List<Piloto> getAllPilotos();

    public List<Reserva> getAllReservas();

    public void clear();

    public int sizeDrones();

    public int sizePilotos();

    public int sizeReservas();

    public int sizeAlmacen();

}
