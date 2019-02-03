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
public interface DAOFactory<Context> {
    
    public interface DAOCreator<Context> {
        public DAO create(Context context);
    }
    public Context getContext() throws DAOException;
    public DAO getDAO(Context context, Class dToClass) throws DAOException;
//    public MariaDbCityDAO getCityDAO(Connection connection);
//    public CityController getCityController(Connection connection);
 
    
}
