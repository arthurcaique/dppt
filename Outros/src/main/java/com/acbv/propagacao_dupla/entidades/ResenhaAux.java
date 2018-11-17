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
public class ResenhaAux {
    
    private String resenha;
    private List<String> tokens;
    private List<String []> biGrams;
    private List<String []> triGrams;
    private List<String []> fourGrams;
    private List<String []> fiveGrams;
    private List<FrasesAux> frasesAux;

    public List<FrasesAux> getFrasesAux() {
        return frasesAux;
    }

    public void setFrasesAux(List<FrasesAux> frasesAux) {
        this.frasesAux = frasesAux;
    }

    public ResenhaAux(String resenha) {
        this.resenha = resenha;
    }

    public String getResenha() {
        return resenha;
    }

    public void setResenha(String resenha) {
        this.resenha = resenha;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String[]> getBiGrams() {
        return biGrams;
    }

    public void setBiGrams(List<String[]> biGrams) {
        this.biGrams = biGrams;
    }

    public List<String[]> getTriGrams() {
        return triGrams;
    }

    public void setTriGrams(List<String[]> triGrams) {
        this.triGrams = triGrams;
    }

    public List<String[]> getFourGrams() {
        return fourGrams;
    }

    public void setFourGrams(List<String[]> fourGrams) {
        this.fourGrams = fourGrams;
    }

    public List<String[]> getFiveGrams() {
        return fiveGrams;
    }

    public void setFiveGrams(List<String[]> fiveGrams) {
        this.fiveGrams = fiveGrams;
    }
    
    
}
