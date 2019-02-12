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
        <title>Routes ~ Logistics Ind.</title>
        <link href="//fonts.googleapis.com/css?family=Raleway:400,300,600" rel="stylesheet" type="text/css">
        
        <link rel="stylesheet" type="text/css" href="css/normalize.css">
        <link rel="stylesheet" type="text/css" href="css/skeleton.css">
    </head>
    <body>
        <div class="container">
            <br/>
            <h1>Routes</h1>
            
            
            <c:if test="${routesIdSelected != null && !routesIdSelected.isEmpty()}">
                <form action="routes" method="get">
                    <h4>Selected</h4>
                    <p>Selected ${routesIdSelected.size()} elements</p>
                    <input class="button-primary" type="submit" name="selectedAction" value="deleteAll"/>
                    <input class="button-primary" type="submit" name="selectedAction" value="clear"/>
                </form>
            </c:if>
            
            
            <h4>Table</h4>
            <table class="u-full-width">
                <tr>
                    <th><a href="?sortBy=Id">Id</a></th>
                    <th><a href="?sortBy=Name">Name</a></th>
                    <th>City Id</th>
                    <th>City Name</th>
                    <th></th>
                </tr>
                <c:forEach var="route" items="${list}">
                    <c:set var ="listSize" value="${route.cities.size()}" scope="request"/>
                    <c:if test="${listSize==0}">
                        <c:set var="listSize" value="1" scope="request"/>
                    </c:if>
                    <tr>
                        <form action="routes" method="post">
                            <td rowspan="${listSize}">${route.id}</td>
                            <input type="hidden" name="id" value="${route.id}"/>
                            <td rowspan="${listSize}">${route.name}</td>
                            <input type="hidden" name="name" value="${route.name}"/>
                            <c:choose>
                                <c:when test="${!route.cities.isEmpty()}">
                                    <td>${route.cities.get(0).id}</td>
                                    <input type="hidden" name="city.0.id" value="${route.cities.get(0).id}"/>
                                    <td>${route.cities.get(0).name}</td>
                                    <input type="hidden" name="city.0.name" value="${route.cities.get(0).name}"/>
                                </c:when>
                                <c:otherwise>
                                    <td></td>
                                    <td></td>
                                </c:otherwise>
                            </c:choose>
                            <td rowspan="${listSize}">
                                <input class="button-primary" type="submit" name="beanAction" value="edit"/>
                                <input class="button-primary" type="submit" name="beanAction" value="delete"/>
                                <c:choose>
                                    <c:when test="${!routesIdSelected.contains(route.getId())}">
                                        <input type="submit" name="beanAction" value="addToSelect"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="submit" name="beanAction" value="removeFromSelect"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <c:if test="${!route.cities.isEmpty()}">
                                <c:forEach var="city" items="${route.cities.subList(1,route.cities.size())}" varStatus="loop">
                                    <tr>
                                        <td>${city.id}</td>
                                        <input type="hidden" name="city.${loop.index+1}.id" value="${city.id}"/>
                                        <td>${city.name}</td>
                                        <input type="hidden" name="city.${loop.index+1}.name" value="${city.name}"/>
                                    </tr>
                                </c:forEach>
                            </c:if>
                        </form>
                    </tr>
                </c:forEach>
            </table>
            <form action="routes" method="get">
                <input class="button-primary" type="submit" name="beanAction" value="new"/>
            </form>
            
            
            <form action="routes" method="post">
                <h4>Editing</h4>
                <div id="editor">
                    <div class="row">
                        <div class="two columns">
                            <label for="id">Id:</label>
                            <input class="u-full-width" placeholder="Null" type="text" name="id" readonly value="${routeBean.id}"/>
                        </div>
                        <div class="ten columns">
                            <label for="name">Name:</label>
                            <input class="u-full-width" placeholder="Name" type="text" name="name" value="${routeBean.name}"/>
                        </div>
                    </div>
                    <c:forEach var="city" items="${routeBean.cities}" varStatus="loop">
                        <div id="city.${loop.index}" class="row">
                            <div id="city.${loop.index}.id" class="two columns">
                                <label for="id">City#${loop.index} Id:</label>
                                <input class="u-full-width" placeholder="Id" type="text" name="city.${loop.index}.id" readonly value="${city.id}"/>
                            </div>
                            <div id="city.${loop.index}.name" class="ten columns">
                                <label for="name">City#${loop.index} Name:</label>
                                <input class="u-full-width" placeholder="Name" type="text" name="city.${loop.index}.name" value="${city.name}"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <input class="button-primary" type="submit" name="beanAction" value="save" />
            </form>
            <button onclick="addRow()">Add City</button>
            <button onclick="delRow()">Delete City</button>
            <br/>
            <center>
                <h5>Made by Danya</h5>
            </center>
        </div>
    </body>
    <script>
        var num = ${routeBean.cities.size()}
        function addRow() {
            var partLabelId = document.createElement("label");
            partLabelId.for = "id";
            partLabelId.appendChild(document.createTextNode("City#"+num+" Id:"));
            var partInputId = document.createElement("input");
            partInputId.className = "u-full-width";
            partInputId.placeholder = "Null";
            partInputId.type = "text";
            partInputId.name = "city."+num+".id";
            partInputId.readOnly = true;
            var partId = document.createElement("div");
            partId.className += "two columns";
            partId.id = "city."+num+".id";
            partId.appendChild(partLabelId);
            partId.appendChild(partInputId);
            
            var partLabelName = document.createElement("label");
            partLabelName.for = "name";
            partLabelName.appendChild(document.createTextNode("City#"+num+" Name:"));
            var partInputName = document.createElement("input");
            partInputName.className += "u-full-width";
            partInputName.placeholder = "Name";
            partInputName.type = "text";
            partInputName.name = "city."+num+".name";
            var partName = document.createElement("div");
            partName.className += "ten columns";
            partName.id = "city."+num+".id";
            partName.appendChild(partLabelName);
            partName.appendChild(partInputName);
            
            var partRow = document.createElement("div");
            partRow.className += "row";
            partRow.id = "city."+num;
            partRow.appendChild(partId);
            partRow.appendChild(partName);
            document.getElementById("editor").appendChild(partRow);
            num++;
        }
        function delRow() {
            if (num == 0) return;
            num--;
            var row = document.getElementById("city."+num);
            row.parentNode.removeChild(row);
        }
    </script>
</html>