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
public abstract class AbstractJdbcDao<E extends Identified> implements Dao<E> {
    protected DaoFactory parentFactory;
    protected Connection connection;

    public AbstractJdbcDao(DaoFactory<Connection> parentFactory, Connection connection) {
        this.parentFactory = parentFactory;
        this.connection = connection;
    }
    
    protected abstract String getSelectQuery();
    protected abstract String getUpdateQuery();
    protected abstract String getDeleteQuery();
    protected abstract String getCreateQuery();
    protected abstract List<E> parseResultSet(ResultSet rs) throws DaoException;
    protected abstract void prepareStatementForUpdate(PreparedStatement ps, E entity) throws DaoException;
    protected abstract void prepareStatementForInsert(PreparedStatement ps, E entity) throws SQLException;
    protected void prepareForUpdate(E entity) throws DaoException {}
    protected void prepareForDelete(E entity) throws DaoException {}
    protected void prepareForPersist(E entity, Integer id) throws DaoException {}
    
    protected List<E> getAllWith(String field, Object condition) throws DaoException {
        List<E> list;
        String sql = getSelectQuery();
        sql += "WHERE " + field + "=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, condition);
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.getAllWith", e);
        }
        return list;
    }
    @Override
    public List<E> getAll() throws DaoException {
        List<E> list;
        String sql = getSelectQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.getAll", e);
        }
        return list;
    };
    @Override
    public E getEntityById(int id) throws DaoException {
        List<E> list;
        String sql = getSelectQuery();
        sql += " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            list = parseResultSet(rs);
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.getEntityById", e);
        }
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            throw new DaoException("Received more than one record.");
        }
        return list.get(0);
    };
    @Override
    public void update(E entity) throws DaoException {
        prepareForUpdate(entity);
        saveDependences(entity);
        String sql = getUpdateQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForUpdate(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DaoException("On update modified more than one record: " + count);
            }
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.update", e);
        }
    }
    @Override
    public void delete(E entity) throws DaoException {
        prepareForDelete(entity);
        String sql = getDeleteQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getId());
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DaoException("On delete modified more than one record: " + count);
            }
        } catch (SQLException e) {
            throw new DaoException("AbstractJdbcDao.delete", e);
        }
    }
    @Override
    public E persist(E entity) throws DaoException {
        if(entity.getId() != null) {
            throw new DaoException("Object is already persist.");
        }
        saveDependences(entity);
        E persistInstance;
        //add record
        String sql = getCreateQuery();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            prepareStatementForInsert(ps, entity);
            int count = ps.executeUpdate();
            if (count != 1) {
                throw new DaoException("On persist modify more then 1 record: " + count);
            }
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.persist(1)", e);
        }
        Integer lastInsertId;
        try (PreparedStatement ps = connection.prepareStatement("SELECT last_insert_id()")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            lastInsertId = rs.getInt("last_insert_id()");
            prepareForPersist(entity, lastInsertId);
        } catch (Exception e) {
            throw new DaoException("AbstractJdbcDao.persist", e);
        }
        //get persisted record
        sql = getSelectQuery();
        sql += " WHERE id = " + lastInsertId;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            List<E> list = parseResultSet(rs);
            if ((list == null) || (list.size() != 1)) {
                throw new DaoException("Exception on findById new persist data.");
            }
            persistInstance = list.get(0);
        } catch(Exception e) {
            throw new DaoException("AbstractJdbcDao.persist(2)", e);
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
    
    protected Identified getDependence(Class<? extends Identified> dtoClass, Integer id) throws DaoException {
        if (id == null) return null;
        return parentFactory.getDao(connection, dtoClass).getEntityById(id);
    }
    
    protected Identified saveDependence(Class<? extends Identified> dtoClass, Identified entity) throws DaoException {
        if (entity.getId() == null) {
            return parentFactory.getDao(connection, dtoClass).persist(entity);
        } else {
            parentFactory.getDao(connection, dtoClass).update(entity);
            return entity;
        }
    }
    
    private void saveDependences(E owner) throws DaoException {
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
                throw new DaoException("AbstractJdbcDao.saveDependences in relation " + m + ".", e);
            }
        }
    }
}
