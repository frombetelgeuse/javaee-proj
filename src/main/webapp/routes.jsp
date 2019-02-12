<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

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
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Routes ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Routes</h1>
        <c:if test="${routesIdSelected != null && !routesIdSelected.isEmpty()}">
            <form action="routes" method="get">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected ${routesIdSelected.size()} elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        </c:if>
        <br/>
        <table class="table-bord">
            <form action="routes" method="get">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th class="bord">CityId</th>
                    <th class="bord">CityName</th>
                    <th></th>
                </tr>
            </form>
            <c:forEach var="route" items="${list}">
                <c:choose>
                    <c:when test="${routesIdSelected.contains(route.getId())}">
                        <c:set var="selected" value="selected" scope="request"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="selected" value="" scope="request"/>
                    </c:otherwise>
                </c:choose>
                <c:set var ="listSize" value="${route.cities.size()}" scope="request"/>
                <c:if test="${listSize==0}">
                    <c:set var="listSize" value="1" scope="request"/>
                </c:if>
                <tr>
                    <form action="routes" method="get">
                        <td rowspan="${listSize}" class="bord ${selected}">${route.id}</td>
                        <input type="hidden" name="id" value="${route.id}"/>
                        <td rowspan="${listSize}" class="bord ${selected}">${route.name}</td>
                        <input type="hidden" name="name" value="${route.name}"/>
                        <c:choose>
                            <c:when test="${!route.cities.isEmpty()}">
                                <td class="bord ${selected}">${route.cities.get(0).id}</td>
                                <input type="hidden" name="city.0.id" value="${route.cities.get(0).id}"/>
                                <td class="bord ${selected}">${route.cities.get(0).name}</td>
                                <input type="hidden" name="city.0.name" value="${route.cities.get(0).name}"/>
                            </c:when>
                            <c:otherwise>
                                <td class="bord ${selected}"></td>
                                <td class="bord ${selected}"></td>
                            </c:otherwise>
                        </c:choose>
                        <td rowspan="${listSize}"><input type="submit" name="beanAction" value="edit"/></td>
                        <td rowspan="${listSize}"><input type="submit" name="beanAction" value="delete"/></td>
                        <c:choose>
                            <c:when test="${selected.isEmpty()}">
                                <td rowspan="${listSize}"><input type="submit" name="beanAction" value="addToSelect"/></td>
                            </c:when>
                            <c:otherwise>
                                <td rowspan="${listSize}"><input type="submit" name="beanAction" value="removeFromSelect"/></td>
                            </c:otherwise>
                        </c:choose>
                        <c:if test="${!route.cities.isEmpty()}">
                            <c:forEach var="city" items="${route.cities.subList(1,route.cities.size())}" varStatus="loop">
                                <tr>
                                    <td class="bord ${selected}">${city.id}</td>
                                    <input type="hidden" name="city.${loop.index}.id" value="${city.id}"/>
                                    <td class="bord ${selected}">${city.name}</td>
                                    <input type="hidden" name="city.${loop.index}.name" value="${city.name}"/>
                                </tr>
                            </c:forEach>
                        </c:if>
                    </form>
                </tr>
            </c:forEach>
            <tr>
                <form action="routes" method="get">
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <fieldset>
            <legend>Editing</legend>
            <form action="routes" method="post">
                <table id="editing">
                    <tr>
                        <td class="to-right">Id:</td>
                        <td><input size="3" type="text" name="id" readonly value="${routeBean.id}"/></td>
                    </tr>
                    <tr>
                        <td class="to-right">Name:</td>
                        <td><input type="text" name="name" value="${routeBean.name}"/></td>
                    </tr>
                     <c:forEach var="city" items="${routeBean.cities}" varStatus="loop"> <!--...size()-1-->
                        <tr id="city#${loop.index}.id">
                            <td class="to-right">City#${loop.index} Id:</td>
                            <td><input size="3" type="text" name="city.${loop.index}.id" readonly value="${city.id}"/></td>
                        </tr>
                        <tr id="city#${loop.index}.name">
                            <td class="to-right">City#${loop.index} Name:</td>
                            <td><input type="text" name="city.${loop.index}.name" value="${city.name}"/></td>
                        </tr>
                    </c:forEach>
                </table>
                <input type="submit" name="beanAction" value="save" />
            </form>
                <br/>
            <button onclick="addRow()">Add City</button>
            <button onclick="delRow()">Delete City</button>
        </fieldset>
        <script>
            var num = ${routeBean.cities.size()}
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