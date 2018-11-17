/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.dpbr;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Relacao_Dependencia;
import com.acbv.propagacao_dupla.entidades.Tipo_Relacao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Arthur
 */
public class Extracao {

    List<Tipo_Relacao> MR = new ArrayList<>(Arrays.asList(new Tipo_Relacao[]{Tipo_Relacao.AMOD, Tipo_Relacao.NMOD, Tipo_Relacao.CSUBJ, Tipo_Relacao.DOBJ, Tipo_Relacao.IOBJ,
        Tipo_Relacao.NSUBJ, Tipo_Relacao.PREP, Tipo_Relacao.XSUBJ}));
    List<Categoria_Sintatica> NN = new ArrayList<>(Arrays.asList(new Categoria_Sintatica[]{Categoria_Sintatica.NN, Categoria_Sintatica.NNS}));
    List<Categoria_Sintatica> JJ = new ArrayList<>(Arrays.asList(new Categoria_Sintatica[]{Categoria_Sintatica.JJ, Categoria_Sintatica.JJS, Categoria_Sintatica.JJR}));

    public Set<Aspecto> extractAspectsUsingR1(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) throws IOException {
        Set<Aspecto> aspectos = new HashSet<>();
        aspectos.addAll(extractAspectsUsingR1_1(frase, lexicoExpandido, aspectosExtraidos));
        aspectos.addAll(extractAspectsUsingR1_2(frase, lexicoExpandido, aspectosExtraidos));
        return aspectos;
    }

    public Set<String> extractOpinionWordsUsingR2(Frase frase, Set<Aspecto> aspects, Set<String> lexicoExpandido) throws IOException {
        Set<String> palavrasOpinativas = new HashSet<>();
        palavrasOpinativas.addAll(extractOpinionWordsUsingR2_1(frase, aspects, lexicoExpandido));
        palavrasOpinativas.addAll(extractOpinionWordsUsingR2_2(frase, aspects, lexicoExpandido));
        return palavrasOpinativas;
    }

    public Set<Aspecto> extractAspectsUsingR3(Frase frase, Set<Aspecto> aspectos, Set<Aspecto> aspectosExtraidos) throws IOException {
        Set<Aspecto> novosAspectos = new HashSet<>();
        novosAspectos.addAll(extractAspectsUsingR3_1(frase, aspectos, aspectosExtraidos));
        novosAspectos.addAll(extractAspectsUsingR3_2(frase, aspectos, aspectosExtraidos));
        return novosAspectos;
    }

    public Set<String> extractOpinionWordsUsingR4(Frase frase, Set<String> opinionWords) {
        Set<String> palavrasOpinativas = new HashSet<>();
        palavrasOpinativas.addAll(extractOpinionWordsUsingR4_1(frase, opinionWords));
        palavrasOpinativas.addAll(extractOpinionWordsUsingR4_2(frase, opinionWords));
        return palavrasOpinativas;
    }

    private Set<Aspecto> extractAspectsUsingR1_1(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                Nodo governante = relacao.governante;
                Nodo dependente = relacao.dependente;
                if (this.MR.contains(relacao.relacao)) {
                    Aspecto aspecto = null;
                    if ((lexicoExpandido.contains(dependente.lemma) || lexicoExpandido.contains(dependente.texto)
                            || lexicoExpandido.contains(dependente.lemma.toLowerCase()) || lexicoExpandido.contains(dependente.texto.toLowerCase()))
                            && (this.NN.contains(governante.categoria))) {
                        aspecto = new Aspecto(governante);
                    }
                    if (aspecto != null && !aspectosExtraidos.contains(aspecto)) {
                        novosAspectos.add(aspecto);
                    }
                }
            }
        }
        return novosAspectos;
    }

    private Set<Aspecto> extractAspectsUsingR1_2(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (int i = 0; i < relacoes.length; i++) {
            Relacao_Dependencia relacao_i = relacoes[i];
            for (int j = 0; j < relacoes.length; j++) {
                if (i < j) {
                    Relacao_Dependencia relacao_j = relacoes[j];
                    if (Objects.nonNull(relacao_i) && Objects.nonNull(relacao_j)) {
                        if (relacao_i.governante.equals(relacao_j.governante)
                                && (this.MR.contains(relacao_i.relacao) || this.MR.contains(relacao_j.relacao))) {
                            Aspecto aspecto = null;
                            if ((lexicoExpandido.contains(relacao_i.dependente.texto) || lexicoExpandido.contains(relacao_i.dependente.lemma)
                                    || lexicoExpandido.contains(relacao_i.dependente.texto.toLowerCase()) || lexicoExpandido.contains(relacao_i.dependente.lemma.toLowerCase()))
                                    && this.NN.contains(relacao_j.dependente.categoria)) {
                                aspecto = new Aspecto(relacao_j.dependente);
                            } else if ((lexicoExpandido.contains(relacao_j.dependente.texto) || lexicoExpandido.contains(relacao_j.dependente.lemma)
                                    || lexicoExpandido.contains(relacao_j.dependente.texto.toLowerCase()) || lexicoExpandido.contains(relacao_j.dependente.lemma.toLowerCase()))
                                    && this.NN.contains(relacao_i.dependente.categoria)) {
                                aspecto = new Aspecto(relacao_i.dependente);
                            }
                            if (aspecto != null && !aspectosExtraidos.contains(aspecto)) {
                                novosAspectos.add(aspecto);
                            }
                        }
                    }
                }
            }
        }
        return novosAspectos;
    }

    private Set<String> extractOpinionWordsUsingR2_1(Frase frase, Set<Aspecto> aspects, Set<String> lexicoExpandido) {
        Set<String> palavrasOpinativasExtraidas = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                if (this.MR.contains(relacao.relacao)) {
                    String palavraOpinativa = null;
                    if (aspects.contains(relacao.governante) && this.JJ.contains(relacao.dependente.categoria)) {
                        palavraOpinativa = relacao.dependente.texto;
                    }
                    if (palavraOpinativa != null && !lexicoExpandido.contains(relacao.dependente.texto)) {
                        palavrasOpinativasExtraidas.add(palavraOpinativa);
                    }
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private Set<String> extractOpinionWordsUsingR2_2(Frase frase, Set<Aspecto> aspectos, Set<String> lexicoExpandido) {
        Set<String> palavrasOpinativasExtraidas = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (int i = 0; i < relacoes.length; i++) {
            Relacao_Dependencia relacao_i = relacoes[i];
            for (int j = 0; j < relacoes.length; j++) {
                if (i < j) {
                    Relacao_Dependencia relacao_j = relacoes[j];
                    if (Objects.nonNull(relacao_i) && Objects.nonNull(relacao_j)) {
                        if (relacao_i.governante.equals(relacao_j.governante)
                                && (this.MR.contains(relacao_i.relacao) || this.MR.contains(relacao_j.relacao))) {
                            String palavraOpinativa = null;
                            if (aspectos.contains(relacao_i.dependente) && this.JJ.contains(relacao_j.dependente.categoria)) {
                                palavraOpinativa = relacao_j.dependente.texto;
                            }
                            if (palavraOpinativa != null && !lexicoExpandido.contains(palavraOpinativa)) {
                                palavrasOpinativasExtraidas.add(palavraOpinativa);
                            }
                        }
                    }
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private Set<Aspecto> extractAspectsUsingR3_1(Frase frase, Set<Aspecto> aspects_i, Set<Aspecto> aspectsSet) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                if (relacao.relacao == Tipo_Relacao.CONJ) {
                    Aspecto aspecto = null;
                    String targ_gov = relacao.governante.texto;
                    String targ_dep = relacao.dependente.texto;
                    if (aspects_i.contains(relacao.dependente) && this.NN.contains(relacao.dependente.categoria)) {
                        aspecto = new Aspecto(relacao.dependente);
                    }
                    if (aspecto != null && !aspectsSet.contains(aspecto)) {
                        novosAspectos.add(aspecto);
                    }
                }
            }
        }
        return novosAspectos;
    }

    private Set<Aspecto> extractAspectsUsingR3_2(Frase frase, Set<Aspecto> aspects_i, Set<Aspecto> aspectsSet) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (int i = 0; i < relacoes.length; i++) {
            Relacao_Dependencia relacao_i = relacoes[i];
            if (Objects.nonNull(relacao_i)) {
                for (int j = 0; j < relacoes.length; j++) {
                    if (i > j) {
                        Relacao_Dependencia relacao_j = relacoes[j];
                        if (Objects.nonNull(relacao_j)) {
                            if (relacao_i.governante.equals(relacao_j.governante)
                                    && isRelacaoEquivalente(relacao_i, relacao_j)) {
                                Aspecto aspecto = null;
                                Nodo dependente_i = relacao_i.dependente;
                                String dependente_j = relacao_j.dependente.texto;
                                if (aspects_i.contains(dependente_i) && this.NN.contains(relacao_j.dependente.categoria)) {
                                    aspecto = new Aspecto(dependente_j);
                                } else if (aspects_i.contains(dependente_j) && this.NN.contains(relacao_i.dependente.categoria)) {
                                    aspecto = new Aspecto(dependente_i);
                                }
                                if (aspecto != null && !aspectsSet.contains(aspecto)) {
                                    novosAspectos.add(aspecto);
                                }
                            }
                        }
                    }
                }
            }
        }
        return novosAspectos;
    }

    private Set<String> extractOpinionWordsUsingR4_1(Frase frase, Set<String> palavrasOpinativas) {
        Set<String> palavrasOpinativasExtraidas = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                if (relacao.relacao == Tipo_Relacao.CONJ && palavrasOpinativas.contains(relacao.governante.texto)
                        && this.JJ.contains(relacao.dependente.categoria) && !palavrasOpinativas.contains(relacao.dependente.texto)) {
                    palavrasOpinativasExtraidas.add(relacao.dependente.texto);
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private Set<String> extractOpinionWordsUsingR4_2(Frase frase, Set<String> palavrasOpinativas) {
        Set<String> palavrasOpinativasExtraidas = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (int i = 0; i < relacoes.length; i++) {
            Relacao_Dependencia relacao_i = relacoes[i];
            for (int j = 0; j < relacoes.length; j++) {
                if (i < j) {
                    Relacao_Dependencia relacao_j = relacoes[j];
                    if (Objects.nonNull(relacao_i) && Objects.nonNull(relacao_j)) {
                        if (relacao_i.governante.equals(relacao_j.governante)
                                && isRelacaoEquivalente(relacao_i, relacao_j)) {
                            String palavraOpinativa = null;
                            if (palavrasOpinativas.contains(relacao_i.dependente.texto) && this.JJ.contains(relacao_j.dependente.categoria)) {
                                palavraOpinativa = relacao_j.dependente.texto;
                            } else if (palavrasOpinativas.contains(relacao_j.dependente.texto) && this.JJ.contains(relacao_i.dependente.categoria)) {
                                palavraOpinativa = relacao_i.dependente.texto;
                            }
                            if (palavraOpinativa != null && !palavrasOpinativas.contains(palavraOpinativa)) {
                                palavrasOpinativasExtraidas.add(palavraOpinativa);
                            }
                        }
                    }
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private boolean isRelacaoEquivalente(Relacao_Dependencia relacao_i, Relacao_Dependencia relacao_j) {
        boolean equivalente = false;
        if (relacao_i.relacao != Tipo_Relacao.OUTROS && relacao_j.relacao != Tipo_Relacao.OUTROS) {
            if (relacao_i.relacao == relacao_j.relacao) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.NSUBJ && relacao_j.relacao == Tipo_Relacao.DOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.DOBJ && relacao_j.relacao == Tipo_Relacao.NSUBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.XSUBJ && relacao_j.relacao == Tipo_Relacao.DOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.DOBJ && relacao_j.relacao == Tipo_Relacao.XSUBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.CSUBJ && relacao_j.relacao == Tipo_Relacao.DOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.DOBJ && relacao_j.relacao == Tipo_Relacao.CSUBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.NSUBJ && relacao_j.relacao == Tipo_Relacao.IOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.IOBJ && relacao_j.relacao == Tipo_Relacao.NSUBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.XSUBJ && relacao_j.relacao == Tipo_Relacao.IOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.IOBJ && relacao_j.relacao == Tipo_Relacao.XSUBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.CSUBJ && relacao_j.relacao == Tipo_Relacao.IOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.IOBJ && relacao_j.relacao == Tipo_Relacao.CSUBJ) {
                equivalente = true;
            }
        }
        return equivalente;
    }

}
