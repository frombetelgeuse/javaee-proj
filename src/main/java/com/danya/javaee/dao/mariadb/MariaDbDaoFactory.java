/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import com.danya.javaee.dao.Dao;
import com.danya.javaee.dao.DaoFactory;

/**
 *
 * @author danya
 */
public class MariaDbDaoFactory implements DaoFactory<Connection> {
    
    private static final String url = Properties.get("db.host");;
    private static final String user = Properties.get("db.login");;
    private static final String password = Properties.get("db.password");;
    private static final String driver = Properties.get("db.driver");
    private Map<Class, DaoCreator> creators;
    
    @Override
    public Connection getContext() throws DaoException {
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DaoException("Exception at opening connection", e);
        }
        return conn;
    }
    
    @Override
    public void closeContext(Connection connection) throws DaoException {
        try {
            connection.close();
        } catch(SQLException e) {
            throw new DaoException("Exception at closing connection", e);
        }
    }

    @Override
    public Dao getDao(Connection connection, Class dToClass) throws DaoException {
        DaoCreator creator = creators.get(dToClass);
        if (creator == null) {
            throw new DaoException("Dao obj for " + dToClass + " not found.");
        }
        return creator.create(connection);
    }
    
    public MariaDbDaoFactory() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Couldn't find driver for db: " + driver);
        }
        creators = new HashMap();
        creators.put(City.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection conn) {
                return new MariaDbCityDao(MariaDbDaoFactory.this, conn);
            }
        });
        creators.put(Address.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection conn) {
                return new MariaDbAddressDao(MariaDbDaoFactory.this, conn);
            }
        });
        creators.put(Route.class, new DaoCreator<Connection>() {
            @Override
            public Dao create(Connection conn) {
                return new MariaDbRouteDao(MariaDbDaoFactory.this, conn);
            }
        });
    }
    
}
