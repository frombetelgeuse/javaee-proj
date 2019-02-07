/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import com.danya.javaee.Properties;
import com.danya.javaee.dao.AddressDao;
import com.danya.javaee.dao.DaoException;
import com.danya.javaee.domain.Address;
import com.danya.javaee.util.UrlUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
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
@WebServlet("/addresses")
public class AddressServlet extends HttpServlet {
    
    private AddressDao addressDao;
    
    @Override
    public void init(ServletConfig sc) {
        addressDao = (AddressDao) Properties.getDao(Address.class);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        PrintWriter out = resp.getWriter();
        List<Address> addresses;
        try {
            addresses = addressDao.getAll();
        } catch (DaoException e) {
            throw new ServletException("Can't read from db", e);
        }
        Map<String, String> params = UrlUtils.parseUrlParams(req.getQueryString());
        switch (params.getOrDefault("sortBy", "")) {
            case "id" : addresses.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                    break;
            case "name" : addresses.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));
                    break;
            case "city" : addresses.sort((a1, a2) -> a1.getCity().getName().compareTo(a2.getCity().getName()));
                    break;
        }
        printAddressesTableFormat(out, addresses);
        
        out.println(
                "<style type=\"text/css\">\n" +
                "   table, td, th { border: 1px solid black; border-collapse: collapse;} "
                + " td { padding: 5px; text-align: left;} "
                + " th { padding: 5px; text-align: center; }"
                + "</style>");
    }
    
    public static void printAddressesTableFormat(PrintWriter out, List<Address> cities) {
        out.println("<h1>Addresses</h1>");
        out.println("<table>");
        out.println("<tr><th rowspan=\"2\">Id</th><th rowspan=\"2\">Name</th><th colspan=\"2\">City</th></tr><tr><th>Id</th><th>Name</th></tr>");
        cities.stream().map(AddressServlet::formatAddressToTable).forEach(out::println);
        out.println("</table>");
        
    }
    
    public static String formatAddressToTable(Address address) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>")
            .append("<td>").append(address.getId()).append("</td>")
            .append("<td>").append(address.getName()).append("</td>")
            .append("<td>").append(address.getCity().getId()).append("</td>")
            .append("<td>").append(address.getCity().getName()).append("</td>")
            .append("<tr>");
        return sb.toString();
    }
}
