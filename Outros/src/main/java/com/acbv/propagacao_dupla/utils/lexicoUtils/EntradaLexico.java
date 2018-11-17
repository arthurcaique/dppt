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
public class EntradaLexico {
    
    private String entrada;
    private String pos;
    private List<Integer> polaridades;

    public EntradaLexico(String entrada, List<Integer> polaridades) {
        this.entrada = entrada;
        this.polaridades = polaridades;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<Integer> getPolaridades() {
        return polaridades;
    }

    public void setPolaridades(List<Integer> polaridades) {
        this.polaridades = polaridades;
    }
    
    
}
