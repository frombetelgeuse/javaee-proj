/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author danya
 */
public abstract class AbstractJdbcDAO<E extends Identified> implements DAO<E> {
    protected Connection connection;
//    private ConnectionPool connectionPool;

    public AbstractJdbcDAO(Connection connection) {
        this.connection = connection;
//        connectionPool = ConnectionPool.getConnectionPool();
//        connection = connectionPool.getConnection();
    }
    
    protected abstract String getSelectQuery();
    protected abstract String getUpdateQuery();
    protected abstract String getDeleteQuery();
    protected abstract String getCreateQuery();
    protected abstract List<E> parseResultSet(ResultSet rs) throws SQLException;
    protected abstract void prepareStatementForUpdate(PreparedStatement ps, E entity) throws SQLException;
    protected abstract void prepareStatementForDelete(PreparedStatement ps, E entity) throws SQLException;
    protected abstract void prepareStatementForInsert(PreparedStatement ps, E entity) throws SQLException;
    @Override
    public List<E> getAll() throws DAOException {
        List<E> list;
        String sql = getSelectQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        return list;
    };
    @Override
    public E getEntityById(int id) throws DAOException {
        List<E> list;
        String sql = getSelectQuery();
        sql += " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DAOException(e);
        }
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            throw new DAOException("Received more than one record.");
        }
        return list.get(0);
    };
    @Override
    public void update(E entity) throws DAOException {
        String sql = getUpdateQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForUpdate(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("On update modified more than one record: " + count);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
    @Override
    public void delete(E entity) throws DAOException {
        String sql = getDeleteQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForDelete(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("On delete modified more than one record: " + count);
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }
    @Override
    public E persist(E entity) throws DAOException {
        if(entity.getId() != null) {
            throw new DAOException("Object is already persist.");
        }
        E persistInstance;
        //add record
        String sql = getCreateQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForInsert(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("On persist modify more then 1 record: " + count);
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
        //get persisted record
        sql = getSelectQuery();
        sql += " WHERE id = last_insert_id()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<E> list = parseResultSet(rs);
            if ((list == null) || (list.size() != 1)) {
                throw new DAOException("Exception on findById new persist data.");
            }
            persistInstance = list.get(0);
        } catch(Exception e) {
            throw new DAOException(e);
        }
        return persistInstance;
    }
    
//    public void closeConnection() throws SQLException {
//        connection.close();
//    }
    
//    protected PreparedStatement getPrepareStatement(String sql) {
//        PreparedStatement ps = null;
//        try {
//            ps = connection.prepareStatement(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return ps;
//    }
    
//    protected void closePrepareStatement(PreparedStatement ps) {
//        if (ps != null) {
//            try {
//                ps.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
