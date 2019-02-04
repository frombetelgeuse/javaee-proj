/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

import com.danya.javaee.dao.mariadb.MariaDbDAOFactory;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author danya
 */
public class RelationAddressCityTest {
    
    private static final DAOFactory<Connection> factory = new MariaDbDAOFactory();
    private Connection conn;
    private DAO addressDao;
    private DAO cityDao;
    
    @Before
    public void setUp() throws DAOException, SQLException {
        conn = factory.getContext();
        conn.setAutoCommit(false);
        addressDao = factory.getDAO(conn, Address.class);
        cityDao = factory.getDAO(conn, City.class);
    }
    
    @After
    public void tearDown() throws SQLException {
        conn.rollback();
        conn.close();
    }
    
    @Test
    public void testCreate() throws DAOException {
        Address address = (Address) addressDao.create();
        assertNull("City is not null.", address.getCity());
        
        City city = new City();
        address.setCity(city);
        assertNotNull("City is null.", address.getCity());
    }
    
    @Test
    public void testPersist() throws DAOException {
        Address address = new Address();
        City city = (City) cityDao.create();
        address.setCity(city);
        city.setName("City#1");
        address = (Address) addressDao.persist(address);
        assertNotNull("address.city is null", address.getCity());
        assertEquals("address.city wrong name", "City#1", address.getCity().getName());
    }
    
    @Test
    public void testPersistRecursive() throws DAOException {
        Address address = new Address();
        City city = new City();
        address.setCity(city);
        address = (Address) addressDao.persist(address);
        assertNotNull("address.city is null", address.getCity());
        assertNotNull("address.city.id is null", address.getCity().getId());
    }
    
    @Test
    public void testUpdate() throws DAOException {
        Address address = (Address) addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        assertNotNull("address.city is null", address.getCity());
        assertNotNull("address.city.id is null", address.getCity().getId());
    }
    
    @Test
    public void testUpdateRecursive() throws DAOException {
        Address address = (Address) addressDao.create();
        City city = (City) cityDao.create();
        city.setName("City#1");
        address.setCity(city);
        addressDao.update(address);
        assertNotNull("address.city is null", address.getCity());
        assertEquals("address.city wrong name", "City#1", address.getCity().getName());
    }
    
    @Test
    public void testRead() throws DAOException {
        Address address = (Address) addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        address = (Address) addressDao.getEntityById(address.getId());
        assertNotNull("Address is null", address);
        assertNotNull("address.city is null", address.getCity());        
    }
    
    @Test
    public void testDelete() throws DAOException {
        Address address = (Address) addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        City city = address.getCity();
        addressDao.delete(address);
        city = (City) cityDao.getEntityById(city.getId());
        assertNotNull("City not found", city);
    }
}
