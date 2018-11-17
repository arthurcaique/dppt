/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author arthur
 */
public class Aspecto {

    private String aspecto, lemma;
    private List<String> palavras;
    private List<Aspecto> superSets;
    private int freqLemma;

    public Aspecto(String aspecto) {
        aspecto = aspecto.toLowerCase();
        getPalavras();
        getSuperSets();
        this.aspecto = aspecto;
        this.palavras.add(aspecto);
    }
    
    public Aspecto(Nodo nodo) {
        aspecto = nodo.texto.toLowerCase();
        lemma = nodo.lemma.toLowerCase();
        getPalavras();
        getSuperSets();
        this.palavras.add(aspecto);
    }

    public String getAspecto() {
        return aspecto;
    }

    public void setAspecto(String aspecto) {
        this.aspecto = aspecto;
    }

    public final List<String> getPalavras() {
        if (Objects.isNull(palavras)) {
            palavras = new ArrayList<>();
        }
        return palavras;
    }

    public void setPalavras(List<String> palavras) {
        this.palavras = palavras;
    }

    public final List<Aspecto> getSuperSets() {
        if (Objects.isNull(superSets)) {
            superSets = new ArrayList<>();
        }
        return superSets;
    }

    public void setSuperSets(List<Aspecto> superSets) {
        System.out.println("Setou");
        this.superSets = superSets;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public int getFreqLemma() {
        return freqLemma;
    }

    public void setFreqLemma(int freqLemma) {
        this.freqLemma = freqLemma;
    }
    
    public void set(Aspecto aspecto) {
        this.aspecto = aspecto.aspecto;
        this.palavras = aspecto.palavras;
        this.superSets = aspecto.superSets;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Aspecto other = (Aspecto) obj;
        if (!Objects.equals(this.aspecto.toLowerCase(), other.aspecto.toLowerCase())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return aspecto + ": " + toStringSuperSet();
    }

    public String toStringSuperSet() {
        String stringSuperSet = "[";
        for (Aspecto superSet : superSets) {
            stringSuperSet = stringSuperSet.concat(superSet.aspecto + ", ");
        }
        stringSuperSet = stringSuperSet.concat("]");
        return stringSuperSet;
    }

}
