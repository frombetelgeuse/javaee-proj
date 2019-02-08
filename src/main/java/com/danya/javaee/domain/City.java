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
public class City implements Identified {
    private Integer id;
    private String name;    

    @Override
    public String toString() {
        return "City{" + "id=" + id + ", name=" + name + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)  return false;
        if (! (o instanceof City)) return false;
        return getId() == ((City)o).getId();
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
