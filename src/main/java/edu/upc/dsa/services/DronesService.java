package edu.upc.dsa.services;


import edu.upc.dsa.DronesManager;
import edu.upc.dsa.DronesManagerImpl;
import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Piloto;
import edu.upc.dsa.models.Reserva;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Api(value = "/drones", description = "Servicio de gestión de drones, pilotos y reservas")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DronesService {

    private DronesManager manager;

    public DronesService() {
        this.manager = DronesManagerImpl.getInstance();
        if (manager.sizeDrones() == 0) {

            this.manager.addDron("D1", "Alpha", "DJI", "Mavic", 4);
            this.manager.addDron("D2", "Bravo", "Parrot", "Anafi", 7);

            this.manager.addPiloto("P1", "lola", "martin", 11);
            this.manager.addPiloto("P2", "leo", "vidal", 5);

            this.manager.addReserva("D1", "11.11.12:00", "5", "30.2, 2.0","10.0,0.0", "P1");
        }
    }

    @POST
    @ApiOperation(value = "Crear un dron", notes = "200 si se crea")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Dron creado", response = Dron.class),
            @ApiResponse(code = 500, message = "Validation Error"),
    })
    @Path("/drones")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDron(Dron dron) {
        if (dron == null || dron.getId() == null || dron.getName() == null
        || dron.getFabricante() == null || dron.getModelo() == null) {
            return Response.status(500).entity(dron).build();
        }
        this.manager.addDron(dron.getId(), dron.getName(), dron.getFabricante(), dron.getModelo(), dron.getHorasVuelo());
        return Response.status(201).entity(dron).build();
    }

    @POST
    @ApiOperation(value = "Crear un piloto", notes = "200 si se crea")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Piloto creado", response = Piloto.class),
            @ApiResponse(code = 500, message = "Validation Error"),
    })
    @Path("/pilotos")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPiloto(Piloto piloto) {
        if (piloto == null || piloto.getId() == null || piloto.getName() == null || piloto.getSurname() == null) {
            return Response.status(500).entity(piloto).build();
        }
        this.manager.addPiloto(piloto.getId(), piloto.getName(), piloto.getSurname(), piloto.getHorasVuelo());
        return Response.status(201).entity(piloto).build();
    }

    @GET
    @ApiOperation(value = "get all drones by hours", notes = "Retorna una llista dels drons ordenats per hores")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Dron.class, responseContainer = "List")
    })
    @Path("/drones/hours")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDronesByHours() {
        List<Dron> drones = this.manager.getDronesByHours();
        GenericEntity<List<Dron>> entity = new GenericEntity<List<Dron>>(drones) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "get all pilots by hours", notes = "Retorna una llista dels pilots ordenats per hores")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Piloto.class, responseContainer = "List")
    })
    @Path("/pilotos/hours")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilotosByHours() {
        List<Piloto> pilotos = this.manager.getPilotosByHours();
        GenericEntity<List<Piloto>> entity = new GenericEntity<List<Piloto>>(pilotos) {};
        return Response.status(200).entity(entity).build();
    }

    @POST
    @ApiOperation(value = "Guardar dron en almacén (mantenimiento)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dron almacenado o ya estaba"),
            @ApiResponse(code = 404, message = "Dron no existe"),
            @ApiResponse(code = 400, message = "Solicitud inválida")
    })
    @Path("/almacen/{idDron}")
    public Response guardarEnAlmacen(@PathParam("idDron") String idDron) {
        if (idDron == null || idDron.isEmpty()) {
            return Response.status(400).entity("idDron inválido").build();
        }
        if (this.manager.getDron(idDron) == null) {
            return Response.status(404).entity("Dron no existe: " + idDron).build();
        }
        this.manager.guardarDronAlmacen(idDron);
        return Response.ok().build();
    }

    @POST
    @ApiOperation(value = "Sacar de almacén el dron más cercano a la puerta")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Dron reparado", response = Dron.class),
            @ApiResponse(code = 404, message = "No hay drones en almacén")
    })
    @Path("/almacen/mantenimiento")
    public Response mantenimientoDron() {
        Dron d = this.manager.mantenimientoDron();
        if (this.manager.sizeAlmacen() == 0) {
            return Response.status(404).entity("No hay drones en almacén").build();
        }
        else{
            return Response.status(200).entity(d).build();
        }
    }

    @POST
    @ApiOperation(value = "Crear una reserva", notes = "201 si se crea; 400 si hay conflicto o dron en mantenimiento; 404 si no existe dron/piloto")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Reserva creada", response = Reserva.class),
            @ApiResponse(code = 400, message = "Conflicto/Mantenimiento o datos inválidos"),
            @ApiResponse(code = 404, message = "Dron/Piloto no existe")
    })
    @Path("/reservas")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReserva(Reserva reserva) {
        if (reserva == null || reserva.getFecha() == null || reserva.getTiempoReserva() == null
        || reserva.getIdDron() == null || reserva.getIdPiloto() == null || reserva.getPosFinal() == null
        || reserva.getPosInicial() == null) {
            return Response.status(400).entity("Datos de reserva inválidos").build();
        }
        if (this.manager.getDron(reserva.getIdDron()) == null)
            return Response.status(404).entity("Dron no existe: " + reserva.getIdDron()).build();
        if (this.manager.getPiloto(reserva.getIdPiloto()) == null)
            return Response.status(404).entity("Piloto no existe: " + reserva.getIdPiloto()).build();

        int antes = this.manager.sizeReservas();
        this.manager.addReserva(
                reserva.getIdDron(), reserva.getFecha(), reserva.getTiempoReserva(), reserva.getPosInicial(), reserva.getPosFinal(), reserva.getIdPiloto());
        int despues = this.manager.sizeReservas();

        if (despues > antes)
            return Response.status(201).entity(reserva).build(); // creada
        else return Response.status(400).entity("Reserva rechazada (conflicto horario o dron en mantenimiento)").build();
    }

    @GET
    @ApiOperation(value = "get all reservations made by a pilot", notes = "Retorna una llista de les reserves")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Piloto.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Piloto no ha hecho reservas"),
    })
    @Path("/reservas/piloto/{idPiloto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reservasPorPiloto(@PathParam("idPiloto") String idPiloto) {
        List<Reserva> reservas = this.manager.ReservasPiloto(idPiloto);
        if (reservas == null || reservas.isEmpty())
        {
            return Response.status(404).entity("Este piloto no ha hecho reservas:").build();
        }
        else{
            GenericEntity<List<Reserva>> entity = new GenericEntity<List<Reserva>>(reservas) {};
            return Response.status(200).entity(entity).build();
        }
    }

    @GET
    @ApiOperation(value = "get all reservations made by a drone", notes = "Retorna una llista de les reserves")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Dron.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Dron no ha hecho reservas"),
    })
    @Path("/reservas/dron/{idDron}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reservasPorDron(@PathParam("idDron") String idDron) {
        List<Reserva> reservas = this.manager.ReservasDron(idDron);
        if (reservas == null || reservas.isEmpty())
        {
            return Response.status(404).entity("Este dron no ha hecho reservas:").build();
        }
        else{
            GenericEntity<List<Reserva>> entity = new GenericEntity<List<Reserva>>(reservas) {};
            return Response.status(200).entity(entity).build();
        }
    }

    @GET
    @ApiOperation(value = "get all Drones", notes = "Retorna una lista de todos los drones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Dron.class, responseContainer = "List")
    })
    @Path("/drones")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDrones() {
        List<Dron> drones = this.manager.getAllDrones();
        GenericEntity<List<Dron>> entity = new GenericEntity<List<Dron>>(drones) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "get all Pilotos", notes = "Retorna una lista de todos los pilotos")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Piloto.class, responseContainer = "List")
    })
    @Path("/pilotos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPilots() {
        List<Piloto> pilotos = this.manager.getAllPilotos();
        GenericEntity<List<Piloto>> entity = new GenericEntity<List<Piloto>>(pilotos) {};
        return Response.status(200).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "get all Reservas", notes = "Retorna una lista de todos las reservas")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful", response = Reserva.class, responseContainer = "List")
    })
    @Path("/reservas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReservas() {
        List<Reserva> reservas = this.manager.getAllReservas();
        GenericEntity<List<Reserva>> entity = new GenericEntity<List<Reserva>>(reservas) {};
        return Response.status(200).entity(entity).build();
    }

}