<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>

<%@page import="com.danya.javaee.dao.DaoException"%>
<%@page import="java.util.List"%>
<%@page import="com.danya.javaee.domain.Address"%>
<%@page import="com.danya.javaee.Properties"%>
<%@page import="com.danya.javaee.dao.AddressDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%!
    AddressDao addressDao;
    
    public void jspInit() {
        addressDao = (AddressDao) Properties.getDao(Address.class);
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Addresses ~ Logistics Ind.</title>
    </head>
    <body>
        <%
            List<Address> list;
            try {
                list = addressDao.getAll();
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
                    case "cityId" : list.sort((a1, a2) -> a1.getCity().getId().compareTo(a2.getCity().getId()));
                        break;
                    case "cityName" : list.sort((a1, a2) -> a1.getCity().getName().compareTo(a2.getCity().getName()));
                        break;
                }
            } catch (Exception e) { //I would use NullPointerException, but it can be wrapped(right?).
                //If city is null - do nothing and go next
            }
            
        %>
        <h1>Addresses</h1>
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
            <%  for (Address address : list) {%>
                <tr>
                    <td><%=address.getId()%></td>
                    <td><%=address.getName()%></td>
                    <% if (address.getCity() != null) {%>
                        <td><%=address.getCity().getId()%></td>
                        <td><%=address.getCity().getName()%></td>
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
