/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.RouteDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.City;
import com.danya.javaee.domain.Route;
import com.danya.javaee.util.UrlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
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
@WebServlet("/routes")
public class RouteServlet extends HttpServlet {
    
    private RouteDao routeDao;
    
    @Override
    public void init(ServletConfig sc) {
        routeDao = (RouteDao) Properties.getDao(Route.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        PrintWriter out = resp.getWriter();
        List<Route> routes;
        try {
            routes = routeDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read from db", e);
        }
        Map<String, String> params = UrlUtils.parseUrlParams(req.getQueryString());
        switch (params.getOrDefault("sortBy", "")) {
            case "id" : routes.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                    break;
            case "name" : routes.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));
                    break;
        }
        printRoutesTableFormat(out, routes);
        
        out.println(
                "<style type=\"text/css\">\n" +
                "   table, td, th { border: 1px solid black; border-collapse: collapse;} "
                + " td { padding: 5px; text-align: left;} "
                + " th { padding: 5px; text-align: center; }"
                + "</style>");
    }
    
    public static void printRoutesTableFormat(PrintWriter out, List<Route> cities) {
        out.println("<h1>Routes</h1>");
        out.println("<table>");
        out.println("<tr><th rowspan=\"2\">Id</th><th rowspan=\"2\">Name</th><th colspan=\"2\">City</th></tr><tr><th>Id</th><th>Name</th></tr>");
        cities.stream().map(RouteServlet::formatRouteToTable).forEach(out::println);
        out.println("</table>");
        
    }
    
    public static String formatRouteToTable(Route route) {
        int citiesNum = route.getCities().size();
        citiesNum = (citiesNum == 0) ? 2 : citiesNum+1;
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>").append("<td rowspan=\"").append(citiesNum).append("\">").append(route.getId()).append("</td>")
            .append("<td rowspan=\"").append(citiesNum).append("\">").append(route.getName()).append("</td>");
//            .append("<td>").append(route.getCity().getName()).append("</td>")
        for (City city : route.getCities()) {
            sb.append("<tr>").append("<td>").append(city.getId()).append("</td>")
                    .append("<td>").append(city.getName()).append("</td>").append("</tr>");
        }
        sb.append("<tr>");
        return sb.toString();
    }
}
