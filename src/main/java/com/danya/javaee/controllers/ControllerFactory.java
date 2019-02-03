/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.controllers;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author danya
 */
public interface ControllerFactory {
    
    public Connection getConnection() throws SQLException;
    public CityController getCityController(Connection connection);
//    public CityController getCityController(Connection connection);
 
    
}
