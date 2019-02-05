/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.DaoException;
import com.danya.javaee.dao.DaoTest;
import com.danya.javaee.dao.Identified;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.Parameterized;
import com.danya.javaee.dao.Dao;
import com.danya.javaee.dao.DaoFactory;

/**
 *
 * @author danya
 */
public class MariaDbDaoTest extends DaoTest<Connection> {
    
    private Connection conn;
    private Dao dao;
    
    private static final DaoFactory<Connection> factory = new MariaDbDaoFactory();
    
    @Parameterized.Parameters
    public static Collection getParameters() {
        return Arrays.asList(new Object[][]{
            {Address.class, new Address()},
            {City.class, new City()},
            {Route.class, new Route()}
        });
    }
    
    @Before
    public void setUp() throws DaoException, SQLException {
        conn = factory.getContext();
        conn.setAutoCommit(false);
        dao = factory.getDao(conn, daoClass);
    }
    
    @After
    public void tearDown() throws SQLException {
        context().rollback();
        context().close();
    }
    
    @Override
    public Dao dao() {
        return dao;
    }
    
    @Override
    public Connection context() {
        return conn;
    }
    
    public MariaDbDaoTest(Class clazz, Identified notPersistedDto) {
        super(clazz, notPersistedDto);
    }  
}
