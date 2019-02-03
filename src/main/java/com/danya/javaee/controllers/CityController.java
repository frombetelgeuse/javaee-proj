/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.controllers;

import com.danya.javaee.instances.City;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danya
 */
public class CityController extends AbstractController<City, Integer> {
    private static final String getEntityByIdQuery = "SELECT * FROM cities WHERE id=?;";
    private static final String getAllQuery = "SELECT * FROM cities;";
    private static final String updateQuery = "UPDATE cities SET name=? WHERE id=?;";
    private static final String deleteQuery = "DELETE FROM cities WHERE id=?";
    private static final String createQuery = "INSERT INTO cities(name) VALUES (?);";

    public CityController(Connection connection) {
        super(connection);
    }

    @Override
    public List<City> getAll() throws SQLException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        PreparedStatement ps = connection.prepareStatement(getAllQuery);
        ResultSet rs = ps.executeQuery();
        List<City> res = new ArrayList();
        while(rs.next()) {
            City city = new City();
            city.setId(rs.getInt("id"));
            city.setName(rs.getString("name"));
            res.add(city);
        }
        return res;
    }

    @Override
    public City getEntityById(Integer id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(getEntityByIdQuery);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        City city = new City();
        city.setId(rs.getInt("id"));
        city.setName(rs.getString("name"));
        return city;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(City entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(updateQuery);
        ps.setString(1, entity.getName());
        ps.setInt(2, entity.getId());
        ps.executeUpdate();
        
    }

    @Override
    public void delete(Integer id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(deleteQuery);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public City create(City entity) throws SQLException {
        if(entity.getId() != null) {
            throw new RuntimeException("Change this exception");
        }
        PreparedStatement ps = connection.prepareStatement(createQuery);
        ps.setString(1, entity.getName());
        ps.executeUpdate();
        
        ps = connection.prepareStatement("SELECT * FROM cities WHERE id=last_insert_id();");
        ResultSet rs = ps.executeQuery();
        rs.next();
        City entity2 = new City();
        entity2.setId(rs.getInt("id"));
        entity2.setName(rs.getString("name"));
        return entity2;
        
    }
    
}
