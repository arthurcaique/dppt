/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.normalizadorugcmvn;

import java.util.List;

/**
 *
 * @author arthur
 */
public class Frase {
    
    public String frase;
    public List<String> tokens;

    @Override
    public String toString() {
        return "Frase{" + "frase=" + frase + ", tokens=" + tokens + '}';
    }
    
    
}
