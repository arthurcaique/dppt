/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.NGrams;

/**
 *
 * @author arthur
 */
public class NewMain {

    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, Exception {
        HashMap<String, Set<String>> aspectos4 = getAspectos4();
        Set<Map.Entry<String, Set<String>>> entrySet = aspectos4.entrySet();
        entrySet.forEach((entry) -> {
            System.out.println(entry);
        });
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

}
