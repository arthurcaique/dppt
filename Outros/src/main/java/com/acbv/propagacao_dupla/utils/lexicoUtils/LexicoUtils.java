/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.utils.lexicoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author arthur
 */
public class LexicoUtils {

    public static Set<String> getLexicoSentiLex_Lem() throws IOException {
        Set<String> lexico = new HashSet<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/SentiLex-PT02/SentiLex-lem-PT02.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String lex = string.split("\\.")[0];
            if (lex.split(" ").length == 1) {
                lexico.add(lex);
            }
        }
        return lexico;
    }

    public static Set<String> getLexicoSentiLex_Flex() throws IOException {
        Set<String> lexico = new HashSet<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/SentiLex-PT02/SentiLex-flex-PT02.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String flex = string.split(",")[0];
            if (flex.split(" ").length == 1) {
                lexico.add(flex);
            }
        }
        return lexico;
    }

    public static Set<String> getLexicoOpLex() throws IOException {
        Set<String> lexico = new HashSet<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/oplexicon_v3.0/lexico_v3.0.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String lex = string.split(",")[0];
            String type = string.split(",")[1];
            if (lex.split(" ").length == 1 && !type.equalsIgnoreCase("emot") && !type.equalsIgnoreCase("htag")) {
                lexico.add(lex);
            }

        }
        return lexico;
    }

    public static HashMap<String, Boolean> getLexicoLIWC_PTBR2() throws IOException {
        HashMap<String, Boolean> lexico = new HashMap<>();
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/LIWC2007_Portugues_win.dic")), "ISO-8859-1");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/brazilian portuguese resources for sentiment analysis/LIWC2007_Portugues_win.dic.txt")), "ISO-8859-1");
        fileText = fileText.split("%")[2].replaceAll("\r", "");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String[] feats = string.split("\t");
            for (int i = 1; i < feats.length; i++) {
                try {
                    Integer valueOf = Integer.valueOf(feats[i]);
                    if (valueOf == 126 || valueOf == 127) {
                        lexico.put(feats[0], valueOf == 126);
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(feats[i]);
                }
            }
        }
        return lexico;
    }

    public static Set<String> getLexicoLIWC_PTBR() throws IOException {
        Set<String> lexico = new HashSet<>();
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/LIWC2007_Portugues_win.dic")), "ISO-8859-1");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/brazilian portuguese resources for sentiment analysis/LIWC2007_Portugues_win.dic.txt")), "ISO-8859-1");
        fileText = fileText.split("%")[2].replaceAll("\r", "");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String[] feats = string.split("\t");
            for (int i = 1; i < feats.length; i++) {
                try {
                    Integer valueOf = Integer.valueOf(feats[i]);
                    if (valueOf == 126 || valueOf == 127) {
                        lexico.add(feats[0]);
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(feats[i]);
                }
            }
        }
        return lexico;
    }

    public static Set<String> getLexicoLIWC_PTBR3() throws IOException {
        Set<String> lexico = new HashSet<>();
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/LIWC2007_Portugues_win.dic")), "ISO-8859-1");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/brazilian portuguese resources for sentiment analysis/LIWC2007_Portugues_win.dic.txt")), "ISO-8859-1");
        fileText = fileText.split("%")[2].replaceAll("\r", "");
        String[] lexicoStr = fileText.split("\n");
        for (String string : lexicoStr) {
            String[] feats = string.split("\t");
            if (lexico.contains(feats[0])) {
                System.out.println(feats[0]);
            }
            lexico.add(feats[0]);
        }
        return lexico;
    }

    public static Lexico getSentiLex02_Lema() throws IOException {
        List<EntradaLexico> entradasLexico = new ArrayList<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/SentiLex-PT02/SentiLex-lem-PT02.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        for (String entradaLexicoStr : lexicoStr) {
            String[] feats = entradaLexicoStr.split(";");
            String lem = feats[0].split("\\.")[0];
            String pos = feats[0].split("\\.")[1];
            List<Integer> polaridades = new ArrayList<>();
            for (String feat : feats) {
                if (feat.startsWith("POL:")) {
                    Integer polaridade = Integer.valueOf(feat.split("=")[1]);
                    polaridades.add(polaridade);
                }
            }
            EntradaLexico entradaLexico = new EntradaLexico(lem, polaridades);
            entradaLexico.setPos(pos);
            entradasLexico.add(entradaLexico);
        }
        Lexico lexico = new Lexico(entradasLexico);
        return lexico;
    }

    public static Lexico getSentiLex02_Flex() throws IOException {
        List<EntradaLexico> entradasLexico = new ArrayList<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/SentiLex-PT02/SentiLex-flex-PT02.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        for (String entradaLexicoStr : lexicoStr) {
            String[] feats = entradaLexicoStr.split(";");
            String lem = feats[0].split("\\.")[0];
            String pos = feats[0].split("\\.")[1];
            List<Integer> polaridades = new ArrayList<>();
            for (String feat : feats) {
                if (feat.startsWith("POL:")) {
                    Integer polaridade = Integer.valueOf(feat.split("=")[1]);
                    polaridades.add(polaridade);
                }
            }
            EntradaLexico entradaLexico = new EntradaLexico(lem, polaridades);
            entradaLexico.setPos(pos);
            entradasLexico.add(entradaLexico);
        }
        Lexico lexico = new Lexico(entradasLexico);
        return lexico;
    }

    public static Lexico getOpLex() throws IOException {
        List<EntradaLexico> entradasLexico = new ArrayList<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Lexicos/PT/oplexicon_v3.0/lexico_v3.0.txt")), "UTF-8");
        String[] lexicoStr = fileText.split("\n");
        System.out.println(lexicoStr.length);
        for (String entradaLexicoStr : lexicoStr) {
            String[] feats = entradaLexicoStr.split(",");
            String lex = feats[0];
            String type = feats[1];
            if (!type.equalsIgnoreCase("emot") && !type.equalsIgnoreCase("htag")) {
                List<Integer> polaridades = new ArrayList<>();
                Integer polaridade = Integer.valueOf(feats[2]);
                polaridades.add(polaridade);
                EntradaLexico entradaLexico = new EntradaLexico(lex, polaridades);
                entradaLexico.setPos(type);
                entradasLexico.add(entradaLexico);
            }
        }
        Lexico lexico = new Lexico(entradasLexico);
        return lexico;
    }

    public static Lexico getLIWC_PTBR() throws IOException {
        List<EntradaLexico> entradasLexico = new ArrayList<>();
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/brazilian portuguese resources for sentiment analysis/LIWC2007_Portugues_win.dic.txt")), "ISO-8859-1");
        fileText = fileText.split("%")[2].replaceAll("\r", "");
        String[] lexicoStr = fileText.split("\n");
        for (String entradaLexicoStr : lexicoStr) {
            String[] feats = entradaLexicoStr.split("\t");
            String flex = feats[0];
            List<Integer> polaridades = new ArrayList<>();
            for (int i = 1; i < feats.length; i++) {
                try {
                    Integer emo = Integer.valueOf(feats[i]);
                    if (emo == 126) {
                        polaridades.add(1);
                    } else if (emo == 127) {
                        polaridades.add(-1);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(feats[i]);
                }
            }
            EntradaLexico entradaLexico = new EntradaLexico(flex, polaridades);
            entradasLexico.add(entradaLexico);
        }
        Lexico lexico = new Lexico(entradasLexico);
        return lexico;
    }
}
