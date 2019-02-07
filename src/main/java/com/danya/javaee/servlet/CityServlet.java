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
@WebServlet("/cities")
public class CityServlet extends HttpServlet {
    
    private CityDao cityDao;
    
    @Override
    public void init(ServletConfig sc) {
        cityDao = (CityDao) Properties.getDao(City.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        PrintWriter out = resp.getWriter();
        List<City> cities;
        try {
            cities = cityDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read from db", e);
        }
        Map<String, String> params = UrlUtils.parseUrlParams(req.getQueryString());
        switch (params.getOrDefault("sortBy", "")) {
            case "id" : cities.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                    break;
            case "name" : cities.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));
                    break;
        }
        printCitiesTableFormat(out, cities);
        
        out.println(
                "<style type=\"text/css\">\n" +
                "   table, td, th { border: 1px solid black; border-collapse: collapse;} "
                + " td { padding: 5px; text-align: left;} "
                + " th { padding: 5px; text-align: center; }"
                + "</style>");
    }
    
    public static void printCitiesTableFormat(PrintWriter out, List<City> cities) {
        out.println("<h1>Cities</h1>");
        out.println("<table>");
        out.println("<tr><th>Id</th><th>Name</th></tr>");
        cities.stream().map(CityServlet::formatCityToTable).forEach(out::println);
        out.println("</table>");
        
    }
    
    public static String formatCityToTable(City city) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>")
            .append("<td>").append(city.getId()).append("</td>")
            .append("<td>").append(city.getName()).append("</td>")
            .append("<tr>");
        return sb.toString();
    }
}
