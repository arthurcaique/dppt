/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.dpbr2.DoublePropagation;
import com.acbv.propagacao_dupla.dpbr2.NewAspectPruning;
import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.ResenhaAux;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import com.acbv.propagacao_dupla.utils.lexicoUtils.LexicoUtils;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class TesteDoublePropagation2 {

    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        Controller controller = Controller.getController();
        List<ResenhaAux> resenhaAux = NewAspectPruning.getResenhasNotebooks();
        Corpus corpus = controller.getCorpusMD101BzOriginais();
        Set<String> stopSubsWords = new HashSet<>();
        HashMap<String, Set<String>> lexicos = getLexicos();
        Set<Map.Entry<String, Set<String>>> lexicos_es = lexicos.entrySet();
        HashMap<String, Set<String>> aspectos2 = getAspectosMD101BZ();
        for (Map.Entry<String, Set<String>> lexicos_e : lexicos_es) {
            Set<String> lexico = lexicos_e.getValue();
            DoublePropagation dp = new DoublePropagation();
            dp.runDP(corpus, lexico, stopSubsWords);
            Set<Aspecto> aspectosExtraidosDp = dp.aspectosExtraidos;
            NewAspectPruning.setSuperSets(aspectosExtraidosDp);
            for (double p_support = 0.1; p_support < 1.0; p_support += 0.2) {
                for (int freq = 2; freq <= 10; freq += 2) {
                    Set<Aspecto> aspectosExtraidos = new HashSet<>(aspectosExtraidosDp);
                    NewAspectPruning.removerAspectosFreq(resenhaAux, aspectosExtraidos, freq);
                    NewAspectPruning.doPSupportBasedPruning3(aspectosExtraidos, resenhaAux, p_support);
                    NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
                    Set<Aspecto> aspectosExtraidos2 = new HashSet<>(aspectosExtraidos);
                    Set<String> aspectosStr = getAspectosStr(aspectosExtraidos2);
                    System.out.println("Léxico: " + lexicos_e.getKey()
                            + "\tp-support: " + p_support);
                    getNovaAvaliacao2(aspectosStr, aspectos2);
                }
            }
        }
    }

    private static Set<String> getAspectosRelevantes(HashMap<String, Set<String>> aspectos) {
        Set<String> aspectosRelevantes = new HashSet<>();
        Set<Map.Entry<String, Set<String>>> entrySet = aspectos.entrySet();
        entrySet.stream().map((entry) -> {
            aspectosRelevantes.add(entry.getKey());
            return entry;
        }).map((entry) -> entry.getValue()).forEachOrdered((value) -> {
            value.forEach((string) -> {
                aspectosRelevantes.add(string);
            });
        });
        return aspectosRelevantes;
    }

    private static HashMap<String, Set<String>> getLexicos() throws IOException {
        HashMap<String, Set<String>> lexicos = new HashMap<>();
        lexicos.put("SentiLex_Flex", LexicoUtils.getLexicoSentiLex_Flex());
        lexicos.put("SentiLex_Lem", LexicoUtils.getLexicoSentiLex_Lem());
        lexicos.put("LIWC_PTBR", LexicoUtils.getLexicoLIWC_PTBR());
        lexicos.put("OPLex", LexicoUtils.getLexicoOpLex());
        return lexicos;
    }

    private static void printNameOfLexico(int i) {
        System.out.println("Léxico "
                + (i == 0 ? "SentiLex Flex"
                        : i == 1 ? "SentiLex Lem"
                                : i == 2 ? "LIWC"
                                        : "OP Lex"));
    }

    public static Set<String> getAspectosStr(Set<Aspecto> aspectos) {
        Set<String> aspectosStr = new HashSet<>();
        aspectos.forEach((aspecto) -> {
            try {
                aspectosStr.add(utils.StringUtils.removerAcentosString(aspecto.getLemma().toLowerCase()));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(TestesDoublePropagation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e) {
                System.out.println("ERRO, DANADO.");
                System.out.println(aspecto.getAspecto());
            }
        });
        return aspectosStr;
    }

    private static HashMap<String, Set<String>> getAspectos4() throws ParseException, FileNotFoundException, IOException, Exception {
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

    private static HashMap<String, Set<String>> getAspectosMD101BZ() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/md101_bz/aspectos");
    }

    private static HashMap<String, Set<String>> getAspectos_un40d6500_40_polegadas() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/samsung_un40d6500_40_polegadas_led_plana/aspectos");
    }

    private static HashMap<String, Set<String>> getAspectos_MotoG_XT_1032_8Gb() throws ParseException, FileNotFoundException, IOException, Exception {
        return getAspectos("/home/arthur/Documentos/Dissertacao Data/smartphone_motorola_motog_xt1032_8gb/aspectos");
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

    private static List<Frase> getFrasesCorpus(Corpus corpus) {
        List<Frase> frases = new ArrayList<>();
        corpus.resenhas.stream().map((resenha) -> Arrays.asList(resenha.frases)).forEachOrdered((frasesResenha) -> {
            frases.addAll(frasesResenha);
        });
        return frases;
    }

}
