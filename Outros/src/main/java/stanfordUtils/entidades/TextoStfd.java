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
public class TextoStfd {
    
    private String texto;
    private FraseStfd[] frases;

    public TextoStfd(String texto, FraseStfd[] frases) {
        this.texto = texto;
        this.frases = frases;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public FraseStfd[] getFrases() {
        return frases;
    }

    public void setFrases(FraseStfd[] frases) {
        this.frases = frases;
    }
    
}
