package edu.upc.dsa;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Piloto;
import edu.upc.dsa.models.Reserva;

import java.util.*;
import org.apache.log4j.Logger;


public class DronesManagerImpl implements DronesManager {
    private static DronesManagerImpl instance;

    private Map<String, Dron> drones;
    private Map<String, Piloto> pilotos;
    private List<Reserva> reservas;

    private Stack<Dron> almacen;

    List<Dron> AllDrones;
    List<Piloto> AllPilotos;

    final static Logger logger = Logger.getLogger(DronesManagerImpl.class);

    private DronesManagerImpl() {
        this.drones = new HashMap<>();
        this.pilotos = new HashMap<>();
        this.AllDrones = new ArrayList<>();
        this.AllPilotos = new ArrayList<>();
        this.reservas = new ArrayList<>();
        this.almacen = new Stack<>();
    }

    public static DronesManager getInstance() {
        if (instance==null) instance = new DronesManagerImpl();
        return instance;
    }

    public Dron addDron(String id, String name, String fabricante, String modelo, double horasVuelo) {
        logger.info("addDron: id=" + id + " name=" + name + " fabricante=" + fabricante + " modelo=" + modelo);
        Dron dron = new Dron(id, name, fabricante, modelo, horasVuelo);
        this.drones.put(id, dron);
        logger.info("Dron añadido: " + dron);
        return drones.get(id);
    }

    public Piloto addPiloto(String id, String name, String surname, double horasVuelo) {
        logger.info("addPilot: id=" + id + " name=" + name + " surname=" + surname);
        Piloto piloto = new Piloto(id, name, surname, horasVuelo);
        this.pilotos.put(id, piloto);
        logger.info("Piloto añadido: " + piloto);
        return pilotos.get(id);
    }

    public List<Dron> getDronesByHours() {
        logger.info("getDronesByHours");
        List<Dron> list = new ArrayList<>(drones.values());//drones.values da todos los objetos dron que hay en el mapa
        list.sort(Comparator.comparingDouble(Dron::getHorasVuelo).reversed());
        logger.info("lista de drones ordenada en orden descendente obtenida");
        return list;
    }

    public List<Piloto> getPilotosByHours() {
        logger.info("getPilotosByHours");
        List<Piloto> list = new ArrayList<>(pilotos.values());//drones.values da todos los objetos dron que hay en el mapa
        list.sort(Comparator.comparingDouble(Piloto::getHorasVuelo).reversed());
        logger.info("lista de pilotos ordenada en orden descendente obtenida");
        return list;
    }

    public void guardarDronAlmacen(String idDron){
        logger.info("guardarDronAlmacen: idDron=" + idDron);
        try{
            if(!this.drones.containsKey(idDron)){
                logger.error("Dron no existe: " + idDron);
                throw new DronNotFoundException("Dron no existe: " + idDron);
            }
            if (almacen.contains(idDron)) {
                logger.error("Dron ya estaba en almacén");
                throw new DronDuplicateStorageException("El dron ya se encuentra en el almacén");
            }
            else{
                Dron d = this.drones.get(idDron);
                this.almacen.add(d);
                d.setEstado("Mantenimiento");
                logger.info("Dron almacenado");
            }
        }
        catch(DronNotFoundException|DronDuplicateStorageException e){
            logger.info("Dron no existe: " + idDron);
        }
    }

    public Dron mantenimientoDron(){

        logger.info("mantenimientoDron");
        try{
            Dron d = this.almacen.pop();
            if(d==null){
                logger.error("No hay drones en el almacen");
                throw new NoDronesInStorageException("No hay drones almacenados");
            }
            else{
                d.setEstado("Disponible");
                logger.info("mantenimiento realizado, el dron "+ d.getId()+"está disponible");
                return d;
            }
        }
        catch(NoDronesInStorageException e){
            logger.info("No hay drones en el almacen");
            return null;
        }
    }

    public void addReserva(String idDron, String fecha, String tiempoReserva, String posInicio, String posDestino, String idPiloto){
        logger.info("Añadir reserva");
        Reserva r = new Reserva(idDron, fecha, tiempoReserva, posInicio, posDestino, idPiloto);
        try {
            for (Reserva res : reservas) {
                if (res.getIdDron().equals(idDron)) {
                    if (res.getFecha().equals(fecha) && res.getTiempoReserva().equals(tiempoReserva)) {
                        throw new HourConflictInReservationException("El dron o piloto ya tienen un vuelo asignado en ese horario");
                    }
                }
                if (res.getIdPiloto().equals(idPiloto)) {
                    if (res.getFecha().equals(fecha) && res.getTiempoReserva().equals(tiempoReserva)) {
                        throw new HourConflictInReservationException("El dron o piloto ya tienen un vuelo asignado en ese horario");
                    }
                }
            }
            if (this.almacen.contains(idDron)) {
                logger.error("El dron se encuentra en mantenimiento");
                throw new DronInMaintenanceException("Dron en mantenimiento");
            }
            else{
                this.reservas.add(r);
            }
        }
        catch(DronInMaintenanceException|HourConflictInReservationException e){
            logger.info("El dron o piloto ya tienen un vuelo asignado en ese horario o el dron está en mantenimiento");
        }
    }

    public List<Reserva> ReservasPiloto(String idPiloto){
        logger.info("Listar las reservas del piloto: " + idPiloto);
        List<Reserva> r = new ArrayList<>();
        for (Reserva res : this.reservas) {
            if (res.getIdPiloto().equals(idPiloto)) {
                r.add(res);
            }
        }
        logger.info("Lista enviada");
        return r;
    }

    public List<Reserva> ReservasDron(String idDron){
        logger.info("Listar las reservas del dron: " + idDron);
        List<Reserva> r = new ArrayList<>();
        for (Reserva res : this.reservas) {
            if (res.getIdDron().equals(idDron)) {
                r.add(res);
            }
        }
        logger.info("Lista enviada");
        return r;
    }

    public Dron getDron(String idDron){
        logger.info("getDron: " + idDron);
        return this.drones.get(idDron);
    }

    public Piloto getPiloto(String idPiloto){
        logger.info("getPiloto: " + idPiloto);
        return this.pilotos.get(idPiloto);
    }

    public List<Dron> getAllDrones(){
        logger.info("getAllDrones");
        return new ArrayList<>(this.drones.values());
    }

    public List<Piloto> getAllPilotos(){
        logger.info("getAllPilotos");
        return new ArrayList<>(this.pilotos.values());
    }

    public List<Reserva> getAllReservas(){
        logger.info("getAllReservas");
        return new ArrayList<>(this.reservas);
    }

    public void clear() {
        drones.clear();
        pilotos.clear();
        almacen.clear();
        reservas.clear();
        logger.info("Sistema gestion cleared");
    }

    public int sizeDrones() {
        return drones.size();
    }

    public int sizePilotos() {
        return pilotos.size();
    }

    public int sizeReservas() {
        return reservas.size();
    }

    public int sizeAlmacen(){
        return almacen.size();
    }
}