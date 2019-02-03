/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author danya
 */
public class MariaDBFactory implements ControllerFactory {
    
    private static final String url = "jdbc:mysql://localhost:3306/<db>";
    private static final String user = "<user>";
    private static final String password = "<pass>";

//    public MariaDBFactory() {
//    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public CityController getCityController(Connection connection) {
        return new CityController(connection);
    }
    
}
