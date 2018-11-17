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
public class Texto {

    public String texto;
    public List<Frase> frases;

    public Texto(String texto, List<Frase> frases) {
        this.texto = texto;
        this.frases = frases;
    }

    public String getTexto() {
        return texto;
    }

    public List<Frase> getFrases() {
        return frases;
    }

    
}
