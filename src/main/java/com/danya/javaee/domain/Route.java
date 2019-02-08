/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.domain;

import com.danya.javaee.dao.Identified;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author danya
 */
public class Route implements Identified {
    
    private Integer id;
    private String name;
    private List<City> cities;

    @Override
    public boolean equals(Object o) {
        if (o == null)  return false;
        if (! (o instanceof Route)) return false;
        return (Objects.equals(((City)o).getId(), getId()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    @Override
    public Integer getId() {
        return id;
    }
    
}
