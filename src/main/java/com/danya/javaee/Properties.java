/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee;

import com.danya.javaee.dao.Dao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.dao.DaoFactory;
import com.danya.javaee.dao.Identified;
import com.danya.javaee.dao.mariadb.MariaDbDaoFactory;
import java.util.ResourceBundle;

/**
 *
 * @author danya
 */
public class Properties {
    
    private static ResourceBundle rb;
    private static DaoFactory daoFactory;
    static {
        rb = ResourceBundle.getBundle("config");
        daoFactory = new MariaDbDaoFactory();
    }
    
    public static String get(String prop) {
        return rb.getString(prop);
    }
    
    public static DaoFactory getDaoFactory() {
        return daoFactory;
    }
}
