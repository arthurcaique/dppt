/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.preprocessamento;

import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Relacao_Dependencia;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.entidades.Tipo_Relacao;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;
import stanfordUtils.StanfordMethods;
import stanfordUtils.entidades.FraseStfd;
import stanfordUtils.entidades.RelacaoDependenciaStfd;
import stanfordUtils.entidades.TextoStfd;
import stanfordUtils.entidades.TokenStfd;

/**
 *
 * @author arthur
 */
public class PreProcessamentoCorpus {

    private static PreProcessamentoCorpus INSTANCIA;
    private final StanfordMethods stfMethods;
    private final String[] corpusStrVctr;
    private Corpus corpus;
    private final TreeTaggerUtils treeTaggerUtils;

    public static PreProcessamentoCorpus getINSTANCIA(String[] corpus) throws IOException, Exception {
        if (INSTANCIA == null) {
            INSTANCIA = new PreProcessamentoCorpus(corpus);
        }
        return INSTANCIA;
    }

    private PreProcessamentoCorpus(String[] corpus) throws IOException, Exception {
        this.stfMethods = new StanfordMethods();
        this.treeTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
        this.corpusStrVctr = corpus;
        preProcess();
    }

    private void preProcess() throws Exception {
        TextoStfd[] textos = this.stfMethods.getTextos(this.corpusStrVctr);
        Resenha[] resenhas = textosStfdToResenhas(textos);
        this.corpus = new Corpus(resenhas);
    }

    private Resenha[] textosStfdToResenhas(TextoStfd[] textos) throws Exception {
        int textosLength = textos.length;
        Resenha[] resenhas = new Resenha[textosLength];
        for (int i = 0; i < textosLength; i++) {
            TextoStfd texto = textos[i];
            System.out.println(i);
            resenhas[i] = textoStfdToResenha(texto);
        }
        return resenhas;
    }

    private Resenha textoStfdToResenha(TextoStfd texto) throws Exception {
        Resenha resenha = new Resenha(frasesStfdToFrases(texto.getFrases()), texto.getTexto());
        return resenha;
    }

    private Frase[] frasesStfdToFrases(FraseStfd[] frasesStfd) throws Exception {
        int frasesStfdLength = frasesStfd.length;
        Frase[] frases = new Frase[frasesStfdLength];
        for (int i = 0; i < frasesStfdLength; i++) {
            FraseStfd fraseStfd = frasesStfd[i];
            frases[i] = fraseStfdToFrase(fraseStfd);
        }
        return frases;
    }

    private Frase fraseStfdToFrase(FraseStfd fraseStfd) throws Exception {
        Frase frase = new Frase(fraseStfd.getFrase(), relacoesStfdToRelacoes(fraseStfd.getRelacoesDependencia()));
        frase.nodos = tokensStfdToNodos(fraseStfd.getTokens());
        return frase;
    }

    private Relacao_Dependencia[] relacoesStfdToRelacoes(RelacaoDependenciaStfd[] relacoesStfd) throws Exception {
        int relacoesStfdLength = relacoesStfd.length;
        Relacao_Dependencia[] relacoes = new Relacao_Dependencia[relacoesStfdLength];
        for (int i = 0; i < relacoesStfdLength; i++) {
            RelacaoDependenciaStfd relacao = relacoesStfd[i];
            if (relacao.getRelacao().equalsIgnoreCase("root")) {
            } else {
                relacoes[i] = relacaoStfdToRelacao(relacao);
            }
        }
        return relacoes;
    }

    private Relacao_Dependencia relacaoStfdToRelacao(RelacaoDependenciaStfd relacaoStfd) throws Exception {
        Nodo governante = tokenStfdToNodo(relacaoStfd.getGovernante());
        Nodo dependente = tokenStfdToNodo(relacaoStfd.getDependente());
        Relacao_Dependencia relacao_dependencia = new Relacao_Dependencia(governante, dependente, getTipoRelacao(relacaoStfd.getRelacao()));
        return relacao_dependencia;
    }

    private Nodo[] tokensStfdToNodos(TokenStfd[] tokensStfd) throws Exception {
        int tokensStfdLength = tokensStfd.length;
        Nodo[] nodos = new Nodo[tokensStfdLength];
        for (int i = 0; i < tokensStfdLength; i++) {
            TokenStfd tokenStfd = tokensStfd[i];
            nodos[i] = tokenStfdToNodo(tokenStfd);
        }
        return nodos;
    }

    private Nodo tokenStfdToNodo(TokenStfd tokenStfd) throws Exception {
        String nodoSemAcento = removerAcentosString(tokenStfd.getToken());
        Nodo nodo = new Nodo(nodoSemAcento, getCategoriaSintatica(tokenStfd.getCategoriaSintatica()));
        nodo.indice = tokenStfd.getIndice();
        nodo.lemma = tokenStfd.getLemma();
        return nodo;
    }

    public Corpus getCorpus() {
        return corpus;
    }

    private static Categoria_Sintatica getCategoriaSintatica(String tag) {
        Categoria_Sintatica categoria_sintatica;
        if (tag.equalsIgnoreCase("ADV")) {
            categoria_sintatica = Categoria_Sintatica.RB;
        } else if (tag.equalsIgnoreCase("ADJ")) {
            categoria_sintatica = Categoria_Sintatica.JJ;
        } else if (tag.contains("NOUN")) {
            categoria_sintatica = Categoria_Sintatica.NN;
        } else if (tag.contains("DET")) {
            categoria_sintatica = Categoria_Sintatica.DT;
        } else if (tag.contains("VERB")) {
            categoria_sintatica = Categoria_Sintatica.VB;
        } else if (tag.contains("ADP")) {
            categoria_sintatica = Categoria_Sintatica.PREP;
        } else if (tag.equalsIgnoreCase(".")) {
            categoria_sintatica = Categoria_Sintatica.PUNCT;
        } else {
            categoria_sintatica = Categoria_Sintatica.OUTRAS;
        }
        return categoria_sintatica;
    }

    private Tipo_Relacao getTipoRelacao(String relacao) {
        relacao = relacao.toUpperCase();
        Tipo_Relacao tipoRelacao = Tipo_Relacao.getTipoRelacao(relacao);
        return tipoRelacao;
    }

    private static String removerAcentosString(String string) throws UnsupportedEncodingException {
        return StringUtils.stripAccents(string);
    }
}
