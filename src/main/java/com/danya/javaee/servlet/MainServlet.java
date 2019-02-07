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
        PrintWriter out = resp.getWriter();
        out.println("<h1><i>LOGISTICS IND.</i></h1>");
        out.println("<br/>");
        out.println("<a href=\"cities\">/cities</a><br/>");
        out.println("<a href=\"addresses\">/addresses</a><br/>");
        out.println("<a href=\"routes\">/routes</a><br/>");
        out.println("<br/>");
        Integer visNum = (Integer) req.getSession().getAttribute("visNum");
        visNum = (visNum == null) ? 1 : visNum+1;
        req.getSession().setAttribute("visNum", visNum);
        out.println("You have visited this page " + visNum + " times.");
        
    }
}
