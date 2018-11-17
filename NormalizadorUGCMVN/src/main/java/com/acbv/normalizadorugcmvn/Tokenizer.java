/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.normalizadorugcmvn;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author arthur
 */
public class Tokenizer {

    public static List<Frase> getFrases(String texto) throws Exception {
        List<Frase> frases = new ArrayList<>();
        List<List<HasWord>> tokenizedText = MaxentTagger.tokenizeText(new StringReader(texto));
        Frase frase;
        List<String> tokens;
        for (List<HasWord> list : tokenizedText) {
            System.out.println(list.toString());
            frase = new Frase();
            tokens = new ArrayList<>();
            for (HasWord hasWord : list) {
                tokens.add(hasWord.word());
            }
            frase.tokens = tokens;
//            recuperarAcentos(tokens, tokensComAcento);
            frases.add(frase);
        }
//        tokenizedText.forEach((hwSentence) -> {
//            tokens.add(hwSentence.toString());
//        });
        return frases;
    }

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String removerCaracteresEspeciais(String str) {
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match = pt.matcher(str);
        while (match.find()) {
            String s = match.group();
            str = str.replaceAll("\\" + s, "");
        }
        return str;
    }
    
    private static List<String> recuperarAcentos(List<String> tokensSemAcento, String[] tokensComAcento){
        int j = 0;
        for (int i = 0; i < tokensSemAcento.size(); i++) {
            String tokenSemAcento = removerAcentos(tokensSemAcento.get(i));
            if(!tokenSemAcento.trim().isEmpty()){
                for (int k = j; k < tokensComAcento.length; k++) {
                    String tokenComAcento = tokensComAcento[k];
                    String tokenComAcentoAux = removerAcentos(removerCaracteresEspeciais(tokenComAcento));
                    if(!tokenComAcentoAux.trim().isEmpty() && tokenComAcentoAux.equalsIgnoreCase(tokenSemAcento)){
                        tokensSemAcento.set(i, tokenComAcento);
                        j = k;
                        break;
                    }
                }
            }
        }
        System.out.println(tokensSemAcento);
        return tokensSemAcento;
    }
}
