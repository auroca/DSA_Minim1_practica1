package edu.upc.dsa;

import edu.upc.dsa.models.Dron;
import edu.upc.dsa.models.Piloto;
import edu.upc.dsa.models.Reserva;
import org.junit.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

public class DronesManagerTest {

    private DronesManager manager;

    @Before
    public void setUp() throws Exception {
        manager = DronesManagerImpl.getInstance();

        // --- Inicializa el campo privado 'almacen' (Stack<Dron>) por reflexión ---
        Field almacenField = DronesManagerImpl.class.getDeclaredField("almacen");
        almacenField.setAccessible(true);
        Object current = almacenField.get(manager);
        if (current == null) {
            almacenField.set(manager, new Stack<Dron>());
        } else {
            ((Stack<Dron>) current).clear();
        }

        // Limpia todas las estructuras internas
        manager.clear();
    }

    @After
    public void tearDown() {
        manager.clear();
    }

    // ---------------------------------------------------------
    //           AVIONES y PILOTOS (altas y consulta)
    // ---------------------------------------------------------

    @Test
    public void addDron_y_addPiloto_inserta_con_horas_y_estado() {
        Dron d1 = ((DronesManagerImpl) manager).addDron("D1", "Alpha", "Maker", "M1", 12.5);
        Assert.assertNotNull(d1);
        Assert.assertEquals("D1", d1.getId());
        Assert.assertEquals(12.5, d1.getHorasVuelo(), 0.0001);
        Assert.assertEquals("Disponible", d1.getEstado());

        Piloto p1 = ((DronesManagerImpl) manager).addPiloto("P1", "Ada", "Lovelace", 300.0);
        Assert.assertNotNull(p1);
        Assert.assertEquals("P1", p1.getId());
        Assert.assertEquals(300.0, p1.getHorasVuelo(), 0.0001);

        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizeDrones());
        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizePilotos());
    }

    @Test
    public void getDronesByHours_devuelve_orden_descendente() {
        ((DronesManagerImpl) manager).addDron("D1", "Alpha",   "Maker", "M1", 10.0);
        ((DronesManagerImpl) manager).addDron("D2", "Bravo",   "Maker", "M2", 50.0);
        ((DronesManagerImpl) manager).addDron("D3", "Charlie", "Maker", "M3", 30.0);

        List<Dron> orden = ((DronesManagerImpl) manager).getDronesByHours();
        Assert.assertEquals(3, orden.size());
        Assert.assertEquals("D2", orden.get(0).getId()); // 50
        Assert.assertEquals("D3", orden.get(1).getId()); // 30
        Assert.assertEquals("D1", orden.get(2).getId()); // 10
    }

    @Test
    public void getPilotosByHours_devuelve_orden_descendente() {
        ((DronesManagerImpl) manager).addPiloto("P1", "Ada",   "Lovelace", 120.0);
        ((DronesManagerImpl) manager).addPiloto("P2", "Grace", "Hopper",   300.0);
        ((DronesManagerImpl) manager).addPiloto("P3", "Alan",  "Turing",   200.0);

        List<Piloto> orden = ((DronesManagerImpl) manager).getPilotosByHours();
        Assert.assertEquals(3, orden.size());
        Assert.assertEquals("P2", orden.get(0).getId()); // 300
        Assert.assertEquals("P3", orden.get(1).getId()); // 200
        Assert.assertEquals("P1", orden.get(2).getId()); // 120
    }

    // ---------------------------------------------------------
    //                ALMACÉN (pila de mantenimiento)
    // ---------------------------------------------------------

    @Test
    public void guardar_en_almacen_y_mantenimiento_funcionan() {
        ((DronesManagerImpl) manager).addDron("D1", "Alpha", "Maker", "M1", 5.0);

        ((DronesManagerImpl) manager).guardarDronAlmacen("D1");
        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizeAlmacen());

        Dron almacenado = ((DronesManagerImpl) manager).getDron("D1");
        Assert.assertEquals("Mantenimiento", almacenado.getEstado());

        Dron reparado = ((DronesManagerImpl) manager).mantenimientoDron();
        Assert.assertNotNull(reparado);
        Assert.assertEquals("D1", reparado.getId());
        Assert.assertEquals("Disponible", reparado.getEstado());
        Assert.assertEquals(0, ((DronesManagerImpl) manager).sizeAlmacen());
    }

    // ---------------------------------------------------------
    //                      RESERVAS
    // ---------------------------------------------------------

    @Test
    public void addReserva_crea_y_conflictos_impiden_duplicado() {
        ((DronesManagerImpl) manager).addDron("D1", "Alpha", "Maker", "M1", 15.0);
        ((DronesManagerImpl) manager).addDron("D2", "Bravo", "Maker", "M2", 12.0);
        ((DronesManagerImpl) manager).addPiloto("P1", "Ada", "Lovelace", 300.0);
        ((DronesManagerImpl) manager).addPiloto("P2", "Grace", "Hopper",  50.0);

        // Reserva válida
        ((DronesManagerImpl) manager).addReserva("D1", "2025-11-05", "10:00-11:00",
                "41.0,2.0", "41.5,2.2", "P1");
        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizeReservas());

        // Conflicto por mismo dron misma franja
        ((DronesManagerImpl) manager).addReserva("D1", "2025-11-05", "10:00-11:00",
                "41.1,2.1", "41.6,2.3", "P2");
        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizeReservas());

        // Conflicto por mismo piloto misma franja (otro dron)
        ((DronesManagerImpl) manager).addReserva("D2", "2025-11-05", "10:00-11:00",
                "41.2,2.2", "41.7,2.4", "P1");
        Assert.assertEquals(1, ((DronesManagerImpl) manager).sizeReservas());

        // Reserva válida en otra franja
        ((DronesManagerImpl) manager).addReserva("D1", "2025-11-05", "11:00-12:00",
                "41.0,2.0", "41.5,2.2", "P1");
        Assert.assertEquals(2, ((DronesManagerImpl) manager).sizeReservas());

        // Comprobaciones de consulta
        List<Reserva> rp = ((DronesManagerImpl) manager).ReservasPiloto("P1");
        Assert.assertEquals(2, rp.size());

        List<Reserva> rd = ((DronesManagerImpl) manager).ReservasDron("D1");
        Assert.assertEquals(2, rd.size());
    }
}