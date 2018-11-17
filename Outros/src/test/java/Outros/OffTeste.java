/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author arthur
 */
public class OffTeste {

    private static final List<String> artigos = new ArrayList<>(Arrays.asList(new String[]{"s", "a", "as", "o", "os", "io", "ios", "ado", "ados", "vo", "vos", "va", "vas"}));

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Set<String> delasPBDict = getDelasPBDict();
        Set<String> lexicoLIWC_PTBR = getLexicoLIWC_PTBR();
        Iterator<String> iteratorLexicoLIWC_PTBR = lexicoLIWC_PTBR.iterator();
        while (iteratorLexicoLIWC_PTBR.hasNext()) {
            String entryLexico = iteratorLexicoLIWC_PTBR.next();
            if (entryLexico.endsWith("*")) {
                System.out.println(entryLexico);
                for (String artigo : artigos) {
                    String entryLexicoAux = entryLexico.replace("*", "").concat(artigo);
//                    for (List<String> list : delasPBDict) {
                    if (delasPBDict.contains(entryLexicoAux)) {
                        lexicoLIWC_PTBR.add(entryLexicoAux);
                        iteratorLexicoLIWC_PTBR.remove();
                        System.out.println("ADD: " + entryLexicoAux + "\tREMOVIDO: " + entryLexico);
                        break;
                    }
//                    }
                }
            }
        }
    }

    private static Set<String> getLexicoLIWC_PTBR() throws IOException {
        Set<String> lexico = new HashSet<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/LIWC2007_Portugues_win.dic")), "iso8859-15");
        fileText = com.acbv.propagacao_dupla.utils.Utils.removerAcentos(fileText);
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            if (!string.startsWith("%")) {
                String[] tokens = string.replace("\r", "").split("\t");
                try {
                    Integer valueOf = Integer.valueOf(tokens[0]);
                } catch (NumberFormatException e) {
                    for (int i = 1; i < tokens.length; i++) {
                        try {
                            Integer v = Integer.valueOf(tokens[i]);
                            if (v == 126 || v == 127) {
                                lexico.add(tokens[0]);
//                                if (lex.endsWith("*")) {
//                                    System.out.println(lex);
//                                }
                                break;
                            }
                        } catch (NumberFormatException ex) {
                            System.out.println(string);
                        }
                    }
                }
            }
//            String lex = string.split("\\.")[0];
//            if (lex.split(" ").length == 1) {
//                lexico.add(com.acbv.propagacao_dupla.utils.Utils.removerAcentos(lex).toLowerCase());
//            }
        }
        return lexico;
    }

    private static Set<String> getDelasPBDict() throws IOException {
        Set<String> delasList = new HashSet<>();
        List<String> lines = Files.readAllLines(Paths.get("/home/arthur/Documentos/Delaf2015v04.dic"));
//        fileText = com.acbv.propagacao_dupla.utils.Utils.removerAcentos(fileText);
//        String[] dictionary = fileText.split("\n");
        for (String line : lines) {
            line = com.acbv.propagacao_dupla.utils.Utils.removerAcentos(line);
//            List<String> tuplaList = new ArrayList<>();
//            tuplaList.addAll(Arrays.asList(palavrasVector));
            delasList.addAll(Arrays.asList(line.split("\\.")[0].split(",")));
        }
        return delasList;
    }

}
