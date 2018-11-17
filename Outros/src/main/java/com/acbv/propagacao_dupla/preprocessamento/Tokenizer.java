/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.preprocessamento;

import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.FrasesAux;
import com.acbv.propagacao_dupla.entidades.Nodo;
import static com.acbv.propagacao_dupla.preprocessamento.StanfordUtils.getCategoriaSintatica;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Arthur
 */
public class Tokenizer {

    private MaxentTagger maxEntTagger;

    @SuppressWarnings("")
    public Tokenizer() throws IOException, Exception {
        config();
//        getTokens();
    }

    private void config() throws IOException {
        if (this.maxEntTagger == null) {
            this.maxEntTagger = (MaxentTagger) MaxentTagger.loadModel("/home/arthur/Downloads/pt-model (2)/pos-tagger.dat");
        }
    }

//    public void getTokens(String texto) throws Exception {
//        this.parse(texto);
//    }
//    private void parse(String texto) throws Exception {
//        getTokens(texto);
//    }
    public List<String> getTokens(String texto) throws Exception {
        List<String> tokens = new ArrayList<>();
        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
        tokenizedText.forEach((sentence) -> {
            sentence.forEach((hasWord) -> {
                tokens.add(preProccess(hasWord.word()));
            });
        });
        return tokens;
    }

    public List<FrasesAux> getFrasesAux(String texto) throws Exception {
        List<FrasesAux> frases = new ArrayList<>();
        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
        for (List<HasWord> sentence : tokenizedText) {
            List<String> tokens = new ArrayList<>();
            String frase = "";
            for (HasWord hasWord : sentence) {
                tokens.add(hasWord.word());
                frase += hasWord.word() + " ";
            }
            List<String> lemmas = Arrays.asList(TreeTaggerUtils.getTreeTaggerUtils().lematizar(tokens));
            List<String> lemmasAux = new ArrayList<>();
            List<String> tokensAux = new ArrayList<>();
            for (String lemma : lemmas) {
                lemmasAux.add(removerAcentosString(lemma));
            }
            lemmas = lemmasAux;
            for (String token : tokens) {
                tokensAux.add(removerAcentosString(token));
            }
            tokens = tokensAux;
            frase = frase.trim();
            FrasesAux frasesAux = new FrasesAux(frase);
            frasesAux.setTokens(tokens);
            frasesAux.setLemmas(lemmas);
            frases.add(frasesAux);
        }
        return frases;
    }

    private static String removerAcentosString(String string) throws UnsupportedEncodingException {
        return StringUtils.stripAccents(string);
    }

    public List<Nodo> getNodos(String texto) throws Exception {
        List<Nodo> nodos = new ArrayList<>();
        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
        tokenizedText.stream().map((hwSentence) -> (ArrayList<TaggedWord>) this.maxEntTagger.tagSentence(hwSentence)).map((taggedSentence) -> preProccess2(taggedSentence)).forEachOrdered((taggedSentence) -> {
            String fraseStr = "";
            for (TaggedWord taggedWord : taggedSentence) {
                String palavra = taggedWord.word();
                String tag = taggedWord.tag();
                Categoria_Sintatica categoria_sintatica = getCategoriaSintatica(tag);
                Nodo nodo = new Nodo(palavra, categoria_sintatica);
                nodo.lemma = palavra;
                if (fraseStr.isEmpty()) {
                    fraseStr = palavra;
                } else {
                    fraseStr = fraseStr.concat(" " + palavra);
                }
                nodos.add(nodo);
            }
        });
        return nodos;
    }

    /*
    TODO: renomear
     */
    private String preProccess(String token) {
        token = token.equalsIgnoreCase("-RRB-") ? ")"
                : token.equalsIgnoreCase("-LRB-") ? "("
                : token;
        return token;
    }

    private List<TaggedWord> preProccess2(List<TaggedWord> tagged) {
        for (int i = 0; i < tagged.size(); i++) {
            TaggedWord taggedWord = tagged.get(i);
            String tag = taggedWord.tag();
            String word = taggedWord.word();
            if (word.equalsIgnoreCase("-RRB-")
                    || word.equalsIgnoreCase("-LRB-")) {
                tag = ".";
            }
            word = word.equalsIgnoreCase("-RRB-") ? ")"
                    : word.equalsIgnoreCase("-LRB-") ? "("
                    : word;
            taggedWord.setFromString(word);
            taggedWord.setTag(tag);
            tagged.set(i, taggedWord);
        }
        return tagged;
    }

}
