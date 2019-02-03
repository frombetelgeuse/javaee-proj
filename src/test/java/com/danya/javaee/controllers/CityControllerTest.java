/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.controllers;

import com.danya.javaee.App;
import com.danya.javaee.instances.City;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author danya
 */
public class CityControllerTest {
    
    private Connection conn;
    private ControllerFactory factory;
    
    private CityController cc;
    
    public CityControllerTest() {
        this.factory = new MariaDBFactory();
        try {
            conn = factory.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        cc = factory.getCityController(conn);
    }
    
    @After
    public void tearDown() {
//        try {
//            conn.rollback();
//        } catch (SQLException e) {
//            System.out.println("Some shit in CityControllerTest::tearDown");
//            e.printStackTrace();
//        }
    }

    /**
     * Test of getAll method, of class CityController.
     */
    @Test
    public void testGetAll() throws Exception {
        List<City> cities = cc.getAll();
        assertNotNull("Check if list is null", cities);
        assertEquals("City#1", cities.get(0).getName());
    }

    /**
     * Test of getEntityById method, of class CityController.
     */
    @Test
    public void testGetEntityById() throws Exception {
        City city = cc.getEntityById(2);
        assertNotNull(city);
        assertEquals("City#2", city.getName());
    }

    /**
     * Test of update method, of class CityController.
     */
    @Test
    public void testUpdate() throws Exception {
        String newName = "City#4-2";
        String oldName;
        City city = cc.getEntityById(4);
        oldName = city.getName();
        city.setName(newName);
        cc.update(city);
        City city2 = cc.getEntityById(4);
        assertEquals(newName, city2.getName());
        city2.setName(oldName);
        cc.update(city2);
    }

    /**
     * Test of delete method, of class CityController.
     */
    @Test
    public void testDelete() throws Exception {
//        cc.delete(7);
//        assertNull(city);
    }

    /**
     * Test of create method, of class CityController.
     */
    @Test
    public void testCreate() throws Exception {
        City city = new City();
        city.setName("City#Infinity");
        City city2 = cc.create(city);
        assertEquals(city.getName(), city2.getName());
    }
    
}
