/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.dpbr2.NewAspectPruning;
import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import com.acbv.propagacao_dupla.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.NGrams;
import static utils.StringUtils.removerAcentosString;

/**
 *
 * @author arthur
 */
public class WhatMatter {

    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /* TO DO
        Tarefa 1 (Pré processamento) - DONE
        Tarefa 2 (Substantivos mais frequentes) - DONE
        Tarefa 3 (Substantivos relevantes) - DONE
        Tarefa 4 (Mapeamento de Indicadores) - 
        Tarefa 5 (Remoção de substantivos não relacionados) - 
     */
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Corpus corpus = Controller.getController().getCorpus305E4A();
        Set<String> stopList = getStopList();
        HashMap<String, Set<String>> aspectosCorpus305E4A = getAspectosCorpus305E4A();
        for (int i = 1; i <= 100; i++) {
            List<Resenha> resenhas = corpus.resenhas;
            Set<String> substantivosFrequentes = getSubstantivosFrequentes(resenhas, i);
            Set<String> novosSubstantivos = getSubstantivosRelevantes(resenhas, substantivosFrequentes, stopList);
            substantivosFrequentes.addAll(novosSubstantivos);
            NewAspectPruning.removerAspectosStopWords2(novosSubstantivos);
//        novosSubstantivos.forEach((novosSubstantivo) -> {
//            System.out.println(novosSubstantivo);
//        });
            System.out.println("Percent " + i);
            getNovaAvaliacao2(substantivosFrequentes, aspectosCorpus305E4A);
        }
    }

    private static void getNovaAvaliacao2(Set<String> aspectosStr, HashMap<String, Set<String>> aspectosExpressoes) throws UnsupportedEncodingException, Exception {
        aspectosExpressoes = tirarAcentosHashMap2(aspectosExpressoes);
        aspectosExpressoes = removerStopWordsHashMap2(aspectosExpressoes);
        aspectosStr = removerStopWordsSet(aspectosStr);
        int n_extraidos_relevantes = 0;
        for (String aspecto : aspectosStr) {
            if (aspectosExpressoes.containsKey(aspecto) || getContainsExpressao(aspecto, aspectosExpressoes.values())) {
                n_extraidos_relevantes++;
            }
        }
        double recall = (n_extraidos_relevantes * 1.00) / aspectosExpressoes.size();
        double precision = (n_extraidos_relevantes * 1.00) / aspectosStr.size();
        double f_measure = 2 * ((precision * recall) / (precision + recall));
        System.out.println("Avaliacao{recall=" + recall + ", precision=" + precision + ", f_measure=" + f_measure + "}");
    }

    private static HashMap<String, Set<String>> tirarAcentosHashMap2(HashMap<String, Set<String>> aspectosMD101Bz) throws UnsupportedEncodingException {
        HashMap<String, Set<String>> aux = new HashMap<>();
        Set<Map.Entry<String, Set<String>>> entrySet = aspectosMD101Bz.entrySet();
        for (Map.Entry<String, Set<String>> entry : entrySet) {
            Set<String> expressoes = new HashSet<>();
            Set<String> value = entry.getValue();
            for (String string : value) {
                expressoes.add(string.toLowerCase());
            }
            aux.put(removerAcentosString(entry.getKey()).toLowerCase(), expressoes);
        }
        return aux;
    }

    private static Set<String> getStopWords() throws IOException {
        List<String> stopWordsAux = Files.readAllLines(Paths.get("/home/arthur/Dropbox/Dissertacao/stopwords.txt"));
        Set<String> stopWords = new HashSet<>();
        stopWordsAux.stream().map((stopWord) -> stopWord.trim().toLowerCase()).forEachOrdered((stopWord) -> {
            try {
                stopWords.add(removerAcentosString(stopWord));
            } catch (UnsupportedEncodingException ex) {
            }
        });
        return stopWords;
    }

    private static Set<String> removerStopWordsSet(Set<String> aspectosMD101Bz) throws Exception {
        Set<String> aspectosSemStopWords = new HashSet<>();
        Set<String> stopWords = getStopWords();
        Tokenizer t = new Tokenizer();
        String aspectoAux;
        for (String aspecto : aspectosMD101Bz) {
            aspectoAux = "";
            List<String> tokensAspecto = t.getTokens(aspecto);
            aspectoAux = tokensAspecto.stream().filter((tokenAspecto) -> (!stopWords.contains(tokenAspecto))).map((tokenAspecto) -> tokenAspecto + " ").reduce(aspectoAux, String::concat);
            aspectoAux = aspectoAux.trim();
            aspectosSemStopWords.add(aspectoAux);
        }
        return aspectosSemStopWords;
    }

    private static boolean getContainsExpressao(String aspecto, Collection<Set<String>> expressao) {
        boolean contains = false;
        for (Set<String> set : expressao) {
            if (set.contains(aspecto)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private static HashMap<String, Set<String>> removerStopWordsHashMap2(HashMap<String, Set<String>> aspectosMD101Bz) throws Exception {
        HashMap<String, Set<String>> aux = new HashMap<>();
        Set<String> stopWords = getStopWords();
        Tokenizer t = new Tokenizer();
        String aspectoAuxKey, aspectoAuxValue;
        Set<Map.Entry<String, Set<String>>> entrySet = aspectosMD101Bz.entrySet();
        for (Map.Entry<String, Set<String>> entry : entrySet) {
            aspectoAuxKey = "";
            List<String> tokensAspectoKey = t.getTokens(entry.getKey().toLowerCase());
            aspectoAuxKey = tokensAspectoKey.stream().filter((tokenAspecto) -> (!stopWords.contains(tokenAspecto))).map((tokenAspecto) -> tokenAspecto + " ").reduce(aspectoAuxKey, String::concat);
            aspectoAuxKey = aspectoAuxKey.trim();

            Set<String> expressoes = new HashSet<>();
            Set<String> expressoesAux = entry.getValue();
            for (String expressao : expressoesAux) {
                aspectoAuxValue = "";
                List<String> tokensAspectoValue = t.getTokens(expressao.toLowerCase());
                aspectoAuxValue = tokensAspectoValue.stream().filter((tokenAspecto) -> (!stopWords.contains(tokenAspecto))).map((tokenAspecto) -> tokenAspecto + " ").reduce(aspectoAuxValue, String::concat);
                aspectoAuxValue = aspectoAuxValue.trim();
                expressoes.add(aspectoAuxValue);
            }
            aux.put(aspectoAuxKey, expressoes);
        }
        return aux;
    }
//    private static List<Resenha> getReviews() throws Exception {
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Área de Trabalho/experimentos/1")), "UTF-8");
//        String[] reviewsStr = fileText.split("\\[r\\]");
//        List<String> corpus = Arrays.asList(reviewsStr);
//        List<Resenha> resenhas = stfUtils.getResenhas(corpus);
//        Iterator<Resenha> iterator = resenhas.iterator();
//        while (iterator.hasNext()) {
//            Resenha resenha = iterator.next();
//            if (resenha.texto.trim().isEmpty()) {
//                iterator.remove();
//            }
//        }
//        return resenhas;
//    }
//    private static List<Resenha> getReviewsMd101bz() throws Exception {
//        File[] listFiles = new File("/home/arthur/Documentos/searchfoot/md101bz").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
//        List<File> arquivosTxt = Arrays.asList(listFiles);
//        List<String> corpus = new ArrayList<>();
//        for (File file : arquivosTxt) {
//            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
//            String resenhaAux = "";
//            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
//            corpus.add(resenhaAux);
//        }
//        List<Resenha> resenhas = stfUtils.getResenhas(corpus);
//        Iterator<Resenha> iterator = resenhas.iterator();
//        while (iterator.hasNext()) {
//            Resenha resenha = iterator.next();
//            if (resenha.texto.trim().isEmpty()) {
//                iterator.remove();
//            }
//        }
//        return resenhas;
//    }
//    public static List<Resenha> getCorpus305E4A() throws IOException, Exception {
//        File[] listFiles = new File("/home/arthur/Documentos/305E4A/resenhas_ugc_normal").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
//        List<File> arquivosTxt = Arrays.asList(listFiles);
//        List<String> resenhasStr = new ArrayList<>();
//        for (File file : arquivosTxt) {
//            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
//            String resenhaAux = "";
//            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
//            resenhasStr.add(resenhaAux);
//        }
//        List<Resenha> resenhas = stfUtils.getResenhas(resenhasStr);
//        Iterator<Resenha> iterator = resenhas.iterator();
//        while (iterator.hasNext()) {
//            Resenha resenha = iterator.next();
//            if (resenha.texto.trim().isEmpty()) {
//                iterator.remove();
//            }
//        }
//        return resenhas;
//    }

    private static HashMap<String, Set<String>> getAspectosCorpus305E4A() throws ParseException, FileNotFoundException, IOException, Exception {
        HashMap<String, Set<String>> aspectosResenha = new HashMap<>();
        File dir2 = new File("/home/arthur/Documentos/305E4A/resenhas_ugc_normal/");
        String[] listFiles = dir2.list((File dir, String name) -> name.endsWith(".json"));
        Tokenizer t = new Tokenizer();
        TreeTaggerUtils treeTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
        for (String arqResenha : listFiles) {
            arqResenha = dir2.getPath() + "/" + arqResenha;
            if (Files.exists(Paths.get(arqResenha))) {
                org.json.simple.parser.JSONParser parser = new JSONParser();
                JSONObject resenhaJSon = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(arqResenha)));
                String resenhaDesc = ((String) resenhaJSon.get(RESENHA_TAG)).toLowerCase();
                List<String> tokens = t.getTokens(resenhaDesc);
                String[] lemmas = treeTaggerUtils.lematizar(tokens);
                JSONArray aspectosJson = (JSONArray) resenhaJSon.get(ASPECTOS_TAG);
                Iterator iterator = aspectosJson.iterator();
                while (iterator.hasNext()) {
                    JSONObject aspectoJson = (JSONObject) iterator.next();
                    String expressao = (String) aspectoJson.get("expressao");
                    String entidade = (String) aspectoJson.get("entidade");
                    String aspecto = (String) aspectoJson.get("aspecto");
                    String sentimento = (String) aspectoJson.get("sentimento");
                    Set<String> expressoes = new HashSet<>();
//                    System.out.println("Aspecto: " + aspecto + "\tExpressão: " + expressao);
                    aspecto = aspecto.trim().toLowerCase();
                    expressao = expressao.trim().toLowerCase();
                    List<String> tokensExpressao = t.getTokens(expressao);
                    List<String> tokensAspecto = t.getTokens(aspecto);
                    Integer[] indexesExpressao = getIndexes(tokens, tokensExpressao);
                    expressao = search(lemmas, indexesExpressao[0], indexesExpressao[1]);
                    Integer[] indexesAspecto = getIndexes(tokens, tokensAspecto);
                    aspecto = search(lemmas, indexesAspecto[0], indexesAspecto[1]);
//                    System.out.println("Aspecto: " + aspecto + "\tExpressão: " + expressao);
                    if (aspectosResenha.containsKey(aspecto)) {
                        expressoes = aspectosResenha.get(aspecto);
                        expressoes.add(expressao);
                        aspectosResenha.replace(aspecto, expressoes);
                    } else {
                        expressoes.add(expressao);
                        aspectosResenha.put(aspecto, expressoes);
                    }
                }
            }
        }
        return aspectosResenha;
    }

    private static String search(String[] tokens, int inicio, int fim) {
        String token = "";
        if (inicio == fim) {
            token = tokens[inicio];
        } else {
            for (int i = inicio; i <= fim; i++) {
                String tokenAux = tokens[i];
                token += tokenAux + " ";
            }
        }
        token = token.trim();
        return token;
    }

    private static Integer[] getIndexes(List<String> tokensTexto, List<String> tokensAspecto) {
        int n = tokensAspecto.size();
        String[] tokensAspectoArray = tokensAspecto.toArray(new String[tokensAspecto.size()]);
        List<String[]> nGrams = NGrams.getNGrams(tokensTexto, n);
        int inicio = 0;
        for (String[] nGram : nGrams) {
            if (Arrays.deepEquals(nGram, tokensAspectoArray)) {
                break;
            }
            inicio++;
        }
        int fim = inicio + n - 1;
        Integer[] indexes = new Integer[2];
        indexes[0] = inicio;
        indexes[1] = fim;
        return indexes;
    }

    public static Set<String> getAspectosMD101Bz() throws IOException {
        File extraidosFile = new File("/home/arthur/Documentos/md101bz/aspectos");
        List<String> aspectosExtraidos = Files.readAllLines(extraidosFile.toPath());
        Set<String> aspectosExtraidosSet = new HashSet<>(aspectosExtraidos);
        return aspectosExtraidosSet;
    }

//    private static Set<String> getExpectedAspects() throws IOException {
//        String expectedStr = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/aspectos")), "UTF-8");
//        List<String> expectedAux = Arrays.asList(expectedStr.split("\n"));
//        Set<String> expectedAux_ = new HashSet<>();
//        expectedAux.forEach((string) -> {
//            expectedAux_.add(methods.Utils.removerAcentos(string.trim().toLowerCase()));
//        });
//        return expectedAux_;
//    }
    private static Set<String> getStopList() throws IOException {
        String expectedStr = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/stopwords.txt")), "UTF-8");
        List<String> expectedAux = Arrays.asList(expectedStr.split("\n"));
        Set<String> expectedAux_ = new HashSet<>();
        expectedAux.forEach((string) -> {
            if (!string.isEmpty()) {
                expectedAux_.add(Utils.removerAcentos(string.trim().toLowerCase()));
            }
        });
        return expectedAux_;
    }

    private static Set<String> getSubstantivosFrequentes(List<Resenha> reviews, int percgiven) {
        Map<String, Integer> substantivos = new TreeMap<>();
        reviews.stream().map((review) -> review.frases).forEachOrdered((frases) -> {
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (Nodo nodo : nodos) {
                    if (nodo.categoria == Categoria_Sintatica.NN || nodo.categoria == Categoria_Sintatica.NNS) {
                        String substantivo = nodo.lemma.toLowerCase();
                        substantivos.putIfAbsent(substantivo, 0);
                        Integer freqAnterior = substantivos.get(substantivo);
                        Integer novaFreq = freqAnterior + 1;
                        substantivos.replace(substantivo, novaFreq);
                    }
                }
            }
        });
        int numSubstantivos = substantivos.size();
        int perc = (numSubstantivos / 100) * percgiven;
        List<Entry<String, Integer>> entriesSortedByValues = entriesSortedByValues(substantivos);
        List<Entry<String, Integer>> subList = entriesSortedByValues.subList(0, perc - 1);
        Set<String> substantivosFreq = new HashSet<>();
        subList.forEach((entry) -> {
            substantivosFreq.add(entry.getKey());
        });
        return substantivosFreq;
    }

    static <K, V extends Comparable<? super V>>
            List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (Entry<K, V> e1, Entry<K, V> e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }

    private static Set<String> getSubstantivosRelevantes(List<Resenha> reviews, Set<String> substantivosFrequentes, Set<String> stopList) {
        Set<String> adjetivosRelacionados = new HashSet<>();
        for (Resenha review : reviews) {
            Frase[] frases = review.frases;
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (int i = 0; i < nodos.length; i++) {
                    Nodo nodo = nodos[i];
                    if (substantivosFrequentes.contains(nodo.texto.toLowerCase())) {
//                        boolean temAntes = false;
                        for (int j = i; j > 0; j--) {
                            Nodo nodo_antes = nodos[j];
                            if (nodo_antes.categoria == Categoria_Sintatica.JJ) {
                                adjetivosRelacionados.add(nodo_antes.texto.toLowerCase());
                            } else if (nodo.categoria == Categoria_Sintatica.PREP || stopList.contains(nodo.texto.toLowerCase())) {
                                //Faz nada
                            } else {
                                break;
                            }
                        }
                        int k = i + 1;
                        for (int j = k; j < nodos.length; j++) {
                            Nodo nodo_depois = nodos[j];
                            if (nodo_depois.categoria == Categoria_Sintatica.JJ) {
                                adjetivosRelacionados.add(nodo_depois.texto.toLowerCase());
                            } else if (nodo.categoria == Categoria_Sintatica.PREP || stopList.contains(nodo.texto.toLowerCase())) {
                                //Faz nada
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        Set<String> novosSubstantivos = new HashSet<>();
        for (Resenha review : reviews) {
            Frase[] frases = review.frases;
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (int i = 0; i < nodos.length; i++) {
                    Nodo nodo = nodos[i];
                    if (adjetivosRelacionados.contains(nodo.texto.toLowerCase())) {
//                        boolean temAntes = false;
                        for (int j = i; j > 0; j--) {
                            Nodo nodo_antes = nodos[j];
                            String nodo_antes_str = nodo_antes.texto.toLowerCase();
                            if (nodo_antes.categoria == Categoria_Sintatica.NN && !substantivosFrequentes.contains(nodo_antes_str)) {
                                novosSubstantivos.add(nodo_antes_str);
                            } else if (nodo.categoria == Categoria_Sintatica.PREP || stopList.contains(nodo.texto.toLowerCase())) {
                                //Faz nada
                            } else {
                                break;
                            }
                        }
                        int k = i + 1;
                        for (int j = k; j < nodos.length; j++) {
                            Nodo nodo_depois = nodos[j];
                            String nodo_depois_str = nodo_depois.texto.toLowerCase();
                            if (nodo_depois.categoria == Categoria_Sintatica.NN && !substantivosFrequentes.contains(nodo_depois_str)) {
                                substantivosFrequentes.add(nodo_depois_str);
                            } else if (nodo.categoria == Categoria_Sintatica.PREP || stopList.contains(nodo.texto.toLowerCase())) {
                                //Faz nada
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return novosSubstantivos;
    }

}
