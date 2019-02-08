<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>

<%@page import="com.danya.javaee.util.SessionUtils"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Collections"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.Comparator"%>
<%@page import="com.danya.javaee.dao.DaoException"%>
<%@page import="java.util.List"%>
<%@page import="com.danya.javaee.domain.City"%>
<%@page import="com.danya.javaee.Properties"%>
<%@page import="com.danya.javaee.dao.CityDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
//    PREPARE DB
    Connection context;
    CityDao cityDao;
    try {
        context = (Connection) Properties.getDaoFactory().getContext();
        cityDao = (CityDao) Properties.getDaoFactory().getDao(context, City.class);
    } catch (DaoException e) {
        throw new ServletException("Cannott connect to db", e);
    }
%>
<jsp:useBean id="cityBean" class="com.danya.javaee.domain.City" scope="request"/>
<jsp:setProperty name="cityBean" property="*"/>
<%
//    HANDLE REQUESTS
    String beanAction = request.getParameter("beanAction");
    if ("save".equals(beanAction)) {
        if (cityBean.getId() == null) {
            Integer id = cityDao.persist(cityBean).getId();
            cityBean.setId(id);
        } else {
            cityDao.update(cityBean);
        }
    } else if ("delete".equals(beanAction)) {
        cityDao.delete(cityBean);
        SessionUtils.getSet(request.getSession(), "cities.selected").remove(cityBean.getId());
        cityBean.setId(null);
    } else if ("addToSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "cities.selected").add(cityBean.getId());
    } else if ("removeFromSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "cities.selected").remove(cityBean.getId());
    }
    
    String selectedAction = request.getParameter("selectedAction");
    if ("deleteAll".equals(selectedAction)) {
        Set<Integer> citiesIdSelected = SessionUtils.getSet(request.getSession(), "cities.selected");
        for (Integer id : citiesIdSelected) {
            City toDelete = cityDao.getEntityById(id);
            if (toDelete != null) {
                cityDao.delete(toDelete);
            }
        }
        citiesIdSelected.clear();
    } else if ("clear".equals(selectedAction)) {
        SessionUtils.getSet(request.getSession(), "cities.selected").clear();
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
%>
<%
//    PREPARE DATA
    List<City> list;
    try {
        list = cityDao.getAll();
    } catch (DaoException e) {
        throw new ServletException("Can't read from db");
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
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Cities ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Cities</h1>
        <%if (citiesIdSelected != null && !citiesIdSelected.isEmpty()) {%>
            <form action="cities.jsp" method="post">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected <%=citiesIdSelected.size()%> elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        <%}%>
        <br/>
        <table class="table-bord">
            <form action="cities.jsp" method="post">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th></th>
                </tr>
            </form>
            <%  for (City city : list) {
                String selected = (citiesIdSelected.contains(city.getId())) ? " selected" : "";
                %>
                <tr>
                    <form action="cities.jsp" method="post">
                        <td class="bord<%=selected%>"><%=city.getId()%></td>
                        <input type="hidden" name="id" value="<%=city.getId()%>"/>
                        <td class="bord<%=selected%>"><%=city.getName()%></td>
                        <input type="hidden" name="name" value="<%=city.getName()%>"/>
                        <td><input type="submit" name="beanAction" value="edit"/></td>
                        <td><input type="submit" name="beanAction" value="delete"/></td>
                        <%if (selected.length()==0) {%>
                            <td><input type="submit" name="beanAction" value="addToSelect"/></td>
                        <%} else {%>
                            <td><input type="submit" name="beanAction" value="removeFromSelect"/></td>
                        <%}%>
                    </form>
                </tr>
            <%}%>
            <tr>
                <form action="cities.jsp" method="post">
                    <input type="hidden" name="id" value=""/>
                    <input type="hidden" name="name" value=""/>
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <form action="cities.jsp" method="post">
            <fieldset>
                <legend>Editing</legend>
                <table>
                    <tr>
                        <td style="text-align:right;">Id:</td>
                        <td><input type="text" name="id" readonly value="${cityBean.id}"/></td>
                    </tr>
                    <tr>
                        <td style="text-align:right;">Name:</td>
                        <td><input type="text" name="name" value="${cityBean.name}"/></td>
                    </tr>
                </table>
                <input type="submit" name="beanAction" value="save" />
            </fieldset>
        </form>
<%@ include file="parts/footer.jsp" %>
<%
    context.close();
%>
