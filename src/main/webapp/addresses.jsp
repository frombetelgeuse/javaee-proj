<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>

<%@page import="com.danya.javaee.domain.Address"%>
<%@page import="com.danya.javaee.dao.CityDao"%>
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
<%@page import="com.danya.javaee.dao.AddressDao"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
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
%>
<jsp:useBean id="addressBean" class="com.danya.javaee.domain.Address" scope="request"/>
<jsp:setProperty name="addressBean" property="*"/>
<%
    String cityIdUnparsed = request.getParameter("city.id");
    Integer cityId = (cityIdUnparsed==null || cityIdUnparsed.isEmpty()) ? null : Integer.valueOf((cityIdUnparsed));
    String cityName = request.getParameter("city.name");
    if (cityId != null || cityName != null) {
        City city = new City();
        city.setId(cityId);
        city.setName(cityName);
        addressBean.setCity(city);
    }
%>
<%
//    HANDLE REQUESTS
    String beanAction = request.getParameter("beanAction");
    if ("save".equals(beanAction)) {
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
        }
    } else if ("delete".equals(beanAction)) {
        addressDao.delete(addressBean);
        SessionUtils.getSet(request.getSession(), "addresses.selected").remove(addressBean.getId());
        addressBean.setId(null);
    } else if ("addToSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "addresses.selected").add(addressBean.getId());
    } else if ("removeFromSelect".equals(beanAction)) {
        SessionUtils.getSet(request.getSession(), "addresses.selected").remove(addressBean.getId());
    }
    
    String selectedAction = request.getParameter("selectedAction");
    if ("deleteAll".equals(selectedAction)) {
        Set<Integer> addressesIdSelected = SessionUtils.getSet(request.getSession(), "addresses.selected");
        for (Integer id : addressesIdSelected) {
            Address toDelete = addressDao.getEntityById(id);
            if (toDelete != null) {
                addressDao.delete(toDelete);
            }
        }
        addressesIdSelected.clear();
    } else if ("clear".equals(selectedAction)) {
        SessionUtils.getSet(request.getSession(), "addresses.selected").clear();
    }
    
    String sortByAction = request.getParameter("sortBy");
    if (sortByAction != null) {
        sortByAction = sortByAction.toLowerCase();
        if (sortByAction.equals(request.getSession().getAttribute("addresses.sortBy"))) {
            boolean rev = SessionUtils.getBoolean(request.getSession(), "addresses.reverse");
            request.getSession().setAttribute("address.reverse", !rev);
        }
        request.getSession().setAttribute("addresses.sortBy", sortByAction);
    }
%>
<%
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
            // may fall somewhere here
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
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Addresses ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Addresses</h1>
        <%if (addressesIdSelected != null && !addressesIdSelected.isEmpty()) {%>
            <form action="addresses.jsp" method="post">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected <%=addressesIdSelected.size()%> elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        <%}%>
        <br/>
        <table class="table-bord">
            <form action="addresses.jsp" method="post">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="CityId"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="CityName"/></th>
                    <th></th>
                </tr>
            </form>
            <%  for (Address address : list) {
                String selected = (addressesIdSelected.contains(address.getId())) ? " selected" : "";
                %>
                <tr>
                    <form action="addresses.jsp" method="post">
                        <td class="bord<%=selected%>"><%=address.getId()%></td>
                        <input type="hidden" name="id" value="<%=address.getId()%>"/>
                        <td class="bord<%=selected%>"><%=address.getName()%></td>
                        <input type="hidden" name="name" value="<%=address.getName()%>"/>
                        <% if (address.getCity() != null) {%>
                            <td class="bord<%=selected%>"><%=address.getCity().getId()%></td>
                            <input type="hidden" name="city.id" value="<%=address.getCity().getId()%>"/>
                            <td class="bord<%=selected%>"><%=address.getCity().getName()%></td>
                            <input type="hidden" name="city.name" value="<%=address.getCity().getName()%>"/>
                        <%} else {%>
                            <td></td>
                            <td></td>
                        <%}%>
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
                <form action="addresses.jsp" method="post">
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <form action="addresses.jsp" method="post">
            <fieldset>
                <legend>Editing</legend>
                <table>
                    <tr>
                        <td class="to-right">Id:</td>
                        <td><input size="3" type="text" name="id" readonly value="${addressBean.id}"/></td>
                    </tr>
                    <tr>
                        <td class="to-right">Name:</td>
                        <td><input type="text" name="name" value="${addressBean.name}"/></td>
                    </tr>
                    <tr>
                        <td class="to-right">City Id:</td>
                        <td><input size="3" type="text" name="city.id" readonly value="${addressBean.city.id}"/></td>
                    </tr>
                    <tr>
                        <td class="to-right">City Name:</td>
                        <td><input type="text" name="city.name" value="${addressBean.city.name}"/></td>
                </table>
                <input type="submit" name="beanAction" value="save" />
            </fieldset>
        </form>
<%@ include file="parts/footer.jsp" %>
<%
    context.close();
%>
