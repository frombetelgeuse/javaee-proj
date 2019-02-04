/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.dao.mariadb;

import com.danya.javaee.dao.AbstractJdbcDAO;
import com.danya.javaee.dao.DAOException;
import com.danya.javaee.dao.DAOFactory;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danya
 */
public class MariaDbAddressDAO extends AbstractJdbcDAO<Address>{
    
    private class PersistAddress extends Address {
        @Override public void setId(Integer id) { super.setId(id); }
    }

    public MariaDbAddressDAO(DAOFactory<Connection> parentFactory, Connection connection) {
        super(parentFactory, connection);
        addRelation(Address.class, "city");
    }

    @Override
    protected String getSelectQuery() {
        return "SELECT * FROM addresses";
    }

    @Override
    protected String getUpdateQuery() {
        return "UPDATE addresses SET name=?, city=?  WHERE id=?";
    }

    @Override
    protected String getDeleteQuery() {
        return "DELETE FROM addresses WHERE id=?";
    }

    @Override
    protected String getCreateQuery() {
        return "INSERT INTO addresses(name,city) VALUES (?, ?)";
    }

    @Override
    protected List<Address> parseResultSet(ResultSet rs) throws DAOException {
        List<Address> list = new ArrayList();
        try {
            while(rs.next()) {
                PersistAddress address = new PersistAddress();
                address.setId(rs.getInt("id"));
                address.setName(rs.getString("name"));
                address.setCity((City) getDependence(City.class, (Integer) rs.getObject("city")));
                list.add(address);
            }
        } catch (DAOException | SQLException e) {
            throw new DAOException("MariaDbAddressDAO.parseResultSet", e);
        }
        return list;
    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement ps, Address entity) throws SQLException {
        Integer cityId = (entity.getCity() == null || entity.getCity().getId() == null) ? null : entity.getCity().getId();
        ps.setString(1, entity.getName());
        ps.setObject(2, cityId, Types.INTEGER);
        ps.setInt(3, entity.getId());
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement ps, Address entity) throws SQLException {
        Integer cityId = (entity.getCity() == null || entity.getCity().getId() == null) ? null : entity.getCity().getId();
        ps.setString(1, entity.getName());
        ps.setObject(2, cityId, Types.INTEGER);
    }

    @Override
    public Address create() throws DAOException {
        Address address = new Address();
        return persist(address);
    }
    
}
