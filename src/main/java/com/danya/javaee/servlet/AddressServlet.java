/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.AddressDao;
import com.danya.javaee.dao.CityDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.Address;
import com.danya.javaee.domain.City;
import com.danya.javaee.util.ParameterUtils;
import com.danya.javaee.util.SessionUtils;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author danya
 */
@WebServlet("/addresses")
public class AddressServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        processRequest(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
            processRequest(req, resp);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
//    PREPARE DB
        Connection context;
        AddressDao addressDao;
        CityDao cityDao;
        try {
            context = (Connection) Properties.getDaoFactory().getContext();
            addressDao = (AddressDao) Properties.getDaoFactory().getDao(context, Address.class);
            cityDao = (CityDao) Properties.getDaoFactory().getDao(context, City.class);
        } catch (DaoException e) {
            throw new ServletException("Cannot connect to db", e);
        }
        
        Address addressBean = getProperties(request);
        
//    HANDLE REQUESTS
        try {
            String beanAction = request.getParameter("beanAction");
            if (null != beanAction) switch (beanAction) {
                case "save":
                    if (addressBean.getId() == null) {
                        if (addressBean.getCity() != null && addressBean.getCity().getId() == null) {
                            List<City> cities = cityDao.getAllWith("name", addressBean.getCity().getName());
                            if (!cities.isEmpty()) {
                                addressBean.setCity(cities.get(0));
                            }
                        }
                        Integer id = addressDao.persist(addressBean).getId();
                        addressBean.setId(id);
                    } else {
                        addressDao.update(addressBean);
                    }   break;
                case "delete":
                    addressDao.delete(addressBean);
                    SessionUtils.getSet(request.getSession(), "addresses.selected").remove(addressBean.getId());
                    addressBean.setId(null);
                    break;
                case "addToSelect":
                    SessionUtils.getSet(request.getSession(), "addresses.selected").add(addressBean.getId());
                    break;
                case "removeFromSelect":
                    SessionUtils.getSet(request.getSession(), "addresses.selected").remove(addressBean.getId());
                    break;
                default:
                    break;
            }

            String selectedAction = request.getParameter("selectedAction");
            if (selectedAction != null) switch (selectedAction) {
                case "deleteAll":
                    Set<Integer> addressesIdSelected = SessionUtils.getSet(request.getSession(), "addresses.selected");
                    for (Integer id : addressesIdSelected) {
                        Address toDelete = addressDao.getEntityById(id);
                        if (toDelete != null) {
                            addressDao.delete(toDelete);
                        }
                    }
                    addressesIdSelected.clear();
                    break;
                case "clear":
                    SessionUtils.getSet(request.getSession(), "addresses.selected").clear();
                    break;
            }

            String sortByAction = request.getParameter("sortBy");
            if (sortByAction != null) {
                sortByAction = sortByAction.toLowerCase();
                if (sortByAction.equals(request.getSession().getAttribute("addresses.sortBy"))) {
                    boolean rev = SessionUtils.getBoolean(request.getSession(), "addresses.reverse");
                    request.getSession().setAttribute("addresses.reverse", !rev);
                }
                request.getSession().setAttribute("addresses.sortBy", sortByAction);
            }
        } catch (DaoException e) {
            throw new ServletException("Problems with db", e);
        }
        
        
    //    PREPARE DATA
        List<Address> list;
        try {
            list = addressDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read from db");
        }
        String sortBy = (String) request.getSession().getAttribute("addresses.sortBy");
        sortBy = (sortBy == null) ? "" : sortBy;
        switch (sortBy) {
            case "id" : list.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                break;
            case "name" : list.sort(
                    Comparator.comparing(
                            Address::getName, 
                            Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "cityid" : list.sort(
                    Comparator.comparing(
                            l->l.getCity().getId(), 
                            Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "cityname" : list.sort(
                    Comparator.comparing(
                            l->l.getCity().getName(), 
                            Comparator.nullsLast(Comparator.naturalOrder())));
                break;
        }
        if (SessionUtils.getBoolean(request.getSession(), "addresses.reverse")) {
            Collections.reverse(list);
        }
        Set<Integer> addressesIdSelected = SessionUtils.getSet(request.getSession(), "addresses.selected");
        
        try {
            Properties.getDaoFactory().closeContext(context);
        } catch(DaoException e) {
            throw new ServletException("Problems with closing db", e);
        }

        request.setAttribute("addressBean", addressBean);
        request.setAttribute("list", list);
        request.setAttribute("addressesIdSelected", addressesIdSelected);

        request.getRequestDispatcher("addresses.jsp").forward(request, response);
    }
    
    public Address getProperties(HttpServletRequest request) {
        Address address = new Address();
        address.setId(ParameterUtils.getParameterInt(request, "id"));
        address.setName(ParameterUtils.getParameter(request, "name"));

        Integer cityId = ParameterUtils.getParameterInt(request, "city.id");
        String cityName = ParameterUtils.getParameter(request, "city.name");
        if (cityId != null || cityName != null) {
            City city = new City();
            city.setId(cityId);
            city.setName(cityName);
            address.setCity(city);
        }
        return address;
    }
}
