<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>

<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Set"%>
<%@page import="com.danya.javaee.util.SessionUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.danya.javaee.dao.CityDao"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.danya.javaee.domain.City"%>
<%@page import="com.danya.javaee.dao.DaoException"%>
<%@page import="java.util.List"%>
<%@page import="com.danya.javaee.domain.Route"%>
<%@page import="com.danya.javaee.Properties"%>
<%@page import="com.danya.javaee.dao.RouteDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
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
%>
<jsp:useBean id="routeBean" class="com.danya.javaee.domain.Route" scope="request"/>
<jsp:setProperty name="routeBean" property="*"/>
<%
    //Populate bean with dependency
    List<City> cities = new ArrayList();
    for (int i = 0; true; i++) {
        String cityIdUnparsed = request.getParameter("city."+i+".id");
        Integer cityId = (cityIdUnparsed==null || cityIdUnparsed.isEmpty()) ? null : Integer.valueOf((cityIdUnparsed));
        String cityName = request.getParameter("city."+i+".name");
        if (cityId != null || (!"".equals(cityName) && cityName != null)) {
            City city = new City();
            city.setId(cityId);
            city.setName(cityName);
            cities.add(city);
        } else {
            break;
        }
    }
    routeBean.setCities(cities);
%>
<%
//    HANDLE REQUESTS
    String beanAction = request.getParameter("beanAction");
    if ("save".equals(beanAction)) {
        if (routeBean.getId() == null) {
            cities = routeBean.getCities();
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
        }
    } else if ("delete".equals(beanAction)) {
        routeDao.delete(routeBean);
        SessionUtils.getSet(request.getSession(), "routes.selected").remove(routeBean.getId());
        routeBean.setId(null);
    } else if ("addToSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "routes.selected").add(routeBean.getId());
    } else if ("removeFromSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "routes.selected").remove(routeBean.getId());
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
%>
<%
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
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Routes ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Routes</h1>
        <%if (routesIdSelected != null && !routesIdSelected.isEmpty()) {%>
            <form action="routes.jsp" method="post">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected <%=routesIdSelected.size()%> elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        <%}%>
        <br/>
        <table class="table-bord">
            <form action="routes.jsp" method="post">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th class="bord">CityId</th>
                    <th class="bord">CityName</th>
                    <th></th>
                </tr>
            </form>
            <%  for (Route route : list) {
                String selected = (routesIdSelected.contains(route.getId())) ? " selected" : "";
                int citySize = route.getCities().size();
                citySize = (citySize==0) ? 1 : citySize;
                %>
                <tr>
                    <form action="routes.jsp" method="post">
                        <td rowspan="<%=citySize%>" class="bord<%=selected%>"><%=route.getId()%></td>
                        <input type="hidden" name="id" value="<%=route.getId()%>"/>
                        <td rowspan="<%=citySize%>" class="bord<%=selected%>"><%=route.getName()%></td>
                        <input type="hidden" name="name" value="<%=route.getName()%>"/>
                        <%if (!route.getCities().isEmpty()) {%>
                            <td class="bord<%=selected%>"><%=route.getCities().get(0).getId()%></td>
                            <input type="hidden" name="city.0.id" value="<%=route.getCities().get(0).getId()%>"/>
                            <td class="bord<%=selected%>"><%=route.getCities().get(0).getName()%></td>
                            <input type="hidden" name="city.0.name" value="<%=route.getCities().get(0).getName()%>"/>
                        <%} else {%>
                            <td class="bord<%=selected%>"></td>
                            <td class="bord<%=selected%>"></td>
                        <%}%>
                        <td rowspan="<%=citySize%>"><input type="submit" name="beanAction" value="edit"/></td>
                        <td rowspan="<%=citySize%>"><input type="submit" name="beanAction" value="delete"/></td>
                        <%if (selected.length()==0) {%>
                            <td rowspan="<%=citySize%>"><input type="submit" name="beanAction" value="addToSelect"/></td>
                        <%} else {%>
                            <td rowspan="<%=citySize%>"><input type="submit" name="beanAction" value="removeFromSelect"/></td>
                        <%}%>
                        <%for (int i = 1; i < route.getCities().size(); i++) {%>
                            <tr>
                                <td class="bord<%=selected%>"><%=route.getCities().get(i).getId()%></td>
                                <input type="hidden" name="city.<%=i%>.id" value="<%=route.getCities().get(i).getId()%>"/>
                                <td class="bord<%=selected%>"><%=route.getCities().get(i).getName()%></td>
                                <input type="hidden" name="city.<%=i%>.name" value="<%=route.getCities().get(i).getName()%>"/>
                            </tr>
                        <%}%>
                    </form>
                </tr>
            <%}%>
            <tr>
                <form action="routes.jsp" method="post">
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <fieldset>
            <legend>Editing</legend>
            <form action="routes.jsp" method="post">
                <table id="editing">
                    <tr>
                        <td class="to-right">Id:</td>
                        <td><input size="3" type="text" name="id" readonly value="${routeBean.id}"/></td>
                    </tr>
                    <tr>
                        <td class="to-right">Name:</td>
                        <td><input type="text" name="name" value="${routeBean.name}"/></td>
                    </tr>
                    <%for (int i = 0; i < routeBean.getCities().size(); i++) {%>
                        <tr id="city#<%=i%>.id">
                            <td class="to-right">City#<%=i%> Id:</td>
                            <td><input size="3" type="text" name="city.<%=i%>.id" readonly value="<%=routeBean.getCities().get(i).getId()%>"/></td>
                        </tr>
                        <tr id="city#<%=i%>.name">
                            <td class="to-right">City#<%=i%> Name:</td>
                            <td><input type="text" name="city.<%=i%>.name" value="<%=routeBean.getCities().get(i).getName()%>"/></td>
                        </tr>
                    <%}%>
                </table>
                <input type="submit" name="beanAction" value="save" />
            </form>
                <br/>
            <button onclick="addRow()">Add City</button>
            <button onclick="delRow()">Delete City</button>
        </fieldset>
        <script>
            var num = <%=routeBean.getCities().size()%>
            function addRow() {
                var leftPartId = document.createElement("td");
                leftPartId.className += "to-right";
                leftPartId.appendChild(document.createTextNode("City#"+num+" Id:"));
                var inpId = document.createElement("input");
                inpId.size = "3";
                inpId.type = "text";
                inpId.name = "city."+(num)+".id";
                var rightPartId = document.createElement("td");
                rightPartId.appendChild(inpId);
                var trId = document.createElement("tr");
                trId.id = "city#"+num+".id";
                trId.appendChild(leftPartId);
                trId.appendChild(rightPartId);
                document.getElementById("editing").appendChild(trId);
                var leftPartName = document.createElement("td");
                leftPartName.className += "to-right";
                leftPartName.appendChild(document.createTextNode("City#"+num+" Name:"));
                var inpName = document.createElement("input");
                inpName.size = "3";
                inpName.type = "text";
                inpName.name = "city."+(num)+".name";
                var rightPartName = document.createElement("td");
                rightPartName.appendChild(inpName);
                var trName = document.createElement("tr");
                trName.id = "city#"+num+".name";
                trName.appendChild(leftPartName);
                trName.appendChild(rightPartName);
                document.getElementById("editing").appendChild(trName);
                num++;
            }
            function delRow() {
                num--;
                var trId = document.getElementById("city#"+(num)+".id");
                var trName = document.getElementById("city#"+(num)+".name");
                trName.parentNode.removeChild(trName);
                trId.parentNode.removeChild(trId);
            }
        </script>
<%@include file="parts/footer.jsp" %>
<%
    context.close();
%>