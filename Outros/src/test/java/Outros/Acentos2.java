/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author arthur
 */
public class Acentos2 {

    private static List<String> lexico;
    private static List<String> lexicoSemAcento;
    private static Set<String> tokensSet;
    private static List<String> corpus;
    private static HashMap<String, String> tokensAcentosFaltantes;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        lexicoSemAcento = new ArrayList<>();
        getLexico("/home/arthur/Documentos/Delaf2015v04_2.dic");
        File[] filesDirs = getFilesDirs("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb");
        for (File fileDir : filesDirs) {
            tokensSet = new HashSet<>();
            tokensAcentosFaltantes = new HashMap<>();
            getCorpus(fileDir);
            getBagOfWords(corpus);
            pegarTokensComAcentosFaltantes();
            Set<Map.Entry<String, String>> entrySet = tokensAcentosFaltantes.entrySet();
            entrySet.forEach((entry) -> {
                System.out.println(entry);
            });
            substituir(fileDir);
        }
    }

    private static void getLexico(String diretorio) throws IOException {
//        System.out.println("Pegando léxico");
        lexico = Files.readAllLines(Paths.get(diretorio));
        tirarAcentosLexico(lexico);
//        System.out.println("Pegou léxico");
    }

    public static void getCorpus(File dir) throws Exception {
        String fileText = new String(Files.readAllBytes(Paths.get(dir.getPath())), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
        corpus = new ArrayList<>(Arrays.asList(reviewsStr));
        Iterator<String> iterator = corpus.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (str.trim().isEmpty()) {
                iterator.remove();
            }
        }
    }

    private static void getBagOfWords(List<String> corpus) throws Exception {
//        System.out.println("Tokenizando");
        Tokenizer stfUtils = new Tokenizer();
        for (String corpu : corpus) {
            tokensSet.addAll(stfUtils.getTokens(corpu));
        }
//        System.out.println("Tokenizou");
    }

    private static void tirarAcentosLexico(List<String> lexico) throws UnsupportedEncodingException {
        for (int i = 0; i < lexico.size(); i++) {
            lexicoSemAcento.add(StringUtils.stripAccents(lexico.get(i)));
        }
    }

    private static File[] getFilesDirs(String folderDir) {
        File folder = new File(folderDir);
        boolean directory = folder.isDirectory();
        File[] files = null;
        if (directory) {
            files = folder.listFiles((File dir, String name) -> name.endsWith(".txt"));
        }
        return files;
    }

    public static void pegarTokensComAcentosFaltantes() throws UnsupportedEncodingException {
//        System.out.println("Pegando tokens inexistentes");
//        System.out.println("-> Tirando acento dos tokens");
        tokensSet.forEach((token) -> {
            boolean contem = lexico.stream().anyMatch(x -> x.equalsIgnoreCase(token));
            if (contem) {
            } else {
                boolean contem2 = lexicoSemAcento.stream().anyMatch(x -> x.equalsIgnoreCase(token));
                if (contem2) {
                    int indice = IntStream.range(0, lexicoSemAcento.size())
                            .filter(i -> lexicoSemAcento.get(i).equalsIgnoreCase(token)).findFirst().getAsInt();
                    tokensAcentosFaltantes.put(token, lexico.get(indice));
                }
            }
        });
//        System.out.println("-> Acentos retirados");
//        System.out.println("Pegou tokens inexistentes");
    }

    private static void substituir(File dir) throws Exception {
        List<String> preProcessado = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer();
        for (String corpu : corpus) {
            List<String> tokens = tokenizer.getTokens(corpu);
            Set<String> palavrasSemAcento = tokensAcentosFaltantes.keySet();
            for (int i = 0; i < tokens.size(); i++) {
                for (String palavraSemAcento : palavrasSemAcento) {
                    String newToken = "";
                    String token = tokens.get(i);
                    if (token.equalsIgnoreCase(palavraSemAcento)) {
                        String[] tokenSplit = token.split("");
                        String[] palavraComAcentoSplit = tokensAcentosFaltantes.get(palavraSemAcento).split("");
                        if (tokenSplit.length == palavraComAcentoSplit.length) {
                            for (int j = 0; j < tokenSplit.length; j++) {
                                char t = tokenSplit[j].charAt(0);
                                String t2 = palavraComAcentoSplit[j];
                                boolean isUpper = Character.isUpperCase(t);
                                if (isUpper) {
                                    newToken = newToken.concat(t2.toUpperCase());
                                } else {
                                    newToken = newToken.concat(t2);
                                }
                            }
                        } else {
                            newToken = token;
                        }
                    } else {
                        newToken = token;
                    }
                    tokens.set(i, newToken);
                }
            }
            String texto = "";
            for (String token : tokens) {
                texto = texto.concat(token + " ");
            }
            preProcessado.add("[r] " + texto.trim() + "\n");
        }
        Files.write(Paths.get(dir.getPath().replace(".txt", "_comacentos.txt")), preProcessado);
    }

}
