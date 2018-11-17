/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.Avaliacao;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
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
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final static String RESENHA_TAG = "texto";

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
        Controller c = Controller.getController();
        Corpus corpus = c.getCorpusMotoG_XT_1032_8GbNormal();
        List<Resenha> resenhas = corpus.resenhas;
        HashMap<String, Set<String>> aspectosMD101BZ = getAspectos_MotoG_XT_1032_8GbUGC();
        Set<String> stopList = getStopList();
        for (int i = 1; i <= 100; i++) {
            Set<String> substantivosFrequentes = getSubstantivosFrequentes(resenhas, i);
            Set<String> novosSubstantivos = getSubstantivosRelevantes(resenhas, substantivosFrequentes, stopList);
            substantivosFrequentes.addAll(novosSubstantivos);
            String avalicao = getNovaAvaliacao2(substantivosFrequentes, aspectosMD101BZ);
            System.out.println("Perc " + i + " :" + avalicao);
        }
    }

    private static HashMap<String, Set<String>> getAspectos(String dir_files) throws ParseException, FileNotFoundException, IOException, Exception {
        HashMap<String, Set<String>> aspectosResenha = new HashMap<>();
        File dir2 = new File(dir_files);
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
                    if (!Objects.isNull(aspectoJson)) {
                        String expressao = (String) aspectoJson.get("expressao");
//                    String entidade = (String) aspectoJson.get("entidade");
                        String aspecto = (String) aspectoJson.get("aspecto");
//                    String sentimento = (String) aspectoJson.get("sentimento");
                        if (Objects.nonNull(aspecto)) {
                            Set<String> expressoes = new HashSet<>();
                            expressao = expressao.trim().toLowerCase();
                            List<String> tokensExpressao = t.getTokens(expressao);
                            Integer[] indexesExpressao = getIndexes(tokens, tokensExpressao);
                            expressao = search(lemmas, indexesExpressao[0], indexesExpressao[1]);
                            aspecto = aspecto.trim().toLowerCase();
                            List<String> tokensAspecto = t.getTokens(aspecto);
                            Integer[] indexesAspecto = getIndexes(tokens, tokensAspecto);
                            aspecto = search(lemmas, indexesAspecto[0], indexesAspecto[1]);
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
            }
        }
        return aspectosResenha;
    }

    private static String search(String[] tokens, int inicio, int fim) {
        String token = "";
        try {
            if (inicio == fim) {
                token = tokens[inicio];
            } else {
                for (int i = inicio; i <= fim; i++) {
                    String tokenAux = tokens[i];
                    token += tokenAux + " ";
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("EITA");
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

    private static HashMap<String, Set<String>> getAspectosMD101BZ() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/MD101BZ/Aspectos/");
    }

    private static HashMap<String, Set<String>> getAspectosUGCMD101BZ() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/UGC/MD101BZ/Aspectos/");
    }

    private static HashMap<String, Set<String>> getAspectos_un40d6500_40_polegadas() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/UN40D6500/Aspectos/");
    }

    private static HashMap<String, Set<String>> getAspectos_un40d6500_40_polegadasUGC() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/UGC/UN40D6500/Aspectos/");
    }

    private static HashMap<String, Set<String>> getAspectos_MotoG_XT_1032_8Gb() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/XT1032/Aspectos/");
    }

    private static HashMap<String, Set<String>> getAspectos_MotoG_XT_1032_8GbUGC() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/UGC/XT1032/Aspectos");
    }

    private static String getNovaAvaliacao2(Set<String> aspectosStr, HashMap<String, Set<String>> aspectosExpressoes) throws UnsupportedEncodingException, Exception {
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
        return ("Avaliacao{recall=" + recall + ", precision=" + precision + ", f_measure=" + f_measure + "}");
    }

    private static HashMap<String, Set<String>> removerStopWordsHashMap2(HashMap<String, Set<String>> aspectos) throws Exception {
        HashMap<String, Set<String>> aux = new HashMap<>();
        Set<String> stopWords = getStopWords();
        Tokenizer t = new Tokenizer();
        String aspectoAuxKey, aspectoAuxValue;
        Set<Map.Entry<String, Set<String>>> entrySet = aspectos.entrySet();
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

    private static Set<String> removerStopWordsSet(Set<String> aspectos) throws Exception {
        Set<String> aspectosSemStopWords = new HashSet<>();
        Set<String> stopWords = getStopWords();
        Tokenizer t = new Tokenizer();
        String aspectoAux;
        for (String aspecto : aspectos) {
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

    private static Set<String> getStopList() throws IOException {
        String expectedStr = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/stopwords.txt")), "UTF-8");
        List<String> expectedAux = Arrays.asList(expectedStr.split("\n"));
        Set<String> expectedAux_ = new HashSet<>();
        expectedAux.forEach((string) -> {
            if (!string.isEmpty()) {
                try {
                    expectedAux_.add(utils.StringUtils.removerAcentosString(string.toLowerCase()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(WhatMatter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        return expectedAux_;
    }

    private static Set<String> getSubstantivosFrequentes(List<Resenha> resenhas, int percgiven) {
        Map<String, Integer> substantivos = new TreeMap<>();
        resenhas.stream().map((review) -> review.frases).forEachOrdered((frases) -> {
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
