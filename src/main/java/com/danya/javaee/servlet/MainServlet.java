/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author danya
 */
@WebServlet("/")
public class MainServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws
            ServletException, IOException {
        req.setAttribute("name", "Daanya");
        String name = (String) req.getAttribute("name");
        if (name == null) {
            throw new ServletException("name is null in getAttribute");
        }
        req.getRequestDispatcher("main.jsp").forward(req, resp);
        
//        PrintWriter out = resp.getWriter();
//        out.println("<h1><i>LOGISTICS IND.</i></h1>");
//        out.println("<br/>");
//        out.println("<a href=\"cities\">/cities</a><br/>");
//        out.println("<a href=\"addresses\">/addresses</a><br/>");
//        out.println("<a href=\"routes\">/routes</a><br/>");
//        out.println("<br/>");
//        Integer visNum = (Integer) req.getSession().getAttribute("visNum");
//        visNum = (visNum == null) ? 1 : visNum+1;
//        req.getSession().setAttribute("visNum", visNum);
//        out.println("You have visited this page " + visNum + " times.");
        
    }
}


//        <p>Your 'sortBy' parameter: <%= request.getParameter("sortBy") %></p>
//        <%! String name = "Daanya"; %>
//        <%
//            out.println("Your IP address is " + request.getRemoteAddr() + "<br/>");
//            out.println("Your name is " + name + "<br/>");
//            
//        %>
//        <jsp:useBean id="first" class="com.danya.javaee.domain.City" />
//        <jsp:setProperty name="first" property="name" value="Kiiiiev" />
//        <h2>You live in a <jsp:getProperty name="first" property="name" /></h2>
//        <h1>Hello ${name}</h1>
//        <center>
//         <h2>Auto Refresh Header Example</h2>
//            <%
//               // Set refresh, autoload time as 60 seconds
//               response.setIntHeader("Refresh", 60);
//
//               // Get current time
//               Calendar calendar = new GregorianCalendar();
//
//               String am_pm;
//               int hour = calendar.get(Calendar.HOUR);
//               int minute = calendar.get(Calendar.MINUTE);
//               int second = calendar.get(Calendar.SECOND);
//
//               if(calendar.get(Calendar.AM_PM) == 0) 
//                  am_pm = "AM";
//               else
//                  am_pm = "PM";
//                  String CT = hour+":"+ minute +":"+ second +" "+ am_pm;
//                  out.println("Current Time is: " + CT + "\n");
//            %>
//         </center>