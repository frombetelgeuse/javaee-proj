/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.DAO;
import com.danya.javaee.dao.DAOException;
import com.danya.javaee.dao.DAOFactory;
import com.danya.javaee.dao.DAOTest;
import com.danya.javaee.dao.Identified;
import com.danya.javaee.dao.mariadb.MariaDbDAOFactory;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.runners.Parameterized;

/**
 *
 * @author danya
 */
public class MariaDbDAOTest extends DAOTest<Connection> {
    
    private Connection conn;
    private DAO dao;
    
    private static final DAOFactory<Connection> factory = new MariaDbDAOFactory();
    
    @Parameterized.Parameters
    public static Collection getParameters() {
        return Arrays.asList(new Object[][]{
            {Address.class, new Address()},
            {City.class, new City()}
        });
    }
    
    @Before
    public void setUp() throws DAOException, SQLException {
        conn = factory.getContext();
        conn.setAutoCommit(false);
        dao = factory.getDAO(conn, daoClass);
    }
    
    @After
    public void tearDown() throws SQLException {
        context().rollback();
        context().close();
    }
    
    @Override
    public DAO dao() {
        return dao;
    }
    
    @Override
    public Connection context() {
        return conn;
    }
    
    public MariaDbDAOTest(Class clazz, Identified notPersistedDto) {
        super(clazz, notPersistedDto);
    }  
}
