/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.AddressDao;
import com.danya.javaee.dao.CityDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import com.danya.javaee.dao.DaoFactory;

/**
 *
 * @author danya
 */
public class RelationAddressCityTest {
    
    private static final DaoFactory<Connection> factory = new MariaDbDaoFactory();
    private Connection conn;
    private AddressDao addressDao;
    private CityDao cityDao;
    
    @Before
    public void setUp() throws DaoException, SQLException {
        conn = factory.getContext();
        conn.setAutoCommit(false);
        addressDao = (AddressDao) factory.getDao(conn, Address.class);
        cityDao = (CityDao) factory.getDao(conn, City.class);
    }
    
    @After
    public void tearDown() throws SQLException {
        conn.rollback();
        conn.close();
    }
    
    @Test
    public void testCreate() throws DaoException {
        Address address = addressDao.create();
        assertNull("City is not null.", address.getCity());
        
        City city = new City();
        address.setCity(city);
        assertNotNull("City is null.", address.getCity());
    }
    
    @Test
    public void testPersist() throws DaoException {
        Address address = new Address();
        City city = cityDao.create();
        address.setCity(city);
        city.setName("City#1");
        address = addressDao.persist(address);
        assertNotNull("address.city is null", address.getCity());
        assertEquals("address.city wrong name", "City#1", address.getCity().getName());
    }
    
    @Test
    public void testPersistRecursive() throws DaoException {
        Address address = new Address();
        City city = new City();
        address.setCity(city);
        address = addressDao.persist(address);
        assertNotNull("address.city is null", address.getCity());
        assertNotNull("address.city.id is null", address.getCity().getId());
    }
    
    @Test
    public void testUpdate() throws DaoException {
        Address address = addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        assertNotNull("address.city is null", address.getCity());
        assertNotNull("address.city.id is null", address.getCity().getId());
    }
    
    @Test
    public void testUpdateRecursive() throws DaoException {
        Address address = addressDao.create();
        City city = cityDao.create();
        city.setName("City#1");
        address.setCity(city);
        addressDao.update(address);
        assertNotNull("address.city is null", address.getCity());
        assertEquals("address.city wrong name", "City#1", address.getCity().getName());
    }
    
    @Test
    public void testRead() throws DaoException {
        Address address = addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        address = addressDao.getEntityById(address.getId());
        assertNotNull("Address is null", address);
        assertNotNull("address.city is null", address.getCity());        
    }
    
    @Test
    public void testDelete() throws DaoException {
        Address address = addressDao.create();
        address.setCity(new City());
        addressDao.update(address);
        City city = address.getCity();
        addressDao.delete(address);
        city = cityDao.getEntityById(city.getId());
        assertNotNull("City not found", city);
    }
}
