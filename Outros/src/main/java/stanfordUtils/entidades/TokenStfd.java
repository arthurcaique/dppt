/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stanfordUtils.entidades;

/**
 *
 * @author arthur
 */
public class TokenStfd {
    
    private int indice;
    private String token, lemma, categoriaSintatica;

    public TokenStfd(String token, String categoriaSintatica) {
        this.token = token;
        this.categoriaSintatica = categoriaSintatica;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCategoriaSintatica() {
        return categoriaSintatica;
    }

    public void setCategoriaSintatica(String categoriaSintatica) {
        this.categoriaSintatica = categoriaSintatica;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }
    
}
