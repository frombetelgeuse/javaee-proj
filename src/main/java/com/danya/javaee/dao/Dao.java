/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

import java.util.List;

/**
 *
 * @author danya
 */
public interface Dao<E extends Identified> {
    
    public E create() throws DaoException;
    public E persist(E object)  throws DaoException;
    public E getEntityById(int key) throws DaoException;
    public void update(E object) throws DaoException;
    public void delete(E object) throws DaoException;
    public List<E> getAll() throws DaoException;
//    protected List<E> getAllWith(String field, Object condition) throws DAOException;
}
