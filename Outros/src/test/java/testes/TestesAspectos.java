/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author arthur
 */
public class TestesAspectos {

    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
//        Set<String> lexico = LexicoUtils.getLexicoSentiLex_Flex();
//        Controller controller = Controller.getController();
//        Corpus corpus = controller.getCorpus5();
//        Set<String> stopSubsWords = controller.stopSubsWords();
//        DoublePropagation dp = new DoublePropagation();
//        dp.runDP(corpus, lexico, stopSubsWords);
//        Set<Aspecto> aspectosExtraidos = dp.aspectosExtraidos;
//        Set<String> aspectosExtraidosStr = new HashSet<>();
//        aspectosExtraidos.forEach((aspectoExtraido) -> {
//            aspectosExtraidosStr.add(aspectoExtraido.getAspecto().toLowerCase());
//        });
//        Set<String> aspectos3 = getAspectos3(aspectosExtraidosStr);
        Set<String> aspectos2 = getAspectos();
        for (String string : aspectos2) {
            System.out.println(string);
        }
        
//        Avaliacao avaliacao = new Avaliacao(aspectos3, aspectos2);
//        System.out.println(avaliacao);
    }

    private static Set<String> getAspectos2() throws Exception {
        Set<String> aspectos2 = new HashSet<>();
        TreeTaggerUtils ttutils = TreeTaggerUtils.getTreeTaggerUtils();
        Tokenizer t = new Tokenizer();
        Set<String> aspectos = getAspectos();
        for (String aspecto : aspectos) {
            List<String> tokensAspecto = t.getTokens(aspecto);
            String[] lemmasAspecto = ttutils.lematizar(tokensAspecto);
            String lemma = "";
            for (String lemmaAspecto : lemmasAspecto) {
                if (lemmaAspecto.contains("_")) {
                } else {
                    lemma += lemmaAspecto + " ";
                }
            }
            lemma = lemma.trim();
            aspectos2.add(lemma);
        }
        return aspectos2;
    }

    private static Set<String> getAspectos3(Set<String> aspectos) throws IOException, Exception{
        Set<String> aspectos2 = new HashSet<>();
        TreeTaggerUtils ttutils = TreeTaggerUtils.getTreeTaggerUtils();
        Tokenizer t = new Tokenizer();
        for (String aspecto : aspectos) {
            List<String> tokensAspecto = t.getTokens(aspecto);
            String[] lemmasAspecto = ttutils.lematizar(tokensAspecto);
            String lemma = "";
            for (String lemmaAspecto : lemmasAspecto) {
                if (lemmaAspecto.contains("_")) {
                } else {
                    lemma += lemmaAspecto + " ";
                }
            }
            lemma = lemma.trim();
            aspectos2.add(lemma);
        }
        return aspectos2;
    }
    
    private static Set<String> getAspectos() throws ParseException, FileNotFoundException, IOException {
        Set<String> aspectosResenha = new HashSet<>();
        File dir2 = new File("/home/arthur/Documentos/searchfoot/md101bz/");
        String[] listFiles = dir2.list((File dir, String name) -> name.endsWith(".json"));
        for (String arqResenha : listFiles) {
            arqResenha = dir2.getPath() + "/" + arqResenha;
            if (Files.exists(Paths.get(arqResenha))) {
                org.json.simple.parser.JSONParser parser = new JSONParser();
                JSONObject resenhaJSon = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(arqResenha)));
                String resenhaDesc = (String) resenhaJSon.get(RESENHA_TAG);
                JSONArray aspectosJson = (JSONArray) resenhaJSon.get(ASPECTOS_TAG);
                Iterator iterator = aspectosJson.iterator();
                while (iterator.hasNext()) {
                    JSONObject aspectoJson = (JSONObject) iterator.next();
                    String expressao = (String) aspectoJson.get("expressao");
                    String entidade = (String) aspectoJson.get("entidade");
                    String aspecto = (String) aspectoJson.get("aspecto");
                    String sentimento = (String) aspectoJson.get("sentimento");
                    aspectosResenha.add(aspecto.toLowerCase());
                }
            }
        }
        return aspectosResenha;
    }

}
