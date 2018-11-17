/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author arthur
 */
public class StringUtils {

    public static String removerAcentosString(String string) throws UnsupportedEncodingException {
        return org.apache.commons.lang3.StringUtils.stripAccents(string);
    }
    
}
