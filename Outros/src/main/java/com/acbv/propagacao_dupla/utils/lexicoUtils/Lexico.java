/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.utils.lexicoUtils;

import java.util.List;

/**
 *
 * @author arthur
 */
public class Lexico {
    
    private List<EntradaLexico> entradas;

    public Lexico(List<EntradaLexico> entradas) {
        this.entradas = entradas;
    }

    public List<EntradaLexico> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<EntradaLexico> entradas) {
        this.entradas = entradas;
    }
    
    
}
