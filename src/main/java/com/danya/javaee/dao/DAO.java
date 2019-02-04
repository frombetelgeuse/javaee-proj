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
public interface DAO<E extends Identified> {
    
    public E create() throws DAOException;
    public E persist(E object)  throws DAOException;
    public E getEntityById(int key) throws DAOException;
    public void update(E object) throws DAOException;
    public void delete(E object) throws DAOException;
    public List<E> getAll() throws DAOException;
//    protected List<E> getAllWith(String field, Object condition) throws DAOException;
}
