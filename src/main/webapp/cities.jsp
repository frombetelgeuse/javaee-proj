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
        <title>Cities ~ Logistics Ind.</title>
    </head>
    <body>
        <h1>Cities</h1>
        <c:if test = "${citiesIdSelected != null && !citiesIdSelected.isEmpty()}">
            <form action="cities" method="get">
                <fieldset>
                    <legend>Selected</legend>
                    <p>Selected ${citiesIdSelected.size()} elements</p>
                    <input type="submit" name="selectedAction" value="deleteAll"/>
                    <input type="submit" name="selectedAction" value="clear"/>
                </fieldset>
            </form>
        </c:if>
        <br/>
        <table class="table-bord">
            <form action="cities" method="get">
                <tr>
                    <th class="bord"><input type="submit" name="sortBy" value="Id"></th>
                    <th class="bord"><input type="submit" name="sortBy" value="Name"/></th>
                    <th></th>
                </tr>
            </form>
            <c:forEach var="city" items="${list}">
                <c:choose>
                    <c:when test="${citiesIdSelected.contains(city.getId())}">
                        <c:set var="selected" value="selected" scope="request"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="selected" value="" scope="request"/>
                    </c:otherwise>
                </c:choose>
                <tr>
                    <form action="cities" method="get">
                        <td class="bord ${selected}">${city.id}</td>
                        <input type="hidden" name="id" value="${city.id}"/>
                        <td class="bord ${selected}">${city.name}</td>
                        <input type="hidden" name="name" value="${city.name}"/>
                        <td><input type="submit" name="beanAction" value="edit"/></td>
                        <td><input type="submit" name="beanAction" value="delete"/></td>
                        <c:choose>
                            <c:when test="${!citiesIdSelected.contains(city.getId())}">
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
                <form action="cities" method="get">
                    <th><input type="submit" name="beanAction" value="+"/></th>
                </form>
            </tr>
        </table>
        <br/>
        <form action="cities" method="post">
            <fieldset>
                <legend>Editing</legend>
                <table>
                    <tr>
                        <td style="text-align:right;">Id:</td>
                        <td><input size="3" type="text" name="id" readonly value="${cityBean.id}"/></td>
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
