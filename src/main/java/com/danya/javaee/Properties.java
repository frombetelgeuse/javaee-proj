/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.danya.javaee;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author danya
 */
public class Properties {
    private static ResourceBundle rb = ResourceBundle.getBundle("config");
    
    public static String get(String prop) {
        return rb.getString(prop);
    }
}
