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
        <link href="//fonts.googleapis.com/css?family=Raleway:400,300,600" rel="stylesheet" type="text/css">
        
        <link rel="stylesheet" type="text/css" href="css/normalize.css">
        <link rel="stylesheet" type="text/css" href="css/skeleton.css">
    </head>
    <body>
        <div class="container">
            <br/>
            <h1>Cities</h1>
            
            
            <c:if test = "${citiesIdSelected != null && !citiesIdSelected.isEmpty()}">
                <form action="cities" method="get">
                    <h4>Selected</h4>
                    <p>Selected ${citiesIdSelected.size()} element(s)</p>
                    <input class="button-primary" type="submit" name="selectedAction" value="deleteAll"/>
                    <input class="button-primary" type="submit" name="selectedAction" value="clear"/>
                </form>
            </c:if>
            
            
            <h4>Table</h4>
            <table class="u-full-width">
                <thead>
                    <tr>
                        <th><a href="?sortBy=Id">Id</a></th>
                        <th><a href="?sortBy=Name">Name</a></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="city" items="${list}">
                        <tr>
                            <form action="cities" method="post">
                                <td>${city.id}</td>
                                <input type="hidden" name="id" value="${city.id}"/>
                                <td>${city.name}</td>
                                <input type="hidden" name="name" value="${city.name}"/>
                                <td>
                                    <input class="button-primary" type="submit" name="beanAction" value="edit"/>
                                    <input class="button-primary" type="submit" name="beanAction" value="delete"/>
                                    <c:choose>
                                        <c:when test="${!citiesIdSelected.contains(city.getId())}">
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
                </tbody>
            </table>
            <form action="cities" method="get">
                <input class="button-primary" type="submit" name="beanAction" value="New"/>
            </form>
            
            
            <form action="cities" method="post">
                <h4>Editing</h4>
                <div class="row">
                    <div class="two columns">
                        <label for="id">Id:</label>
                        <input class="u-full-width" placeholder="Id" type="text" name="id" readonly value="${cityBean.id}"/>
                    </div>
                    <div class="ten columns">
                        <label for="name">Name:</label>
                        <input class="u-full-width" placeholder="Name" type="text" name="name" value="${cityBean.name}"/>
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
