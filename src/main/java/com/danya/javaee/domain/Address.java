/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.domain;

import com.danya.javaee.dao.Identified;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null)  return false;
        if (! (o instanceof Address)) return false;
        return (Objects.equals(((City)o).getId(), getId()));
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
    
    public void setId(Integer id) {
        this.id = id;
    }
}
