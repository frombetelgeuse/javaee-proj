/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao;

import java.lang.reflect.Field;
import java.sql.Connection;

/**
 *
 * @author danya
 */
public class ManyToOne<Owner extends Identified, Dependence extends Identified> {
    
    private DaoFactory<Connection> factory;
    private Field field;
    private String name;
    private int hash;
    
    public Dependence getDependence(Owner owner) throws IllegalAccessException {
        return (Dependence) field.get(owner);
    }
    
    public void setDependence(Owner owner, Dependence dependence) throws IllegalAccessException {
        field.set(owner, dependence);
    }
    
    public Identified persistDependence(Owner owner, Connection connection) throws IllegalAccessException, DaoException {
        return factory.getDao(connection, field.getType()).persist(getDependence(owner));
    }
    
    public void updateDependence(Owner owner, Connection connection) throws IllegalAccessException, DaoException {
        factory.getDao(connection, field.getType()).update(getDependence(owner));
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return hash;
    }
    
    public ManyToOne(Class<Owner> ownerClass, DaoFactory<Connection> factory, String field) throws NoSuchFieldException {
        this.factory = factory;
        this.field = ownerClass.getDeclaredField(field);
        this.field.setAccessible(true);
        name = ownerClass.getSimpleName() + " to " + this.field.getType().getSimpleName();
        hash = ownerClass.hashCode() & field.hashCode();
    }
}
