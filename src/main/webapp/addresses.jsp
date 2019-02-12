<%-- 
    Document   : cities
    Created on : Feb 7, 2019, 2:00:32 PM
    Author     : danya
--%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Addresses ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Addresses</h1>
        <c:if test="${addressesIdSelected != null && !addressesIdSelected.isEmpty()}">
            <form action="addresses" method="get">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected ${addressesIdSelected.size()} elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        </c:if>
        <br/>
        <table class="table-bord">
            <form action="addresses" method="get">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="CityId"/></th>
                    <th class="bord"><input type="submit" name="sortBy" value="CityName"/></th>
                    <th></th>
                </tr>
            </form>
            <c:forEach var="address" items="${list}">
                <c:choose>
                    <c:when test="${addressesIdSelected.contains(address.getId())}">
                        <c:set var="selected" value="selected" scope="request"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="selected" value="" scope="request"/>
                    </c:otherwise>
                </c:choose>
                <tr>
                    <form action="addresses" method="get">
                        <td class="bord ${selected}">${address.id}</td>
                        <input type="hidden" name="id" value="${address.id}"/>
                        <td class="bord ${selected}">${address.name}</td>
                        <input type="hidden" name="name" value="${address.name}"/>
                        <c:choose>
                            <c:when test="${address.city != null}">
                                <td class="bord ${selected}">${address.city.id}</td>
                                <input type="hidden" name="city.id" value="${address.city.id}"/>
                                <td class="bord ${selected}">${address.city.name}</td>
                                <input type="hidden" name="city.name" value="${address.city.name}"/>
                            </c:when>
                            <c:otherwise>
                                <td class="bord ${selected}"></td>
                                <td class="bord ${selected}"></td>
                            </c:otherwise>
                        </c:choose>
                        <td><input type="submit" name="beanAction" value="edit"/></td>
                        <td><input type="submit" name="beanAction" value="delete"/></td>
                        <c:choose>
                            <c:when test="${!addressesIdSelected.contains(address.getId())}">
                                <td><input type="submit" name="beanAction" value="addToSelect"/></td>
                            </c:when>
                            <c:otherwise>
                                <td><input type="submit" name="beanAction" value="removeFromSelect"/></td>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </tr>
            </c:forEach>
            <tr>
                <form action="addresses" method="get">
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <form action="addresses" method="post">
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
