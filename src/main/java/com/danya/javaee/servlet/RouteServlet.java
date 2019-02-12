/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.CityDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.dao.RouteDao;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import com.danya.javaee.util.ParameterUtils;
import com.danya.javaee.util.SessionUtils;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
@WebServlet("/routes")
public class RouteServlet extends HttpServlet {
    
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
        RouteDao routeDao;
        CityDao cityDao;
        try {
            context = (Connection) Properties.getDaoFactory().getContext();
            routeDao = (RouteDao) Properties.getDaoFactory().getDao(context, Route.class);
            cityDao = (CityDao) Properties.getDaoFactory().getDao(context, City.class);
        } catch (DaoException e) {
            throw new ServletException("Cannot connect to db", e);
        }
        
        Route routeBean = getProperties(request);
        
//    HANDLE REQUESTS
        try {
            String beanAction = request.getParameter("beanAction");
            if (null != beanAction) switch (beanAction) {
                case "save":
                    if (routeBean.getId() == null) {
                        List<City> cities = routeBean.getCities();
                        if (!cities.isEmpty()) {
                            for (int i = 0; i < cities.size(); i++) {
                                if (cities.get(i).getId() == null) {
                                    List<City> citiesTemp = cityDao.getAllWith("name", cities.get(i).getName());
                                    if (!citiesTemp.isEmpty()) {
                                        cities.set(i, citiesTemp.get(0));
                                    }
                                }
                            }
                        }
                        Integer id = routeDao.persist(routeBean).getId();
                        routeBean.setId(id);
                    } else {
                        routeDao.update(routeBean);
                    }   break;
                case "delete":
                    routeDao.delete(routeBean);
                    SessionUtils.getSet(request.getSession(), "routes.selected").remove(routeBean.getId());
                    routeBean.setId(null);
                    break;
                case "addToSelect":
                    SessionUtils.getSet(request.getSession(), "routes.selected").add(routeBean.getId());
                    break;
                case "removeFromSelect":
                    SessionUtils.getSet(request.getSession(), "routes.selected").remove(routeBean.getId());
                    break;
                default:
                    break;
            }

            String selectedAction = request.getParameter("selectedAction");
            if ("deleteAll".equals(selectedAction)) {
                Set<Integer> routesIdSelected = SessionUtils.getSet(request.getSession(), "routes.selected");
                for (Integer id : routesIdSelected) {
                    Route toDelete = routeDao.getEntityById(id);
                    if (toDelete != null) {
                        routeDao.delete(toDelete);
                    }
                }
                routesIdSelected.clear();
            } else if ("clear".equals(selectedAction)) {
                SessionUtils.getSet(request.getSession(), "routes.selected").clear();
            }

            String sortByAction = request.getParameter("sortBy");
            if (sortByAction != null) {
                sortByAction = sortByAction.toLowerCase();
                if (sortByAction.equals(request.getSession().getAttribute("routes.sortBy"))) {
                    boolean rev = SessionUtils.getBoolean(request.getSession(), "routes.reverse");
                    request.getSession().setAttribute("routes.reverse", !rev);
                }
                request.getSession().setAttribute("routes.sortBy", sortByAction);
            }
        } catch (DaoException e) {
            throw new ServletException("Problems with db", e);
        }
        
        
//    PREPARE DATA
        List<Route> list;   
        try {
            list = routeDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read from db");
        }
        String sortBy = (String) request.getSession().getAttribute("routes.sortBy");
        sortBy = (sortBy == null) ? "" : sortBy;
        switch (sortBy) {
            case "id" : list.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                break;
            case "name" : list.sort(
                    Comparator.comparing(
                            Route::getName, 
                            Comparator.nullsLast(Comparator.naturalOrder())));
                break;
        }
        if (SessionUtils.getBoolean(request.getSession(), "routes.reverse")) {
            Collections.reverse(list);
        }
        Set<Integer> routesIdSelected = SessionUtils.getSet(request.getSession(), "routes.selected");
        
        try {
            Properties.getDaoFactory().closeContext(context);
        } catch(DaoException e) {
            throw new ServletException("Problems with closing db", e);
        }

        request.setAttribute("routeBean", routeBean);
        request.setAttribute("list", list);
        request.setAttribute("routesIdSelected", routesIdSelected);

        request.getRequestDispatcher("routes.jsp").forward(request, response);
    }
    
    public Route getProperties(HttpServletRequest request) {
        Route route = new Route();
        route.setId(ParameterUtils.getParameterInt(request, "id"));
        route.setName(ParameterUtils.getParameter(request, "name"));

        List<City> cities = new ArrayList();
        for (int i = 0; true; i++) {
            Integer cityId = ParameterUtils.getParameterInt(request, "city."+i+".id");
            String cityName = ParameterUtils.getParameter(request, "city."+i+".name");
            if (cityId != null || cityName != null) {
                City city = new City();
                city.setId(cityId);
                city.setName(cityName);
                cities.add(city);
            } else {
                break;
            }
        }
        route.setCities(cities);
        return route;
    }
    
}
