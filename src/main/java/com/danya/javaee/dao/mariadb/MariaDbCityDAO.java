/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.AbstractJdbcDAO;
import com.danya.javaee.dao.DAOException;
import com.danya.javaee.domain.City;
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
public class MariaDbCityDAO extends AbstractJdbcDAO<City> {
    
    private class PersistCity extends City {
        public void setId(int id) {
            super.setId(id);
        }
    }

    public MariaDbCityDAO(Connection connection) {
        super(connection);
    }
    
    @Override
    protected List<City> parseResultSet(ResultSet rs) throws SQLException {
        List<City> list = new ArrayList();
        while(rs.next()) {
            PersistCity city = new PersistCity();
            city.setId(rs.getInt("id"));
            city.setName(rs.getString("name"));
            list.add(city);
        }
        return list;
    }
    @Override
    protected String getSelectQuery() {
        return "SELECT * FROM cities";
    }
    @Override
    protected String getUpdateQuery() {
        return "UPDATE cities SET name=? WHERE id=?";
    }
    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM cities WHERE id=?";
    }
    @Override
    protected String getCreateQuery() {
        return "INSERT INTO cities(name) VALUES (?)";
    }
    @Override
    public City create() throws DAOException {
        City city = new City();
        return persist(city);
    }
    @Override
    protected void prepareStatementForUpdate(PreparedStatement ps, City entity) throws SQLException {
        ps.setString(1, entity.getName());
        ps.setInt(2, entity.getId());
    }
    @Override
    protected void prepareStatementForDelete(PreparedStatement ps, City entity) throws SQLException {
        ps.setInt(1, entity.getId());
    }
    @Override
    protected void prepareStatementForInsert(PreparedStatement ps, City entity) throws SQLException {
        ps.setString(1, entity.getName());
    }
}
