/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.domain;

import com.danya.javaee.dao.Identified;

/**
 *
 * @author danya
 */
public class Address implements Identified {
    private Integer id;
    private String name;
    private City city;

    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", name=" + name + ", city=" + city + '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
    protected void setId(Integer id) {
        this.id = id;
    }
}
