/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.entidades;

import java.util.List;

/**
 *
 * @author arthur
 */
public class FrasesAux {
    private String frase;
    private List<String> tokens;
    private List<String> lemmas;

    public FrasesAux(String frase) {
        Nodo nodo;
        this.frase = frase;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getLemmas() {
        return lemmas;
    }

    public void setLemmas(List<String> lemmas) {
        this.lemmas = lemmas;
    }
    
    
}
