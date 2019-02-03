/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author danya
 */
public abstract class AbstractController<E, K> {
    protected Connection connection;
//    private ConnectionPool connectionPool;

    public AbstractController(Connection connection) {
        this.connection = connection;
//        connectionPool = ConnectionPool.getConnectionPool();
//        connection = connectionPool.getConnection();
    }
    public abstract List<E> getAll() throws SQLException;
    public abstract E getEntityById(K id) throws SQLException;
    public abstract void update(E entity) throws SQLException;
    public abstract void delete(K id) throws SQLException;
    public abstract E create(E entity) throws SQLException;
    
    public void closeConnection() throws SQLException {
        connection.close();
    }
    
    protected PreparedStatement getPrepareStatement(String sql) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ps;
    }
    
    protected void closePrepareStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
