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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author danya
 */
public abstract class AbstractJdbcDAO<E extends Identified> implements DAO<E> {
    protected DAOFactory parentFactory;
    protected Connection connection;

    public AbstractJdbcDAO(DAOFactory<Connection> parentFactory, Connection connection) {
        this.parentFactory = parentFactory;
        this.connection = connection;
    }
    
    protected Identified getDependence(Class<? extends Identified> dtoClass, Integer id) throws DAOException {
        if (id == null) return null;
        return parentFactory.getDAO(connection, dtoClass).getEntityById(id);
    }
    
    protected abstract String getSelectQuery();
    protected abstract String getUpdateQuery();
    protected abstract String getDeleteQuery();
    protected abstract String getCreateQuery();
    protected abstract List<E> parseResultSet(ResultSet rs) throws DAOException;
    protected abstract void prepareStatementForUpdate(PreparedStatement ps, E entity) throws SQLException;
//    protected abstract void prepareStatementForDelete(PreparedStatement ps, E entity) throws SQLException;
    protected abstract void prepareStatementForInsert(PreparedStatement ps, E entity) throws SQLException;
    
    protected List<E> getAllWith(String field, Object condition) throws DAOException {
        List<E> list;
        String sql = getSelectQuery();
        sql += "WHERE " + field + "=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, condition);
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DAOException("AbstractJdbcDAO.getAllWith", e);
        }
        return list;
    }
    @Override
    public List<E> getAll() throws DAOException {
        List<E> list;
        String sql = getSelectQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DAOException("AbstractJdbcDAO.getAll", e);
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
            throw new DAOException("AbstractJdbcDAO.getEntityById", e);
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
        saveDependences(entity);
        String sql = getUpdateQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForUpdate(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("On update modified more than one record: " + count);
            }
        } catch (Exception e) {
            throw new DAOException("AbstractJdbcDAO.update", e);
        }
    }
    @Override
    public void delete(E entity) throws DAOException {
        String sql = getDeleteQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            prepareStatementForDelete(ps, entity);
            ps.setInt(1, entity.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DAOException("On delete modified more than one record: " + count);
            }
        } catch (SQLException e) {
            throw new DAOException("AbstractJdbcDAO.delete", e);
        }
    }
    @Override
    public E persist(E entity) throws DAOException {
        if(entity.getId() != null) {
            throw new DAOException("Object is already persist.");
        }
        saveDependences(entity);
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
            throw new DAOException("AbstractJdbcDAO.persist(1)", e);
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
            throw new DAOException("AbstractJdbcDAO.persist(2)", e);
        }
        return persistInstance;
    }
    
    private Set<ManyToOne> relations = new HashSet();
    
    protected boolean addRelation(Class<? extends Identified> ownerClass, String field) {
        try {
            return relations.add(new ManyToOne(ownerClass, parentFactory, field));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void saveDependences(E owner) throws DAOException {
        for (ManyToOne m : relations) {
            try {
                if (m.getDependence(owner) == null) {
                    continue;
                }
                if (m.getDependence(owner).getId() == null) {
                    Identified depend = m.persistDependence(owner, connection);
                    m.setDependence(owner, depend);
                } else {
                    m.updateDependence(owner, connection);
                }
            } catch (Exception e) {
                throw new DAOException("AbstractJdbcDAO.saveDependences in relation " + m + ".", e);
            }
        }
    }
}
