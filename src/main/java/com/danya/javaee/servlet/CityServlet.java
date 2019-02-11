/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.CityDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.City;
import com.danya.javaee.util.SessionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author danya
 */
@WebServlet("/cities")
public class CityServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws
            ServletException, IOException {
        processRequest(request, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp) throws
            ServletException, IOException {
        processRequest(request, resp);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse resp) throws
            ServletException, IOException {
//        PREPARE DB
        Connection context;
        CityDao cityDao;
        try {
            context = (Connection) Properties.getDaoFactory().getContext();
            cityDao = (CityDao) Properties.getDaoFactory().getDao(context, City.class);
        } catch (DaoException e) {
            throw new ServletException("Cannot connect to db", e);
        }
        
        City cityBean = buildCity(request);
        
//    HANDLE REQUESTS
        try {
            String beanAction = request.getParameter("beanAction");
            beanAction = (beanAction == null) ? "" : beanAction;
            switch (beanAction) {
                case "save":
                    if (cityBean.getId() == null) {
                        Integer id = cityDao.persist(cityBean).getId();
                        cityBean.setId(id);
                    } else {
                        cityDao.update(cityBean);
                    }
                    break;
                case "delete":
                    cityDao.delete(cityBean);
                    SessionUtils.getSet(request.getSession(), "cities.selected").remove(cityBean.getId());
                    cityBean.setId(null);
                    break;
                case "addToSelect":
                    SessionUtils.getSet(request.getSession(), "cities.selected").add(cityBean.getId());
                    break;
                case "removeFromSelect":
                    SessionUtils.getSet(request.getSession(), "cities.selected").remove(cityBean.getId());
                    break;
            }

            String selectedAction = request.getParameter("selectedAction");
            selectedAction = (selectedAction == null) ? "" : selectedAction;
            switch (selectedAction) {
                case "deleteAll":
                    Set<Integer> citiesIdSelected = SessionUtils.getSet(request.getSession(), "cities.selected");
                    for (Integer id : citiesIdSelected) {
                        City toDelete = cityDao.getEntityById(id);
                        if (toDelete != null) {
                            cityDao.delete(toDelete);
                        }
                    }
                    citiesIdSelected.clear();
                    break;
                case "clear":
                    SessionUtils.getSet(request.getSession(), "cities.selected").clear();
                    break;
            }

            String sortByAction = request.getParameter("sortBy");
            if (sortByAction != null) {
                sortByAction = sortByAction.toLowerCase();
                if (sortByAction.equals(request.getSession().getAttribute("cities.sortBy"))) {
                    boolean rev = SessionUtils.getBoolean(request.getSession(), "cities.reverse");
                    request.getSession().setAttribute("cities.reverse", !rev);
                }
                request.getSession().setAttribute("cities.sortBy", sortByAction);
            }
        } catch(DaoException e) {
            throw new ServletException("Problems with db", e);
        }
        
        
//    PREPARE DATA
        List<City> list;
        try {
            list = cityDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read list from db", e);
        }
        String sortBy = (String) request.getSession().getAttribute("cities.sortBy");
        sortBy = (sortBy == null) ? "" : sortBy;
        switch (sortBy) {
            case "id" : list.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                break;
            case "name" : list.sort(
                    Comparator.comparing(
                            City::getName, 
                            Comparator.nullsLast(Comparator.naturalOrder())));
                break;
        }
        if (SessionUtils.getBoolean(request.getSession(), "cities.reverse")) {
            Collections.reverse(list);
        }
        Set<Integer> citiesIdSelected = SessionUtils.getSet(request.getSession(), "cities.selected");
        
        try {
            Properties.getDaoFactory().closeContext(context);
        } catch(DaoException e) {
            throw new ServletException("Problems with closing db", e);
        }

        request.setAttribute("list", list);
        request.setAttribute("citiesIdSelected", citiesIdSelected);

        request.getRequestDispatcher("cities.jsp").forward(request, resp);
    }
    
    public City buildCity(HttpServletRequest request) {
        City city = new City();
        String idS = request.getParameter("id");
        String name = request.getParameter("name");
        Integer id = ("".equals(idS) || idS == null) ? null : Integer.valueOf(idS);
        city.setId(id);
        city.setName(name);
        return city;
    }
}
