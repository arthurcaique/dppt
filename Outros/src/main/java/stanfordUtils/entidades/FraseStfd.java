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
public class FraseStfd {

    private String frase;
    private RelacaoDependenciaStfd[] relacoesDependencia;
    private TokenStfd[] tokens;

    public FraseStfd(String frase, RelacaoDependenciaStfd[] relacoesDependencia) {
        this.frase = frase;
        this.relacoesDependencia = relacoesDependencia;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public RelacaoDependenciaStfd[] getRelacoesDependencia() {
        return relacoesDependencia;
    }

    public void setRelacoesDependencia(RelacaoDependenciaStfd[] relacoesDependencia) {
        this.relacoesDependencia = relacoesDependencia;
    }

    public TokenStfd[] getTokens() {
        return tokens;
    }

    public void setTokens(TokenStfd[] tokens) {
        this.tokens = tokens;
    }
    
}
