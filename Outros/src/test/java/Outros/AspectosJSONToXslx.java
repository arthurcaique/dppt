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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author arthur
 */
public class AspectosJSONToXslx {

    private final static String ASPECTOS_TAG = "aspectos";
    private final static String RESENHA_TAG = "resenha";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException, IOException {
        HashMap<String, String> aspectos = getAspectos();
        Set<Map.Entry<String, String>> entrySet = aspectos.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            System.out.println(entry);
        }
    }

    private static HashMap<String, String> getAspectos() throws ParseException, FileNotFoundException, IOException {
        HashMap<String, String> aspectosResenha = new HashMap<>();
        File dir2 = new File("/home/arthur/Documentos/305E4A/resenhas_ugc_normal/");
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
                    aspectosResenha.put(expressao.trim().toLowerCase(), aspecto.trim().toLowerCase());
                }
            }
        }
        return aspectosResenha;
    }
    
    private static HashMap<String, String> getAspectos2() throws ParseException, FileNotFoundException, IOException, Exception {
        HashMap<String, String> aspectosResenha = new HashMap<>();
        File dir2 = new File("/home/arthur/Documentos/searchfoot/md101bz/");
        String[] listFiles = dir2.list((File dir, String name) -> name.endsWith(".json"));
        Tokenizer tokenizer = new Tokenizer();
        for (String arqResenha : listFiles) {
            arqResenha = dir2.getPath() + "/" + arqResenha;
            if (Files.exists(Paths.get(arqResenha))) {
                org.json.simple.parser.JSONParser parser = new JSONParser();
                JSONObject resenhaJSon = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(arqResenha)));
                String resenhaDesc = (String) resenhaJSon.get(RESENHA_TAG);
                List<String> tokens = tokenizer.getTokens(resenhaDesc);
                String[] lemmasResenha = TreeTaggerUtils.getTreeTaggerUtils().lematizar(tokens);
                JSONArray aspectosJson = (JSONArray) resenhaJSon.get(ASPECTOS_TAG);
                Iterator iterator = aspectosJson.iterator();
                while (iterator.hasNext()) {
                    JSONObject aspectoJson = (JSONObject) iterator.next();
                    String expressao = (String) aspectoJson.get("expressao");
                    String entidade = (String) aspectoJson.get("entidade");
                    String aspecto = (String) aspectoJson.get("aspecto");
                    String sentimento = (String) aspectoJson.get("sentimento");
                    aspectosResenha.put(expressao.toLowerCase(), aspecto.toLowerCase());
                }
            }
        }
        return aspectosResenha;
    }

}
