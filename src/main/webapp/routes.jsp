<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>

<%@page import="com.danya.javaee.domain.City"%>
<%@page import="com.danya.javaee.dao.DaoException"%>
<%@page import="java.util.List"%>
<%@page import="com.danya.javaee.domain.Route"%>
<%@page import="com.danya.javaee.Properties"%>
<%@page import="com.danya.javaee.dao.RouteDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
    RouteDao routeDao;
    
    public void jspInit() {
        routeDao = (RouteDao) Properties.getDao(Route.class);
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Routes ~ Logistics Ind.</title>
    </head>
    <body>
        <%
            List<Route> list;
            try {
                list = routeDao.getAll();
            } catch (DaoException e) {
                throw new ServletException("Can't read from db");
            }
            String sortBy = request.getParameter("sortBy");
//            sortBy = (sortBy == null) ? "" : sortBy;
            try {
                switch ((sortBy == null) ? "" : sortBy) {
                    case "id" : list.sort((a1, a2) -> a1.getId().compareTo(a2.getId()));
                        break;
                    case "name" : list.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));
                        break;
                }
            } catch (Exception e) { //I would use NullPointerException, but it can be wrapped(right?).
                //If city is null - do nothing and go next
            }
            
        %>
        <h1>Routes</h1>
        <table>
            <tr>
                <th rowspan="2">Id</th>
                <th rowspan="2">Name</th>
                <th colspan="2">City</th>
            </tr>
            <tr>
                <th>Id</th>
                <th>Name</th>
            </tr>
            <%  for (Route route : list) {%>
                <tr>
                    <% int citySize = route.getCities().size()+1; %>
                    <td rowspan="<%=citySize%>"><%=route.getId()%></td>
                    <td rowspan="<%=citySize%>"><%=route.getName()%></td>
                    <% for (City city : route.getCities()) {%>
                        <tr>
                            <td><%=city.getId()%></td>
                            <td><%=city.getName()%></td>
                        </tr>
                    <%}%>
                </tr>
            <%}%>
        </table>
    </body>
    <style type="text/css">
        table, td, th { border: 1px solid black; border-collapse: collapse;}
        td { padding: 5px; text-align: left;}
        th { padding: 5px; text-align: center;}
    </style>
</html>
