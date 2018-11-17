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
public class RelacaoDependenciaStfd {
    
    private String relacao;
    private TokenStfd governante, dependente;

    public RelacaoDependenciaStfd(String relacao, TokenStfd governante, TokenStfd dependente) {
        this.relacao = relacao;
        this.governante = governante;
        this.dependente = dependente;
    }

    public String getRelacao() {
        return relacao;
    }

    public void setRelacao(String relacao) {
        this.relacao = relacao;
    }

    public TokenStfd getGovernante() {
        return governante;
    }

    public void setGovernante(TokenStfd governante) {
        this.governante = governante;
    }

    public TokenStfd getDependente() {
        return dependente;
    }

    public void setDependente(TokenStfd dependente) {
        this.dependente = dependente;
    }
    
}
