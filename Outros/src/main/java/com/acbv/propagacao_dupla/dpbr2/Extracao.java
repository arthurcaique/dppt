/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.dpbr2;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Relacao_Dependencia;
import com.acbv.propagacao_dupla.entidades.Tipo_Relacao;
import com.acbv.propagacao_dupla.utils.Sintatico;
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

    List<Tipo_Relacao> MR = new ArrayList<>(Arrays.asList(new Tipo_Relacao[]{Tipo_Relacao.ADVMOD, Tipo_Relacao.AMOD, Tipo_Relacao.NMOD, Tipo_Relacao.CSUBJ, Tipo_Relacao.DOBJ, Tipo_Relacao.IOBJ,
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

    public Set<Aspecto> extractAspectsUsingR3(Frase frase, Set<Aspecto> aspectos, Set<Aspecto> aspectosExtraidos, Set<String> lexicoExpandido) throws IOException {
        Set<Aspecto> novosAspectos = new HashSet<>();
        novosAspectos.addAll(extractAspectsUsingR3_1(frase, aspectos, aspectosExtraidos, lexicoExpandido));
        novosAspectos.addAll(extractAspectsUsingR3_2(frase, aspectos, aspectosExtraidos, lexicoExpandido));
        return novosAspectos;
    }

    public Set<String> extractOpinionWordsUsingR4(Frase frase, Set<String> opinionWords) {
        Set<String> palavrasOpinativas = new HashSet<>();
        palavrasOpinativas.addAll(extractOpinionWordsUsingR4_1(frase, opinionWords));
        palavrasOpinativas.addAll(extractOpinionWordsUsingR4_2(frase, opinionWords));
        return palavrasOpinativas;
    }

    public Set<Aspecto> extractAspectsUsingR5(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> aspectos = new HashSet<>();
        aspectos.addAll(extractAspectsUsingR5_1(frase, lexicoExpandido, aspectosExtraidos));
        return aspectos;
    }

    public Set<Aspecto> extractAspectsUsingR6(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> aspectos = new HashSet<>();
        aspectos.addAll(extractAspectsUsingR6_1(frase, lexicoExpandido, aspectosExtraidos));
        return aspectos;
    }

    private Set<Aspecto> extractAspectsUsingR1_1(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                Nodo governante = relacao.governante;
                Nodo dependente = relacao.dependente;
                if (this.MR.contains(relacao.relacao)) {
                    Nodo aspecto = null;
                    boolean lexicoContemDependente = lexicoExpandido.contains(dependente.texto) || lexicoExpandido.contains(dependente.lemma)
                            || lexicoExpandido.contains(dependente.texto.toLowerCase()) || lexicoExpandido.contains(dependente.lemma.toLowerCase());
                    boolean governanteESubstantivo = this.NN.contains(governante.categoria);
                    if (Boolean.logicalAnd(lexicoContemDependente, governanteESubstantivo)) {
                        aspecto = governante;
                    }
                    pruning(novosAspectos, frase, aspecto, aspectosExtraidos, lexicoExpandido);
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
                                && (this.MR.contains(relacao_i.relacao) && this.MR.contains(relacao_j.relacao))) {
                            Nodo aspecto = null;
                            if ((lexicoExpandido.contains(relacao_i.dependente.texto) || lexicoExpandido.contains(relacao_i.dependente.lemma)
                                    || lexicoExpandido.contains(relacao_i.dependente.texto.toLowerCase()) || lexicoExpandido.contains(relacao_i.dependente.lemma.toLowerCase()))
                                    && this.NN.contains(relacao_j.dependente.categoria)) {
                                aspecto = relacao_j.dependente;
                                System.out.println("1_2");
                                System.out.println(frase.texto);
                                System.out.println(relacao_i);
                                System.out.println(relacao_j);
                            } else if ((lexicoExpandido.contains(relacao_j.dependente.texto) || lexicoExpandido.contains(relacao_j.dependente.lemma)
                                    || lexicoExpandido.contains(relacao_j.dependente.texto.toLowerCase()) || lexicoExpandido.contains(relacao_j.dependente.lemma.toLowerCase()))
                                    && this.NN.contains(relacao_i.dependente.categoria)) {
                                aspecto = relacao_i.dependente;
                                System.out.println("1_2");
                                System.out.println(frase.texto);
                                System.out.println(relacao_i);
                                System.out.println(relacao_j);
                            }
                            pruning(novosAspectos, frase, aspecto, aspectosExtraidos, lexicoExpandido);
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
            if (relacao != null) {
                if (this.MR.contains(relacao.relacao)) {
                    String palavraOpinativa = null;
                    boolean governanteIsAspecto = aspects.contains(new Aspecto(relacao.governante.texto));
                    if (governanteIsAspecto && this.JJ.contains(relacao.dependente.categoria)) {
                        palavraOpinativa = relacao.dependente.texto;
                    }
//                    else if (aspects.contains(relacao.governante.texto) && this.JJ.contains(relacao.governante.categoria)
//                            && !lexicoExpandido.contains(relacao.governante.texto)) {
//                        palavraOpinativa = relacao.governante.texto;
//                    }
                    if (palavraOpinativa != null
                            && (!lexicoExpandido.contains(palavraOpinativa) || !lexicoExpandido.contains(palavraOpinativa.toLowerCase()))) {
//                        System.out.println("EXTRAÇÃO PALAVRA OPINATIVA: MÉTODO 2.1");
//                        System.out.println("Palavra opinativa extraída em 2.1: " + palavraOpinativa);
//                        System.out.println("Relação: " + relacao);
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
                                && (this.MR.contains(relacao_i.relacao) && this.MR.contains(relacao_j.relacao))) {
                            String palavraOpinativa = null;
                            boolean dependente_relacao_i_is_dependente = aspectos.contains(new Aspecto(relacao_i.dependente.texto));
                            boolean dependente_relacao_j_is_dependente = aspectos.contains(new Aspecto(relacao_j.dependente.texto));
                            if (dependente_relacao_i_is_dependente && this.JJ.contains(relacao_j.dependente.categoria)) {
                                palavraOpinativa = relacao_j.dependente.texto;
                            } else if (dependente_relacao_j_is_dependente && this.JJ.contains(relacao_i.dependente.categoria)) {
                                palavraOpinativa = relacao_i.dependente.texto;
                            }
                            if (palavraOpinativa != null
                                    && (!lexicoExpandido.contains(palavraOpinativa) || !lexicoExpandido.contains(palavraOpinativa.toLowerCase()))) {
//                                System.out.println("EXTRAÇÃO PALAVRA OPINATIVA: MÉTODO 2.2");
//                                System.out.println("Palavra opinativa extraída em 2.2: " + palavraOpinativa);
//                                System.out.println("Relação 1: " + relacao_i);
//                                System.out.println("Relação 2: " + relacao_j);
                                palavrasOpinativasExtraidas.add(palavraOpinativa);
                            }
                        }
                    }
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private Set<Aspecto> extractAspectsUsingR3_1(Frase frase, Set<Aspecto> aspectosExtraidosIteracaoAtual, Set<Aspecto> aspectosExtraidosAnteriormente, Set<String> lexicoExpandido) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                if (relacao.relacao == Tipo_Relacao.CONJ) {
                    Nodo aspecto = null;
                    String targ_gov = relacao.governante.texto;
                    String targ_dep = relacao.dependente.texto;
                    Aspecto posAspecto_gov = new Aspecto(targ_gov);
                    Aspecto posAspecto_dep = new Aspecto(targ_dep);
                    if (aspectosExtraidosIteracaoAtual.contains(posAspecto_gov) && this.NN.contains(relacao.dependente.categoria)) {
                        aspecto = relacao.dependente;
                    } else if (aspectosExtraidosIteracaoAtual.contains(posAspecto_dep) && this.NN.contains(relacao.governante.categoria)) {
                        aspecto = relacao.governante;
                    }
                    Set<Aspecto> aspectosTotais = new HashSet<>(aspectosExtraidosAnteriormente);
                    aspectosTotais.addAll(aspectosExtraidosIteracaoAtual);
                    pruning(novosAspectos, frase, aspecto, aspectosTotais, lexicoExpandido);
                }
            }
        }
        return novosAspectos;
    }

    private Set<Aspecto> extractAspectsUsingR3_2(Frase frase, Set<Aspecto> aspectosExtraidosIteracaoAtual, Set<Aspecto> aspectosExtraidosAnteriormente, Set<String> lexicoExpandido) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (int i = 0; i < relacoes.length; i++) {
            Relacao_Dependencia relacao_i = relacoes[i];
            if (Objects.nonNull(relacao_i)) {
                for (int j = 0; j < relacoes.length; j++) {
                    if (i > j) {
                        Relacao_Dependencia relacao_j = relacoes[j];
                        if (Objects.nonNull(relacao_j)) {
//                            if (relacao_i.governante.equals(relacao_j.governante)
//                                    && isRelacaoEquivalente(relacao_i, relacao_j)) {
                            if (relacao_i.governante.equals(relacao_j.governante)
                                    && isRelacaoEquivalenteSujeitoObjeto(relacao_i, relacao_j)) {
                                Nodo aspecto = null;
                                String tgt_dep_i = relacao_i.dependente.texto;
                                String tgt_dep_j = relacao_j.dependente.texto;
                                Aspecto posAspecto_tgt_dep_i = new Aspecto(tgt_dep_i);
                                Aspecto posAspecto_tgt_dep_j = new Aspecto(tgt_dep_j);
                                if (aspectosExtraidosIteracaoAtual.contains(posAspecto_tgt_dep_i) && this.NN.contains(relacao_j.dependente.categoria)) {
                                    aspecto = relacao_j.dependente;
                                    System.out.println("3_2");
                                    System.out.println(frase.texto);
                                    //System.out.println(frase);
                                    System.out.println(relacao_i);
                                    System.out.println(relacao_j);
                                }
//                                else if (aspectosExtraidosIteracaoAtual.contains(posAspecto_tgt_dep_j) && this.NN.contains(relacao_i.dependente.categoria)) {
//                                    aspecto = relacao_i.dependente;
//                                }
                                Set<Aspecto> aspectosTotais = new HashSet<>(aspectosExtraidosAnteriormente);
                                aspectosTotais.addAll(aspectosExtraidosIteracaoAtual);
                                pruning(novosAspectos, frase, aspecto, aspectosTotais, lexicoExpandido);
                            }
                        }
                    }
                }
            }
        }
//        novosAspectos.forEach((novosAspecto) -> {
//            System.out.println("EXTRAÇÃO 3.2: " + novosAspecto.getAspecto());
//        });
        return novosAspectos;
    }

    private Set<String> extractOpinionWordsUsingR4_1(Frase frase, Set<String> palavrasOpinativas) {
        Set<String> palavrasOpinativasExtraidas = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                if (relacao.relacao == Tipo_Relacao.CONJ && palavrasOpinativas.contains(relacao.governante.texto)
                        && this.JJ.contains(relacao.dependente.categoria) && !palavrasOpinativas.contains(relacao.dependente.texto)) {
//                    System.out.println("EXTRAÇÃO PALAVRA OPINATIVA: MÉTODO 4.1");
//                    System.out.println("Palavra opinativa extraída em 4.1: " + relacao.dependente.texto);
//                    System.out.println("Relação: " + relacao);
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
            if (Objects.nonNull(relacao_i)) {
                for (int j = 0; j < relacoes.length; j++) {
                    if (i < j) {
                        Relacao_Dependencia relacao_j = relacoes[j];
                        if (Objects.nonNull(relacao_j)) {
                            if (relacao_i.governante.equals(relacao_j.governante)
                                    && isRelacaoEquivalente(relacao_i, relacao_j)) {
                                String palavraOpinativa = null;
                                if (palavrasOpinativas.contains(relacao_i.dependente.texto) && this.JJ.contains(relacao_j.dependente.categoria)) {
                                    palavraOpinativa = relacao_j.dependente.texto;
                                } else if (palavrasOpinativas.contains(relacao_j.dependente.texto) && this.JJ.contains(relacao_i.dependente.categoria)) {
                                    palavraOpinativa = relacao_i.dependente.texto;
                                }
                                if (palavraOpinativa != null
                                        && !palavrasOpinativas.contains(palavraOpinativa)
                                        && !palavrasOpinativasExtraidas.contains(palavraOpinativa)) {
//                                    System.out.println("EXTRAÇÃO PALAVRA OPINATIVA: MÉTODO 4.2");
//                                    System.out.println("Palavra extraída em 4.2: " + palavraOpinativa);
//                                    System.out.println("Relação 1: " + relacao_i);
//                                    System.out.println("Relação 2: " + relacao_j);
                                    System.out.println("4_2");
                                    System.out.println(frase.texto);
                                    System.out.println(relacao_i);
                                    System.out.println(relacao_j);
                                    palavrasOpinativasExtraidas.add(palavraOpinativa);
                                }
                            }
                        }
                    }
                }
            }
        }
        return palavrasOpinativasExtraidas;
    }

    private Set<Aspecto> extractAspectsUsingR5_1(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                Nodo governante = relacao.governante;
                Nodo dependente = relacao.dependente;
                if (this.MR.contains(relacao.relacao)) {
                    Nodo aspecto = null;
                    if ((lexicoExpandido.contains(governante.texto) || lexicoExpandido.contains(governante.lemma)
                            || lexicoExpandido.contains(governante.texto.toLowerCase()) || lexicoExpandido.contains(governante.lemma.toLowerCase()))
                            && Categoria_Sintatica.VB.equals(governante.categoria)
                            && this.NN.contains(dependente.categoria)) {
                        aspecto = dependente;
                    }
//                    if (aspecto != null) {
//                        System.out.println("5.1: " + aspecto.texto);
//                    }
                    pruning(novosAspectos, frase, aspecto, aspectosExtraidos, lexicoExpandido);
                }
            }
        }
        return novosAspectos;
    }

    private Set<Aspecto> extractAspectsUsingR6_1(Frase frase, Set<String> lexicoExpandido, Set<Aspecto> aspectosExtraidos) {
        Set<Aspecto> novosAspectos = new HashSet<>();
        Relacao_Dependencia[] relacoes = frase.relacoes;
        for (Relacao_Dependencia relacao : relacoes) {
            if (Objects.nonNull(relacao)) {
                Nodo governante = relacao.governante;
                Nodo dependente = relacao.dependente;
                if (this.MR.contains(relacao.relacao)) {
                    Nodo aspecto = null;
                    if ((lexicoExpandido.contains(governante.texto) || lexicoExpandido.contains(governante.lemma)
                            || lexicoExpandido.contains(governante.texto.toLowerCase()) || lexicoExpandido.contains(governante.lemma.toLowerCase()))
                            && Categoria_Sintatica.RB.equals(governante.categoria)
                            && this.NN.contains(dependente.categoria)) {
                        aspecto = dependente;
                    }
//                    if (aspecto != null) {
//                        System.out.println("6.1: " + aspecto.texto);
//                    }
                    pruning(novosAspectos, frase, aspecto, aspectosExtraidos, lexicoExpandido);
                }
            }
        }
        return novosAspectos;
    }

    private boolean isRelacaoEquivalenteSujeitoObjeto(Relacao_Dependencia relacao_i, Relacao_Dependencia relacao_j) {
        boolean equivalente = false;
        if (relacao_i.relacao != Tipo_Relacao.OUTROS && relacao_j.relacao != Tipo_Relacao.OUTROS) {
            if (relacao_i.relacao == relacao_j.relacao) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.NSUBJ && relacao_j.relacao == Tipo_Relacao.DOBJ) {
                equivalente = true;
            } else if (relacao_i.relacao == Tipo_Relacao.DOBJ && relacao_j.relacao == Tipo_Relacao.NSUBJ) {
                equivalente = true;
            }
        }
        return equivalente;
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

    private void pruning(Set<Aspecto> novosAspectos, Frase frase, Nodo nodoAspecto, Set<Aspecto> aspectosExtraidos, Set<String> lexicoExpandido) {
        if (Objects.nonNull(nodoAspecto)) {
            Aspecto aspecto = new Aspecto(nodoAspecto);
            for (Aspecto aspectoExtraido : aspectosExtraidos) {
                if (aspecto.equals(aspectoExtraido)) {
                    aspecto.set(aspectoExtraido);
                    break;
                }
            }
            Aspecto aspectoPruning3 = this.pruningMethod3(frase, nodoAspecto, lexicoExpandido);
            if (Objects.nonNull(aspectoPruning3)) {
                if (aspectoPruning3.getLemma().equalsIgnoreCase("note Dell Dell")) {
                }
                if (!aspectosExtraidos.contains(aspectoPruning3)) {
                    aspectoPruning3.setPalavras(Arrays.asList(aspectoPruning3.getLemma().split(" ")));
                    novosAspectos.add(aspectoPruning3);
                    //                    System.out.println("PRUNING 3: " + aspectoPruning3.getAspecto());
                }
            }

            Aspecto aspectoPruning5 = this.pruningMethod5(frase, nodoAspecto, lexicoExpandido);
            if (Objects.nonNull(aspectoPruning5)) {
                if (aspectoPruning5.getLemma().equalsIgnoreCase("note Dell Dell")) {
                }
                if (!aspectosExtraidos.contains(aspectoPruning5)) {
                    aspectoPruning5.setPalavras(Arrays.asList(aspectoPruning5.getLemma().split(" ")));
                    novosAspectos.add(aspectoPruning5);
                    //                    System.out.println("PRUNING 5: " + aspectoPruning5.getAspecto());
                }
            }
            //            if(aspecto.getAspecto().contains("porque vai dar problema")){
            //                System.out.println("DOIDO");
            //            }
            //            System.out.println(aspecto.getLemma());
            if (aspectosExtraidos.contains(aspecto)) {
                //                System.out.println(aspecto.getAspecto());
                aspectosExtraidos.add(aspecto);
            } else {
                //                System.out.println(aspecto.getAspecto());
                novosAspectos.add(aspecto);
            }
//            }
//            System.out.println("ASPECTO: "+aspecto.getAspecto());
//            System.out.println("__________________________________________________________________");
        }
    }

    private Aspecto pruningMethod3(Frase frase, Nodo aspecto, Set<String> lexico) {
        int q = 2, k = 1;
        Aspecto phraseTarget = null;
        Nodo[] nodos = frase.nodos;
        for (int i = 0; i < nodos.length; i++) {
            Nodo token = nodos[i];
            if (token.equals(aspecto)) {
                String tokenStr = token.texto;
                String lemmaStr = token.lemma;
                boolean modificou = false;
                boolean pegaAntes = true;
                boolean pegaApos = true;
                int indicePrimeiroToken = i;
                int indiceUltimoToken = i;
                //Pega antes
                for (int j = 0; j < q; j++) {
                    int index_j = j + 1;
                    try {
                        if (pegaAntes) {
                            int index_antes = i - index_j;
                            Nodo antes = nodos[index_antes];
                            if (Sintatico.isSubstantivo(antes.categoria)) {
                                tokenStr = antes.texto.concat(" " + tokenStr);
                                lemmaStr = antes.lemma.concat(" " + lemmaStr);
                                modificou = true;
                                indicePrimeiroToken = index_antes;
                            } else {
                                pegaAntes = false;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        pegaAntes = false;
                    }
                }
                //Pega após
                for (int j = 0; j < q; j++) {
                    int index_j = j + 1;
                    try {
                        if (pegaApos) {
                            int index_apos = i + index_j;
                            Nodo apos = nodos[index_apos];
                            if (Sintatico.isSubstantivo(apos.categoria)) {
                                tokenStr = tokenStr.concat(" " + apos.texto);
                                lemmaStr = lemmaStr.concat(" " + apos.lemma);
                                modificou = true;
                                indiceUltimoToken = index_apos;
                            } else {
                                pegaApos = false;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        pegaApos = false;
                    }
                }
                if (modificou) {
                    boolean satisfez = false;
                    for (int l = 0; l < indicePrimeiroToken; l++) {
                        Nodo t = nodos[l];
                        if (lexico.contains(t.texto.toLowerCase()) || lexico.contains(t.lemma.toLowerCase())) {
                            satisfez = true;
                        }
                        if (satisfez) {
                            break;
                        }
                    }
                    if (!satisfez) {
                        try {
                            int xy = indiceUltimoToken + 1;
                            for (int l = xy; l < xy + 2; l++) {
                                Nodo t = nodos[l];
                                if (lexico.contains(t.texto.toLowerCase()) || lexico.contains(t.lemma.toLowerCase())) {
                                    satisfez = true;
                                }
                                if (satisfez) {
                                    break;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                    if (satisfez) {
                        phraseTarget = new Aspecto(tokenStr.toLowerCase());
                        phraseTarget.setLemma(lemmaStr.toLowerCase());
//                        System.out.println("Pruning 3: " + tokenStr);
//                        System.out.println(frase.texto);
                    }
                }
            }
        }
        return phraseTarget;
    }

    private Aspecto pruningMethod5(Frase frase, Nodo aspecto, Set<String> lexico) {
        Aspecto phraseTarget = null;
        Nodo[] nodos = frase.nodos;
        for (int i = 0; i < nodos.length; i++) {
            Nodo token = nodos[i];
            if (aspecto.equals(token)) {
                String tokenStr = token.texto;
                String lemmaStr = token.lemma;
                boolean modificou = false;
                int indicePrimeiroToken = i;
                int indiceUltimoToken = i;
                int index_j = 1;
                try {
                    Nodo antes = nodos[i - index_j];
                    Nodo bemAntes = nodos[i - index_j - 1];
                    if (antes.categoria.equals(Categoria_Sintatica.PREP) && Sintatico.isSubstantivo(bemAntes.categoria)) {
                        tokenStr = bemAntes.texto + " " + antes.texto.concat(" " + tokenStr);
                        lemmaStr = bemAntes.lemma + " " + antes.lemma.concat(" " + lemmaStr);
                        modificou = true;
                        indicePrimeiroToken = i - index_j - 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    Nodo apos = nodos[i + index_j];
                    Nodo bemApos = nodos[i + index_j + 1];
                    if (apos.categoria.equals(Categoria_Sintatica.PREP) && Sintatico.isSubstantivo(bemApos.categoria)) {
                        tokenStr = tokenStr.concat(" " + apos.texto) + " " + bemApos.texto;
                        lemmaStr = lemmaStr.concat(" " + apos.lemma) + " " + bemApos.lemma;
                        modificou = true;
                        indiceUltimoToken = i + index_j + 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                if (modificou) {
                    boolean satisfez = false;
                    for (int l = 0; l < indicePrimeiroToken; l++) {
                        Nodo t = nodos[l];
                        if (lexico.contains(t.texto.toLowerCase()) || lexico.contains(t.lemma.toLowerCase())) {
                            satisfez = true;
                        }
                        if (satisfez) {
                            break;
                        }
                    }
                    if (!satisfez) {
                        try {
                            int xy = indiceUltimoToken + 1;
                            for (int l = xy; l < xy + 2; l++) {
                                Nodo t = nodos[l];
                                if (lexico.contains(t.texto.toLowerCase()) || lexico.contains(t.lemma.toLowerCase())) {
                                    satisfez = true;
                                }
                                if (satisfez) {
                                    break;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                    if (satisfez) {
                        phraseTarget = new Aspecto(tokenStr.toLowerCase());
                        phraseTarget.setLemma(lemmaStr.toLowerCase());
//                        System.out.println("Pruning 5: " + target);
//                        System.out.println(frase.texto);
                    }
                }
            }
        }
        return phraseTarget;
    }
    /*
    private Aspecto pruningMethod5_1(Frase frase, Nodo aspecto) {
        Aspecto phraseTarget = null;
        Nodo[] nodos = frase.nodos;
        for (int i = 0; i < nodos.length; i++) {
            Nodo token = nodos[i];
            if (aspecto.equals(token)) {
                String tokenStr = token.texto;
                String lemmaStr = token.lemma;
                boolean modificou = false;
                int indicePrimeiroToken = i;
                int indiceUltimoToken = i;
                int index_j = 1;
                try {
                    Nodo antes = nodos[i - index_j];
                    Nodo bemAntes = nodos[i - index_j - 1];
                    if (antes.categoria.equals(Categoria_Sintatica.PREP) && Sintatico.isSubstantivo(bemAntes.categoria)) {
                        tokenStr = bemAntes.texto + " " + antes.texto.concat(" " + tokenStr);
                        lemmaStr = bemAntes.lemma + " " + antes.lemma.concat(" " + lemmaStr);
                        modificou = true;
                        indicePrimeiroToken = i - index_j - 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                try {
                    Nodo apos = nodos[i + index_j];
                    Nodo bemApos = nodos[i + index_j + 1];
                    if (apos.categoria.equals(Categoria_Sintatica.PREP) && Sintatico.isSubstantivo(bemApos.categoria)) {
                        tokenStr = tokenStr.concat(" " + apos.texto) + " " + bemApos.texto;
                        lemmaStr = lemmaStr.concat(" " + apos.lemma) + " " + bemApos.lemma;
                        modificou = true;
                        indiceUltimoToken = i + index_j + 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                }
                if (modificou) {
                    boolean satisfez = false;
                    for (int l = 0; l < indicePrimeiroToken; l++) {
                        Nodo t = nodos[l];
                        if (lexico.contains(t.texto.toLowerCase())) {
                            satisfez = true;
                        }
                        if (satisfez) {
                            break;
                        }
                    }
                    if (!satisfez) {
                        try {
                            int xy = indiceUltimoToken + 1;
                            for (int l = xy; l < xy + 2; l++) {
                                Nodo t = nodos[l];
                                if (lexico.contains(t.texto.toLowerCase())) {
                                    satisfez = true;
                                }
                                if (satisfez) {
                                    break;
                                }
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                    if (satisfez) {
                        phraseTarget = new Aspecto(tokenStr.toLowerCase());
                        phraseTarget.setLemma(lemmaStr.toLowerCase());
//                        System.out.println("Pruning 5: " + target);
//                        System.out.println(frase.texto);
                    }
                }
            }
        }
        return phraseTarget;
    }
     */
}
