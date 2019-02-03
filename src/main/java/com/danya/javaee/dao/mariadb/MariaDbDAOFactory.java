/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.DAO;
import com.danya.javaee.dao.DAOException;
import com.danya.javaee.dao.DAOFactory;
import com.danya.javaee.domain.City;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author danya
 */
public class MariaDbDAOFactory implements DAOFactory<Connection> {
    
    private static final String url = "jdbc:mysql://localhost:3306/<db>";
    private static final String user = "<user>";
    private static final String password = "<pass>";
    private static final String driver = "org.mariadb.jdbc.Driver";
    private Map<Class, DAOCreator> creators;

//    public MariaDBFactory() {
//    }
    
    @Override
    public Connection getContext() throws DAOException {
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DAOException(e);
        }
        return conn;
    }

    @Override
    public DAO getDAO(Connection connection, Class dToClass) throws DAOException {
        DAOCreator creator = creators.get(dToClass);
        if (creator == null) {
            throw new DAOException("DAO obj for " + dToClass + " not found.");
        }
        return creator.create(connection);
//        return new MariaDbCityDAO(connection);
    }
    
    public MariaDbDAOFactory() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // TODO Change to throwing exception
            e.printStackTrace();
        }
        creators = new HashMap();
        creators.put(City.class, new DAOCreator<Connection>() {
            @Override
            public DAO create(Connection conn) {
                return new MariaDbCityDAO(conn);
            }
        });
    }
    
}
