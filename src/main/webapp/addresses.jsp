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
        <link href="//fonts.googleapis.com/css?family=Raleway:400,300,600" rel="stylesheet" type="text/css">
        
        <link rel="stylesheet" type="text/css" href="css/normalize.css">
        <link rel="stylesheet" type="text/css" href="css/skeleton.css">
    </head>
    <body>
        <div class="container">
            <br/>
            <h1>Addresses</h1>
            
            
            <c:if test="${addressesIdSelected != null && !addressesIdSelected.isEmpty()}">
                <form action="addresses" method="get">
                    <h4>Selected</h4>
                    <p>Selected ${addressesIdSelected.size()} elements</p>
                    <input class="button-primary" type="submit" name="selectedAction" value="deleteAll"/>
                    <input class="button-primary" type="submit" name="selectedAction" value="clear"/>
                </form>
            </c:if>
            
            
            <h4>Table</h4>
            <table class="u-full-width">
                <tr>
                    <th><a href="?sortBy=Id">Id</a></th>
                    <th><a href="?sortBy=Name">Name</a></th>
                    <th><a href="?sortBy=CityId">City Id</a></th>
                    <th><a href="?sortBy=CityName">City Name</a></th>
                    <th></th>
                </tr>
                </form>
                <c:forEach var="address" items="${list}">
                    <tr>
                        <form action="addresses" method="post">
                            <td>${address.id}</td>
                            <input type="hidden" name="id" value="${address.id}"/>
                            <td>${address.name}</td>
                            <input type="hidden" name="name" value="${address.name}"/>
                            <c:choose>
                                <c:when test="${address.city != null}">
                                    <td>${address.city.id}</td>
                                    <input type="hidden" name="city.id" value="${address.city.id}"/>
                                    <td>${address.city.name}</td>
                                    <input type="hidden" name="city.name" value="${address.city.name}"/>
                                </c:when>
                                <c:otherwise>
                                    <td></td>
                                    <td></td>
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <input class="button-primary" type="submit" name="beanAction" value="edit"/>
                                <input class="button-primary" type="submit" name="beanAction" value="delete"/>
                                <c:choose>
                                    <c:when test="${!addressesIdSelected.contains(address.getId())}">
                                        <input type="submit" name="beanAction" value="addToSelect"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="submit" name="beanAction" value="removeFromSelect"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </form>
                    </tr>
                </c:forEach>
            </table>
            <form action="addresses" method="get">
                <input class="button-primary" type="submit" name="beanAction" value="new"/>
            </form>
            
            
            <form action="addresses" method="post">
                <h4>Editing</h4>
                <div class="row">
                    <div class="two columns">
                        <label for="id">Id:</label>
                        <input class="u-full-width" placeholder="Id" type="text" name="id" readonly value="${addressBean.id}"/>
                    </div>
                    <div class="ten columns">
                        <label for="name">Name:</label>
                        <input class="u-full-width" placeholder="Name" type="text" name="name" value="${addressBean.name}"/>
                    </div>
                </div>
                <div class="row">
                    <div class="two columns">
                        <label for="city.id">City Id:</label>
                        <input class="u-full-width" placeholder="Id" type="text" name="city.id" readonly value="${addressBean.city.id}"/>
                    </div>
                    <div class="ten columns">
                        <label for="city.name">City Name:</label>
                        <input class="u-full-width" placeholder="Name" type="text" name="city.name" value="${addressBean.city.name}"/>
                    </div>
                </div>
                <input class="button-primary" type="submit" name="beanAction" value="save" />
            </form>
            <br/>
            <center>
                <h5>Made by Danya</h5>
            </center>
        </div>
    </body>
</html>
