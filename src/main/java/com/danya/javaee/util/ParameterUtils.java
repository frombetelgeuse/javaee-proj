/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.util;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author danya
 */
public class ParameterUtils {
    
    public static String getParameter(HttpServletRequest req, String name) {
        String param = req.getParameter(name);
        param = (param == null || param.isEmpty()) ? null : param;
        return param;
    }
    
    public static Integer getParameterInt(HttpServletRequest req, String name) {
        String param = req.getParameter(name);
        Integer paramInt = (param == null || param.isEmpty()) ? null : Integer.valueOf(param);
        return paramInt;
    }
}
