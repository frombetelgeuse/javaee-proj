/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.AbstractJdbcDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.danya.javaee.dao.DaoFactory;
import com.danya.javaee.dao.RouteDao;

/**
 *
 * @author danya
 */
public class MariaDbRouteDao extends AbstractJdbcDao<Route> implements RouteDao {
    
    private class PersistRoute extends Route {
        @Override public void setId(Integer id) { super.setId(id); }
    }

    public MariaDbRouteDao(DaoFactory<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
    }

    @Override
    protected String getSelectQuery() {
        return "SELECT * FROM routes";
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE routes SET name=? WHERE id=?";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM routes WHERE id=?";
    }

    @Override
    protected String getCreateQuery() {
        return "INSERT INTO routes(name) VALUES (?)";
    }

    @Override
    protected List<Route> parseResultSet(ResultSet rs) throws DaoException {
        List<Route> list = new ArrayList();
        try {
            while(rs.next()) {
                PersistRoute route = new PersistRoute();
                route.setId(rs.getInt("id"));
                route.setName(rs.getString("name"));
                List<City> cities = getCities(rs.getInt("id"));
                route.setCities(cities);
                list.add(route);
            }
        } catch (Exception e) {
            throw new DaoException("MariaDbAddressDao.parseResultSet", e);
        }
        return list;
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement ps, Route entity) throws DaoException {
        try {
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getId());
        } catch (SQLException e) {
            throw new DaoException("mariadbrouteDao.prepareStatementForUpdate", e);
        }
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement ps, Route entity) throws SQLException {
        ps.setString(1, entity.getName());
    }

    @Override
    public Route create() throws DaoException {
        Route route = new Route();
        return persist(route);
    }
    
    // everything that down here is for many-to-many relation
    
    public List<City> getCities(Integer id) throws DaoException {
        String sql = "SELECT id, id_city FROM routes INER JOIN cities_routes ON id=id_route WHERE id=?";
        List<City> list = new ArrayList();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                City city = (City) getDependence(City.class, rs.getInt("id_city"));
                list.add(city);                
            }
        } catch (Exception e) {
            throw new DaoException("mariadbroutedao.getCities", e);
        }
        return list;
    }
    @Override
    public void prepareForUpdate(Route entity) throws DaoException {
        deleteAdjacentTable(entity.getId());
        insertAdjacentTable(entity.getId(), entity.getCities());
    }
    
    @Override
    protected void prepareForDelete(Route route) throws DaoException {
        deleteAdjacentTable(route.getId());
    }
    
    @Override
    protected void prepareForPersist(Route route, Integer id) throws DaoException {
        insertAdjacentTable(id, route.getCities());
    }
    
    protected void deleteAdjacentTable(Integer id) throws DaoException {
        String sql = "DELETE FROM cities_routes WHERE id_route=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DaoException("mariadbroutedao.handleSaveCities", e);
        }
    }
    protected void insertAdjacentTable(Integer id, List<City> cities) throws DaoException {
        if (cities == null || cities.isEmpty()) {
            return;
        }
        for (int i = 0; i < cities.size(); i++) {
            cities.set(i, (City) saveDependence(City.class, cities.get(i)));
        }
        String sql = "INSERT INTO cities_routes(id_route, id_city) VALUES (?, ?)";
        for (City city : cities) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setInt(2, city.getId());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DaoException("MariaDbRouteDao.insertAdjacentTable", e);
            }
        }
    }
    
}
