/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.dpbr2;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Arthur
 */
public class DoublePropagation2 {

    public Set<Aspecto> aspectosExtraidos;
    public Set<String> lexicoExpandido;

    public void runDP(Corpus corpus, Set<String> lexico, Set<String>stopSubWords) {
        try {
            this.lexicoExpandido = lexico;
            this.aspectosExtraidos = new HashSet<>();
            List<Resenha> resenhas = corpus.resenhas;
            boolean continua = true;
            Set<Aspecto> alvosExtraidos_i;
            Set<String> palavrasOpExtraidas_i;
            Extracao1 extracao = new Extracao1();
            List<Frase> frases = getFrases(resenhas);
            while (continua) {
                alvosExtraidos_i = new HashSet<>();
                palavrasOpExtraidas_i = new HashSet<>();
                for (Frase frase : frases) {
                    alvosExtraidos_i.addAll(extracao.extractAspectsUsingR1(frase, this.lexicoExpandido, aspectosExtraidos));
                    alvosExtraidos_i.addAll(extracao.extractAspectsUsingR6(frase, this.lexicoExpandido, aspectosExtraidos));
                    palavrasOpExtraidas_i.addAll(extracao.extractOpinionWordsUsingR4(frase, this.lexicoExpandido));
                }
                this.aspectosExtraidos.addAll(alvosExtraidos_i);
                this.lexicoExpandido.addAll(palavrasOpExtraidas_i);
                Set<String> palavrasOpinativasExtraidas_j = new HashSet<>();
                Set<Aspecto> alvosExtraidos_j = new HashSet<>();
                for (Frase frase : frases) {
                    alvosExtraidos_j.addAll(extracao.extractAspectsUsingR3(frase, alvosExtraidos_i, this.aspectosExtraidos, this.lexicoExpandido));
                    palavrasOpinativasExtraidas_j.addAll(extracao.extractOpinionWordsUsingR2(frase, alvosExtraidos_i, this.lexicoExpandido));
                }
                alvosExtraidos_i.addAll(alvosExtraidos_j);
                palavrasOpExtraidas_i.addAll(palavrasOpinativasExtraidas_j);
                this.aspectosExtraidos.addAll(alvosExtraidos_j);
                this.lexicoExpandido.addAll(palavrasOpinativasExtraidas_j);
                System.out.println("Léxico expandido: "+lexicoExpandido.size());
                if (alvosExtraidos_i.isEmpty() && palavrasOpExtraidas_i.isEmpty()) {
                    continua = false;
                }
            }
            
//            NewAspectPruning.setSuperSets(this.aspectosExtraidos);
//            NewAspectPruning.doPSupportBasedPruning(this.aspectosExtraidos, frases);
//            NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
//            NewAspectPruning.removerNonSubstantivos(aspectosExtraidos);
//            NewAspectPruning.removerAspectos(aspectosExtraidos);
            
//            TargetPruning targetPruning = new TargetPruning(corpus, this.aspectosExtraidos, stopSubWords);
//            this.aspectosExtraidos = targetPruning.aspectos;
//            List<NewAspectPruning.Aspect> aspectsAndAspectsSuperSets = NewAspectPruning.getAspectsAndAspectsSuperSets(aspectosExtraidos);
//            aspectsAndAspectsSuperSets.forEach((aspectsAndAspectsSuperSet) -> {
//                System.out.println(aspectsAndAspectsSuperSet);
//            });
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private List<Frase> getFrases(List<Resenha> resenhas) {
        List<Frase> frases = new ArrayList<>();
        resenhas.stream().map((resenha) -> Arrays.asList(resenha.frases)).forEachOrdered((frasesResenha) -> {
            frases.addAll(frasesResenha);
        });
        return frases;
    }

    private HashMap<String, Integer> getBagOfWords(List<Resenha> resenhas) {
        HashMap<String, Integer> bow = new HashMap<>();
        resenhas.stream().map((resenha) -> resenha.frases).forEachOrdered((frases) -> {
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (Nodo nodo : nodos) {
                    String token = nodo.texto;
                    bow.putIfAbsent(token, 0);
                    Integer freqAnterior = bow.get(token) + 1;
                    bow.replace(token, freqAnterior);
                }
            }
        });
        return bow;
    }

}
