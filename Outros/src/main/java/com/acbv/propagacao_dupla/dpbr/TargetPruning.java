/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.dpbr;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.utils.Sintatico;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Arthur
 */
public class TargetPruning {

    private final Corpus corpus;
    private final List<Resenha> resenhas;
    public Set<Aspecto> aspectos;
    private final Set<String> stopSubWords;

    public TargetPruning(Corpus corpus, Set<Aspecto> aspectos, Set<String> stopSubsWords) {
        this.corpus = corpus;
        this.resenhas = corpus.resenhas;
        this.aspectos = aspectos;
        this.stopSubWords = stopSubsWords;
        Set<Aspecto> aspectosPruning3 = pruningMethod3(aspectos);
        this.aspectos.addAll(aspectosPruning3);
    }

    private Set<Aspecto> pruningMethod3(Set<Aspecto> aspectos) {
        Set<Aspecto> targets_ = new HashSet<>();
        this.resenhas.forEach((resenha) -> {
            targets_.addAll(pruningMethod3(resenha, aspectos));
        });
        return targets_;
    }

    private Set<Aspecto> pruningMethod3(Resenha resenha, Set<Aspecto> targets) {
        int q = 2, k = 1;
        Set<Aspecto> phraseTargets = new HashSet<>();
        Frase[] frases = resenha.frases;
        for (Frase frase : frases) {
            Nodo[] nodos = frase.nodos;
            for (int i = 0; i < nodos.length; i++) {
                Nodo token = nodos[i];
                boolean modificou = false;
                int primeiroTokenIndice = i;
                String tokenStr = token.texto;
                String lemmaStr = token.lemma;
                Aspecto aspectoTarget = new Aspecto(token);
                if (targets.contains(aspectoTarget) /*&& (token.categoria == Categoria_Sintatica.NN || token.categoria == Categoria_Sintatica.NNS)*/) {
                    boolean pegaAntes = true;
                    boolean pegaApos = true;
                    for (int j = 0; j < q; j++) {
                        int index_j = j + 1;
                        try {
                            if (pegaAntes) {
                                Nodo antes = nodos[i - index_j];
                                if (Sintatico.isSubstantivo(antes.categoria)) {
                                    tokenStr = antes.texto.concat(" " + tokenStr);
                                    lemmaStr = antes.lemma.concat(" " + lemmaStr);
                                    modificou = true;
                                    primeiroTokenIndice = i - index_j;
                                } else {
                                    pegaAntes = false;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            pegaAntes = false;
                        }
                    }
                    for (int j = 0; j < q; j++) {
                        int index_j = j + 1;
                        try {
                            if (pegaApos) {
                                Nodo apos = nodos[i + index_j];
                                if (Sintatico.isSubstantivo(apos.categoria)) {
                                    tokenStr = tokenStr.concat(" " + apos.texto);
                                    lemmaStr = lemmaStr.concat(" " + apos.lemma);
                                    modificou = true;
                                } else {
                                    pegaApos = false;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                            pegaApos = false;
                        }
                    }
                    if (modificou) {
                        int numAdjetivos = 0;
                        boolean satisfezK = false;
                        for (int l = 0; l < primeiroTokenIndice; l++) {
                            Nodo t = nodos[l];
                            if (Sintatico.isAdjetivo(t.categoria)) {
                                numAdjetivos++;
                                if (numAdjetivos == k) {
                                    satisfezK = true;
                                    break;
                                }
                            }
                        }
                        if (satisfezK) {
                            System.out.println("Pruning 3: " + tokenStr);
                            aspectoTarget.setAspecto(tokenStr);
                            aspectoTarget.setLemma(lemmaStr);
                            phraseTargets.add(aspectoTarget);
                            System.out.println(frase.texto);
                        } else {
                            phraseTargets.add(aspectoTarget);
                        }
                    } else {
                        phraseTargets.add(aspectoTarget);
                    }
                }
            }
        }
        return phraseTargets;
    }

}
