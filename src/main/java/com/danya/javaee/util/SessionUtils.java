/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSession;

/**
 *
 * @author danya
 */
public final class SessionUtils {
    
    public static Set getSet(HttpSession session, String attr) {
        Set set = (Set) session.getAttribute(attr);
        set = (set == null) ? new HashSet() : set;
        session.setAttribute(attr, set);
        return set;
    }
    
    public static List getList(HttpSession session, String attr) {
        List list = (List) session.getAttribute(attr);
        list = (list == null) ? new ArrayList() : list;
        session.setAttribute(attr, list);
        return list;
    }
    
    public static Map getMap(HttpSession session, String attr) {
        Map map = (Map) session.getAttribute(attr);
        map = (map == null) ? new HashMap() : map;
        session.setAttribute(attr, map);
        return map;
    }
    
    public static boolean getBoolean(HttpSession session, String attr) {
        Boolean value = (Boolean) session.getAttribute(attr);
        value = (value == null) ? false : value;
        return value;
    }
    
}
