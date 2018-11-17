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
import com.acbv.propagacao_dupla.entidades.ResenhaAux;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import com.acbv.propagacao_dupla.utils.lexicoUtils.LexicoUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import java.util.Objects;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static testes.TesteDoublePropagation2.getAspectosStr;
import utils.NGrams;
import static utils.StringUtils.removerAcentosString;

/**
 *
 * @author arthur
 */
public class DPPT_ALL {

    private static Controller controller;
    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        controller = Controller.getController();
        List<JSONObject> dataSet = getDataSets();
        for (JSONObject obj : dataSet) {
            HashMap<String, Set<String>> lexicos = getLexicos();
            Corpus corpusOriginal = (Corpus) obj.get("Corpus Original");
//            Corpus ugcNormal = (Corpus) obj.get("UGCNormal");
//            HashMap<String, Set<String>> aspectosUGC = (HashMap<String, Set<String>>) obj.get("AspectosUGCNormal");
            HashMap<String, Set<String>> aspectos = (HashMap<String, Set<String>>) obj.get("Aspectos");
//            List<ResenhaAux> resenhasUGCAux = (List<ResenhaAux>) obj.get("Resenhas UGC Aux");
            String produto = (String) obj.get("Produto");
            run_no_extra_prun(lexicos, corpusOriginal, aspectos, produto, "sem_normalizacao");
//            run_all(lexicos, corpusOriginal, resenhasUGCAux, aspectosUGC, produto, "sem_normalizacao");
//            run_all(lexicos, ugcNormal, resenhasUGCAux, aspectosUGC, produto, "ugc_normal");
//            lexicos = getLexicos();
//            run_no_freq_prun(lexicos, corpusOriginal, resenhasUGCAux, aspectosUGC, produto, "sem_normalizacao");
//            run_no_freq_prun(lexicos, ugcNormal, resenhasUGCAux, aspectosUGC, produto, "ugc_normal");
//            lexicos = getLexicos();
//            run_no_support_prun(lexicos, corpusOriginal, resenhasUGCAux, aspectosUGC, produto, "sem_normalizacao");
//            run_no_support_prun(lexicos, ugcNormal, resenhasUGCAux, aspectosUGC, produto, "ugc_normal");
        }
    }

    private static void saveFile(String filename, List<String> filelines) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String line : filelines) {
                writer.write(line + "\n");
            }
        }
    }

    private static void run_all(HashMap<String, Set<String>> lexicos, Corpus corpus, List<ResenhaAux> resenhasAux, HashMap<String, Set<String>> aspectos2, String produto, String tipoNormalizacao) throws Exception {
        Set<String> stopSubsWords = new HashSet<>();
        Set<Map.Entry<String, Set<String>>> lexicos_es = lexicos.entrySet();
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Set<String>> lexicos_e : lexicos_es) {
            Set<String> lexico = lexicos_e.getValue();
            DoublePropagation dp = new DoublePropagation();
            dp.runDP(corpus, lexico, stopSubsWords);
            Set<Aspecto> aspectosExtraidosDp = dp.aspectosExtraidos;
            NewAspectPruning.setSuperSets(aspectosExtraidosDp);
            for (double p_support = 0.1; p_support < 1.0; p_support += 0.2) {
                for (int freq = 2; freq <= 10; freq += 2) {
                    Set<Aspecto> aspectosExtraidos = new HashSet<>(aspectosExtraidosDp);
                    NewAspectPruning.removerAspectosFreq(resenhasAux, aspectosExtraidos, freq);
                    NewAspectPruning.doPSupportBasedPruning3(aspectosExtraidos, resenhasAux, p_support);
                    NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
                    Set<Aspecto> aspectosExtraidos2 = new HashSet<>(aspectosExtraidos);
                    Set<String> aspectosStr = getAspectosStr(aspectosExtraidos2);
                    String config = ("Léxico: " + lexicos_e.getKey()
                            + "\tp-support: " + p_support + "\tFreq:" + freq);
                    String avaliacao = getNovaAvaliacao2(aspectosStr, aspectos2);
                    lines.add(config);
                    lines.add(avaliacao);
                }
            }
        }
        saveFile("all_" + produto + "_" + tipoNormalizacao, lines);
    }

    private static void run_no_support_prun(HashMap<String, Set<String>> lexicos, Corpus corpus, List<ResenhaAux> resenhasAux, HashMap<String, Set<String>> aspectos2, String produto, String tipoNormalizacao) throws Exception {
        Set<String> stopSubsWords = new HashSet<>();
        Set<Map.Entry<String, Set<String>>> lexicos_es = lexicos.entrySet();
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Set<String>> lexicos_e : lexicos_es) {
            Set<String> lexico = lexicos_e.getValue();
            DoublePropagation dp = new DoublePropagation();
            dp.runDP(corpus, lexico, stopSubsWords);
            Set<Aspecto> aspectosExtraidosDp = dp.aspectosExtraidos;
            NewAspectPruning.setSuperSets(aspectosExtraidosDp);
            for (int freq = 2; freq <= 10; freq += 2) {
                Set<Aspecto> aspectosExtraidos = new HashSet<>(aspectosExtraidosDp);
                NewAspectPruning.removerAspectosFreq(resenhasAux, aspectosExtraidos, freq);
                NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
                Set<Aspecto> aspectosExtraidos2 = new HashSet<>(aspectosExtraidos);
                Set<String> aspectosStr = getAspectosStr(aspectosExtraidos2);
                String config = ("Léxico: " + lexicos_e.getKey() + "\tFreq:" + freq);
                String avaliacao = getNovaAvaliacao2(aspectosStr, aspectos2);
                lines.add(config);
                lines.add(avaliacao);
            }
        }
        saveFile("no_support_prun_" + produto + "_" + tipoNormalizacao, lines);
    }

    private static void run_no_extra_prun(HashMap<String, Set<String>> lexicos, Corpus corpus, HashMap<String, Set<String>> aspectos2, String produto, String tipoNormalizacao) throws Exception {
        Set<String> stopSubsWords = new HashSet<>();
        Set<Map.Entry<String, Set<String>>> lexicos_es = lexicos.entrySet();
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Set<String>> lexicos_e : lexicos_es) {
            Set<String> lexico = lexicos_e.getValue();
            DoublePropagation dp = new DoublePropagation();
            dp.runDP(corpus, lexico, stopSubsWords);
            Set<Aspecto> aspectosExtraidosDp = dp.aspectosExtraidos;
            Set<Aspecto> aspectosExtraidos = new HashSet<>(aspectosExtraidosDp);
            NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
            Set<Aspecto> aspectosExtraidos2 = new HashSet<>(aspectosExtraidos);
            Set<String> aspectosStr = getAspectosStr(aspectosExtraidos2);
            String config = ("Léxico: " + lexicos_e.getKey());
            String avaliacao = getNovaAvaliacao2(aspectosStr, aspectos2);
            lines.add(config);
            lines.add(avaliacao);
        }
        saveFile("no_extra_prun_" + produto + "_" + tipoNormalizacao, lines);
    }

    private static void run_no_freq_prun(HashMap<String, Set<String>> lexicos, Corpus corpus, List<ResenhaAux> resenhasAux, HashMap<String, Set<String>> aspectos2, String produto, String tipoNormalizacao) throws Exception {
        Set<String> stopSubsWords = new HashSet<>();
        Set<Map.Entry<String, Set<String>>> lexicos_es = lexicos.entrySet();
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, Set<String>> lexicos_e : lexicos_es) {
            Set<String> lexico = lexicos_e.getValue();
            DoublePropagation dp = new DoublePropagation();
            dp.runDP(corpus, lexico, stopSubsWords);
            Set<Aspecto> aspectosExtraidosDp = dp.aspectosExtraidos;
            NewAspectPruning.setSuperSets(aspectosExtraidosDp);
            for (double p_support = 0.1; p_support < 1.0; p_support += 0.2) {
                Set<Aspecto> aspectosExtraidos = new HashSet<>(aspectosExtraidosDp);
                NewAspectPruning.doPSupportBasedPruning3(aspectosExtraidos, resenhasAux, p_support);
                NewAspectPruning.removerAspectosStopWords(aspectosExtraidos);
                Set<Aspecto> aspectosExtraidos2 = new HashSet<>(aspectosExtraidos);
                Set<String> aspectosStr = getAspectosStr(aspectosExtraidos2);
                String config = ("Léxico: " + lexicos_e.getKey()
                        + "\tp-support: " + p_support);
                String avaliacao = getNovaAvaliacao2(aspectosStr, aspectos2);
                lines.add(config);
                lines.add(avaliacao);
            }
        }
        saveFile("no_freq_prun_" + produto + "_" + tipoNormalizacao, lines);
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

    private static HashMap<String, Set<String>> getLexicos() throws IOException {
        HashMap<String, Set<String>> lexicos = new HashMap<>();
        lexicos.put("OPLex", LexicoUtils.getLexicoOpLex());
        lexicos.put("SentiLex_Flex", LexicoUtils.getLexicoSentiLex_Flex());
        lexicos.put("LIWC_PTBR", LexicoUtils.getLexicoLIWC_PTBR());
        lexicos.put("SentiLex_Lem", LexicoUtils.getLexicoSentiLex_Lem());
        return lexicos;
    }

    private static List<JSONObject> getDataSets() throws Exception {
        List<JSONObject> objects = new ArrayList<>();
//        objects.add(getMD101DataSet());
//        objects.add(getUn40d6500DataSet());
        objects.add(getXT_1032_8GbDataSet());
        return objects;
    }

    private static JSONObject getMD101DataSet() throws Exception {
        Corpus corpus = controller.getCorpusMD101BzOriginais();
        HashMap<String, Set<String>> aspectosMD101BZ = getAspectosMD101BZ();
//        Corpus corpusMD101BzUGCNormal = controller.getCorpusMD101BzUGCNormal();
//        HashMap<String, Set<String>> aspectosMD101BZUGC = getAspectosUGCMD101BZ();
//        List<ResenhaAux> resenhasUGCAux = NewAspectPruning.getResenhasNotebooksUGC();
        JSONObject obj = new JSONObject();
        obj.put("Corpus Original", corpus);
        obj.put("Aspectos", aspectosMD101BZ);
//        obj.put("Resenhas Aux", );
//        obj.put("UGCNormal", corpusMD101BzUGCNormal);
//        obj.put("AspectosUGCNormal", aspectosMD101BZUGC);
//        obj.put("Resenhas UGC Aux", resenhasUGCAux);
        obj.put("Produto", "MD101");
        return obj;
    }

    private static JSONObject getUn40d6500DataSet() throws Exception {
        Corpus corpus = controller.getCorpusun40d6500_40_polegadasOriginais();
        HashMap<String, Set<String>> aspectosUN40d6500 = getAspectos_un40d6500_40_polegadas();
//        List<ResenhaAux> resenhaAux = NewAspectPruning.getResenhasSMartvs();
//        Corpus corpusUN40d6500UGCNormal = controller.getCorpusun40d6500_40_polegadasUGCNormal();
//        HashMap<String, Set<String>> aspectosUN40d6500UGC = getAspectos_un40d6500_40_polegadasUGC();
//        List<ResenhaAux> resenhaUGCAux = NewAspectPruning.getResenhasSMartvsUGC();
        JSONObject obj = new JSONObject();
        obj.put("Corpus Original", corpus);
        obj.put("Aspectos", aspectosUN40d6500);
//        obj.put("UGCNormal", corpusUN40d6500UGCNormal);
//        obj.put("AspectosUGCNormal", aspectosUN40d6500UGC);
//        obj.put("Resenhas UGC Aux", resenhaUGCAux);
        obj.put("Produto", "UN40d6500");
        return obj;
    }

    private static JSONObject getXT_1032_8GbDataSet() throws Exception {
        Corpus corpus = controller.getCorpusMotoG_XT_1032_8GbOriginal();
        HashMap<String, Set<String>> aspectosXT_1032_8Gb = getAspectos_MotoG_XT_1032_8Gb();
//        List<ResenhaAux> resenhaAux = NewAspectPruning.getResenhasCelulares();
//        Corpus corpusXT_1032_8GbUGCNormal = controller.getCorpusMotoG_XT_1032_8GbNormal();
//        HashMap<String, Set<String>> aspectosXT_1032_8GbUGC = getAspectos_MotoG_XT_1032_8GbUGC();
//        List<ResenhaAux> resenhaUGCAux = NewAspectPruning.getResenhasCelularesUGC();
        JSONObject obj = new JSONObject();
        obj.put("Corpus Original", corpus);
        obj.put("Aspectos", aspectosXT_1032_8Gb);
//        obj.put("UGCNormal", corpusXT_1032_8GbUGCNormal);
//        obj.put("AspectosUGCNormal", aspectosXT_1032_8GbUGC);
//        obj.put("Resenhas UGC Aux", resenhaUGCAux);
        obj.put("Produto", "XT_1032_8Gb");
        return obj;
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
}
