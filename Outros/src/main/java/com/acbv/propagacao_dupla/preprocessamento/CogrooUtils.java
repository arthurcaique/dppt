/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.preprocessamento;

import java.awt.Component;
import java.util.List;
import java.util.Locale;
import org.cogroo.analyzer.Analyzer;
import org.cogroo.analyzer.ComponentFactory;
import org.cogroo.text.Document;
import org.cogroo.text.Sentence;
import org.cogroo.text.Token;
import org.cogroo.text.impl.DocumentImpl;

/**
 *
 * @author arthur
 */
public class CogrooUtils {

    private final ComponentFactory factory;
    private final Analyzer pipe;

    public CogrooUtils() {
        this.factory = ComponentFactory.create(new Locale("pt", "BR"));
        this.pipe = this.factory.createPipe();
//        this.lemmatizer = this.factory.createLemmatizer();
    }

    public String lemmatizarTexto(String texto) {
        String outcome = "";
        Document document = new DocumentImpl();

        document.setText(texto);
        this.pipe.analyze(document);

        for (Sentence sentence : document.getSentences()) {
            List<Token> tokens = sentence.getTokens();
            for (Token token : tokens) {
                String[] lemmas = token.getLemmas();
                String lemma2 = "";
                for (String lemma : lemmas) {
                    System.out.println();
                }
                outcome = outcome.concat(" " + lemma2);
            }
        }

        outcome = outcome.trim();
        return outcome;
    }

}
