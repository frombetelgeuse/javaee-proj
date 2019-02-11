/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

/**
 *
 * @author danya
 */
public interface DaoFactory<Context> {
    
    public interface DaoCreator<Context> {
        public Dao create(Context context);
    }
    public Context getContext() throws DaoException;
    public void closeContext(Context context) throws DaoException;
    public Dao getDao(Context context, Class dToClass) throws DaoException;
 
    
}
