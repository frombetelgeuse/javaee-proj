/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.CityDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.danya.javaee.dao.DaoFactory;
import com.danya.javaee.dao.RouteDao;

/**
 *
 * @author danya
 */
public class RelationRouteCityTest {
    private static final DaoFactory<Connection> factory = new MariaDbDaoFactory();
    private Connection conn;
    private RouteDao routeDao;
    private CityDao cityDao;
    
    @Before
    public void setUp() throws DaoException, SQLException {
        conn = factory.getContext();
        conn.setAutoCommit(false);
        routeDao = (RouteDao) factory.getDao(conn, Route.class);
        cityDao = (CityDao) factory.getDao(conn, City.class);
    }
    
    @After
    public void tearDown() throws SQLException {
        conn.rollback();
        conn.close();
    }
    
    @Test
    public void testCreate() throws DaoException {
        Route route = routeDao.create();
        assertTrue("route.cities is not empty", route.getCities().isEmpty());
        
        List<City> cities = new ArrayList();
        route.setCities(cities);
        assertNotNull("route.cities is null", route.getCities());
    }
    
    @Test
    public void testPersist() throws DaoException {
        Route route = new Route();
        List<City> cities = new ArrayList();
        cities.add(cityDao.create());
        route.setCities(cities);
        route = routeDao.persist(route);
        assertNotNull("route.cities is null", route.getCities());
        assertEquals("route.cities size is not 1", 1, route.getCities().size());
    }
    
    @Test
    public void testPersistRecursive() throws DaoException {
        Route route = new Route();
        List<City> cities = new ArrayList();
        cities.add(new City());
        route.setCities(cities);
        route = routeDao.persist(route);
        assertNotNull("route.cities is null", route.getCities());
        assertEquals("route.cities size is not 1", 1, route.getCities().size());
    }
    
    @Test
    public void testUpdate() throws DaoException {
        Route route = routeDao.create();
        route.getCities().add(new City());
        routeDao.update(route);
        assertNotNull("route.cities is null", route.getCities());
        assertNotNull("route.cities.get(0).id is null", route.getCities().get(0).getId());
    }
    
    @Test
    public void testUpdateRecursive() throws DaoException {
        Route route = routeDao.create();
        City city = cityDao.create();
        city.setName("City#1");
        route.getCities().add(city);
        routeDao.update(route);
        assertNotNull("route.cities is null", route.getCities());
        assertEquals("route.cities.get(0).name is wrong", "City#1", route.getCities().get(0).getName());
    }
    
    @Test
    public void testRead() throws DaoException {
        Route route = routeDao.create();
        route.getCities().add(new City());
        routeDao.update(route);
        route = routeDao.getEntityById(route.getId());
        assertNotNull("route is null", route);
        assertNotNull("route.cities is null", route.getCities());
        assertNotNull("route.cities.get(0) is null", route.getCities().get(0));
    }
    
    @Test
    public void testDelete() throws DaoException {
        Route route = routeDao.create();
        route.getCities().add(new City());
        routeDao.update(route);
        City city = route.getCities().get(0);
        routeDao.delete(route);
        city = cityDao.getEntityById(city.getId());
        assertNotNull("city not found", city);
    }
}
