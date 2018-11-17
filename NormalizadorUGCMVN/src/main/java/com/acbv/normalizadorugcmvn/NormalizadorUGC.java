/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.normalizadorugcmvn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author arthur
 */
public class NormalizadorUGC {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        List<String> dicionario = getDicionario();
        List<Texto> corpus = getCorpus();
        getTokensInexistentesDicionario(corpus, dicionario);
//        Set<String> tokensInexistenteNoDicionario = getTokensInexistentesDicionario(corpus, dicionario);
//        tokensInexistenteNoDicionario.forEach((string) -> {
//            System.out.println(string);
//        });
    }

    private static List<String> getDicionario() throws IOException {
        System.out.println("Pegando dicionário...");
        List<String> dict = Files.readAllLines(Paths.get("/home/arthur/Documentos/Delaf2015v04_2.dic"));
        System.out.println("Dicionário pego!!!");
        return dict;
    }

    private static List<Texto> getCorpus() throws IOException, Exception {
//        System.out.println("Pegando córpus...");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8").toLowerCase();
        String[] reviewsStr = fileText.split("\\[r\\]");
        List<Texto> corpus = new ArrayList<>();
        Texto texto;
        for (String review : reviewsStr) {
            if (!review.trim().isEmpty()) {
                texto = new Texto(review, Tokenizer.getFrases(review));
                corpus.add(texto);
            }
        }
//        System.out.println("Córpus pego!!!");
        return corpus;
    }

    private static void getTokensInexistentesDicionario(List<Texto> corpus, List<String> dicionario) {
        System.out.println("Pegando tokens inexistentes...");
        for (Texto texto : corpus) {
            System.out.println("______________________________________________________________________________________________________________________________");
            System.out.println("TEXTO:" + texto.texto);
            List<Frase> frases = texto.frases;
            for (Frase frase : frases) {
                List<String> tokens = frase.tokens;
                for (String token : tokens) {
                    String tokenAux = removerCaracteresEspeciais(token.toLowerCase());
                    if (!tokenAux.trim().isEmpty() && !dicionario.contains(token)) {
                        System.out.println("TOKEN NÃO IDENTIFICADO: " + token);
                    }
                }
            }
            System.out.println("______________________________________________________________________________________________________________________________");
        }
        System.out.println("Tokens inexistentes pegos!");
//        return tokensInexistentesDicionario;
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

}
