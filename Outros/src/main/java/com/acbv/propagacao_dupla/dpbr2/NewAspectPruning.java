/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.dpbr2;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.FrasesAux;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.entidades.ResenhaAux;
import com.acbv.propagacao_dupla.preprocessamento.StanfordUtils;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.Sintatico;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import utils.NGrams;

/**
 *
 * @author arthur
 */
public class NewAspectPruning {

    public static void removerAspectosFreq(List<ResenhaAux> resenhasAux, Set<Aspecto> aspectos, int freq) {
        try {
            Tokenizer t = new Tokenizer();
            Iterator<Aspecto> aspectosIt = aspectos.iterator();
            while (aspectosIt.hasNext()) {
                Aspecto aspecto = aspectosIt.next();
                String aspectoStr = aspecto.getLemma();
                aspectoStr = removerAcentosString(aspectoStr);
                List<String> tokensAspecto = t.getTokens(aspectoStr);
                int n = tokensAspecto.size();
                if (n > 1) {
                    int freqAspecto = 0;
                    for (ResenhaAux resenhaAux : resenhasAux) {
                        boolean hasAspecto;
                        switch (n) {
                            case 2:
                                if (resenhaAux.getBiGrams().size() > 0) {
                                    hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getBiGrams());
                                } else {
                                    hasAspecto = false;
                                }
                                break;
                            case 3:
                                if (resenhaAux.getTriGrams().size() > 0) {
                                    hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getTriGrams());
                                } else {
                                    hasAspecto = false;
                                }
                                break;
                            case 4:
                                if (resenhaAux.getFourGrams().size() > 0) {
                                    hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFourGrams());
                                } else {
                                    hasAspecto = false;
                                }
                                break;
                            case 5:
                                if (resenhaAux.getFiveGrams().size() > 0) {
                                    hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFiveGrams());
                                } else {
                                    hasAspecto = false;
                                }
                                break;
                            default:
                                hasAspecto = false;
                                break;
                        }
                        if (hasAspecto) {
                            freqAspecto++;
                        }
                    }
                    if (freqAspecto < freq) {
                        aspectosIt.remove();
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public static Aspecto removerAspectoFreq(List<ResenhaAux> resenhasAux, Aspecto aspecto, int freq) {
        try {
            Tokenizer t = new Tokenizer();
            String aspectoStr = aspecto.getLemma();
            aspectoStr = removerAcentosString(aspectoStr);
            List<String> tokensAspecto = t.getTokens(aspectoStr);
            int n = tokensAspecto.size();
            if (n > 1) {
                int freqAspecto = 0;
                for (ResenhaAux resenhaAux : resenhasAux) {
                    boolean hasAspecto;
                    switch (n) {
                        case 2:
                            if (resenhaAux.getBiGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getBiGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 3:
                            if (resenhaAux.getTriGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getTriGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 4:
                            if (resenhaAux.getFourGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFourGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 5:
                            if (resenhaAux.getFiveGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFiveGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        default:
                            hasAspecto = false;
                            break;
                    }
                    if (hasAspecto) {
                        freqAspecto++;
                    }
                }
                if (freqAspecto < freq) {
                    aspecto = null;
                }
//                }
            }
        } catch (Exception ex) {
        }
        return aspecto;
    }

    public static int getFreqAspecto(List<ResenhaAux> resenhasAux, Aspecto aspecto) {
        int freqAspecto = 0;
        try {
            Tokenizer t = new Tokenizer();
            String aspectoStr = aspecto.getLemma();
            aspectoStr = removerAcentosString(aspectoStr);
            List<String> tokensAspecto = t.getTokens(aspectoStr);
            int n = tokensAspecto.size();
            if (n > 1) {
                for (ResenhaAux resenhaAux : resenhasAux) {
                    boolean hasAspecto;
                    switch (n) {
                        case 2:
                            if (resenhaAux.getBiGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getBiGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 3:
                            if (resenhaAux.getTriGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getTriGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 4:
                            if (resenhaAux.getFourGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFourGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        case 5:
                            if (resenhaAux.getFiveGrams().size() > 0) {
                                hasAspecto = hasAspecto(tokensAspecto, resenhaAux.getFiveGrams());
                            } else {
                                hasAspecto = false;
                            }
                            break;
                        default:
                            hasAspecto = false;
                            break;
                    }
                    if (hasAspecto) {
                        freqAspecto++;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return freqAspecto;
    }

    public static boolean hasAspecto(List<String> tokensAspecto, List<String[]> nGramsResenha) {
        if (tokensAspecto.size() <= 0 || nGramsResenha.size() <= 0) {
            return false;
        }
        boolean hasAspecto = true;
        int numTokensAspecto = tokensAspecto.size();
        for (String[] nGramResenha : nGramsResenha) {
            if (numTokensAspecto == nGramResenha.length) {
                hasAspecto = true;
                for (int i = 0; i < numTokensAspecto; i++) {
                    String tokenAspecto = tokensAspecto.get(i);
                    String tokenNgram = nGramResenha[i];
                    if (tokenAspecto.equalsIgnoreCase(tokenNgram)) {
                    } else {
                        hasAspecto = false;
                        break;
                    }
                }
            } else {
                hasAspecto = false;
            }
            if (hasAspecto == true) {
                break;
            }
        }
        return hasAspecto;
    }

    public static void removerNonSubstantivos(Set<Aspecto> aspectos) {
        try {
            Tokenizer t = new Tokenizer();
            TreeTaggerUtils treeTagger = TreeTaggerUtils.getTreeTaggerUtils();
            Iterator<Aspecto> iterator = aspectos.iterator();
            StanfordUtils stfUtils = new StanfordUtils();
            while (iterator.hasNext()) {
                Aspecto aspecto = iterator.next();
                String aspectoStr = aspecto.getAspecto();
                List<Nodo> nodos = t.getNodos(aspectoStr);
                if (nodos.size() == 1) {
                    List<Nodo> categoriasSintaticas = treeTagger.getCategoriaSintatica(t.getTokens(aspectoStr));
                    Resenha res = stfUtils.parse(aspectoStr);
                    Nodo nodo = res.frases[0].nodos[0];
                    if (!Sintatico.isSubstantivo(nodo.categoria) && !Sintatico.isSubstantivo(categoriasSintaticas.get(0).categoria)) {
//                        System.out.println("Não substantivo: " + nodo.texto);
                        iterator.remove();
//                        System.out.println(nodo.texto + " não é substantivo.");
                    } else {
//                        System.out.println(nodo.texto + " é substantivo.");
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NewAspectPruning.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void removerAspectosStopWords(Set<Aspecto> aspectos) {
        try {
            Iterator<Aspecto> aspectosIt = aspectos.iterator();
            List<String> stopWordsAux = Files.readAllLines(Paths.get("/home/arthur/Dropbox/Dissertacao/stopwords.txt"));
            stopWordsAux.add("coisa");
            stopWordsAux.add("expectativa");
            stopWordsAux.add("hora");
            stopWordsAux.add("mes");
            stopWordsAux.add("ano");
            List<String> stopWords = new ArrayList<>();
            stopWordsAux.stream().map((stopWord) -> stopWord.trim()).forEachOrdered((stopWord) -> {
                try {
                    stopWords.add(removerAcentosString(stopWord));
                } catch (UnsupportedEncodingException ex) {
                }
            });
            while (aspectosIt.hasNext()) {
                Aspecto aspecto = aspectosIt.next();
                String aspectoStr = removerAcentosString(aspecto.getAspecto());
                if (stopWords.contains(aspectoStr)
                        || stopWords.contains(aspectoStr.toLowerCase())
                        || stopWords.contains(aspectoStr.toUpperCase())) {
//                    System.out.println("Removendo... stopword: " + aspectoStr);
                    aspectosIt.remove();
                }
            }
        } catch (IOException ex) {
        }
    }

    public static void removerAspectosStopWords2(Set<String> aspectos) {
        try {
            Iterator<String> aspectosIt = aspectos.iterator();
            List<String> stopWordsAux = Files.readAllLines(Paths.get("/home/arthur/Dropbox/Dissertacao/stopwords.txt"));
            List<String> stopWords = new ArrayList<>();
            stopWordsAux.stream().map((stopWord) -> stopWord.trim()).forEachOrdered((stopWord) -> {
                try {
                    stopWords.add(removerAcentosString(stopWord));
                } catch (UnsupportedEncodingException ex) {
                }
            });
            while (aspectosIt.hasNext()) {
                String aspecto = aspectosIt.next();
                aspecto = removerAcentosString(aspecto);
                if (stopWords.contains(aspecto)
                        || stopWords.contains(aspecto.toLowerCase())
                        || stopWords.contains(aspecto.toUpperCase())) {
//                    System.out.println("Removendo... stopword: " + aspectoStr);
                    aspectosIt.remove();
                }
            }
        } catch (IOException ex) {
        }
    }

    public static void setSuperSets(Set<Aspecto> aspectos) {
        for (Aspecto aspectoI : aspectos) {
            for (Aspecto aspectoJ : aspectos) {
                if (!aspectoI.equals(aspectoJ)
                        && aspectoJ.getLemma().contains(aspectoI.getLemma())) {
                    List<String> palavrasAspectoI = aspectoI.getPalavras();
                    String primeiraPalavraAspectoI = palavrasAspectoI.get(0);
                    List<String> palavrasAspectoJ = aspectoJ.getPalavras();
                    int index = 0;
                    for (int i = 0; i < palavrasAspectoJ.size(); i++) {
                        String palavraAspectoJ = palavrasAspectoJ.get(i);
                        if (primeiraPalavraAspectoI.equalsIgnoreCase(palavraAspectoJ)) {
                            index = i;
                            break;
                        }
                    }
                    boolean isSubset = true;
                    try {
                        for (int j = 0; j < palavrasAspectoI.size(); j++) {
                            String palavraI = palavrasAspectoI.get(j);
                            String palavraJ = palavrasAspectoJ.get(j + index);
                            if (!palavraI.equalsIgnoreCase(palavraJ)) {
                                isSubset = false;
                                break;
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        isSubset = false;
                    }
                    if (isSubset) {
                        List<Aspecto> superSets = aspectoI.getSuperSets();
                        boolean add = true;
                        for (Aspecto superSet : superSets) {
                            String lemma_j = aspectoJ.getLemma();
                            String s_lemma = superSet.getLemma();
                            add = !lemma_j.equals(s_lemma);
                        }
                        if (add) {
                            aspectoI.getSuperSets().add(aspectoJ);
                        }
                    }
                }
            }
        }
    }

    public static void doPSupportBasedPruning(Set<Aspecto> aspectos, List<Frase> frases, double p_support_index) {
        Iterator<Aspecto> aspectosIt = aspectos.iterator();
        while (aspectosIt.hasNext()) {
            Aspecto aspecto = aspectosIt.next();
            int s = 0, k = 0;
            for (Frase frase : frases) {
                if (fraseContainsAspecto(frase, aspecto)) {
                    s++;
                    List<Aspecto> superSets = aspecto.getSuperSets();
                    boolean hasSuperSetOnPhrase = false;
                    for (Aspecto superSet : superSets) {
                        if (fraseContainsAspecto(frase, superSet)) {
                            hasSuperSetOnPhrase = true;
                            break;
                        }
                    }
                    if (!hasSuperSetOnPhrase) {
                        k++;
                    }
                }
            }
            double p_support = (double) k / (double) s;
            if (p_support < p_support_index) {
//                System.out.println("p-support pruning: " + aspecto);
//                System.out.println("K: " + k + "\tS:" + s);
                aspectosIt.remove();
            }
        }
    }

    public static void doPSupportBasedPruning2(Set<Aspecto> aspectos, Corpus corpus, double p_support_index) {
        Iterator<Aspecto> aspectosIt = aspectos.iterator();
        List<Resenha> resenhas = corpus.resenhas;
        while (aspectosIt.hasNext()) {
            Aspecto aspecto = aspectosIt.next();
            int s = 0, k = 0;
            for (Resenha resenha : resenhas) {
                Frase[] frases = resenha.frases;
                for (Frase frase : frases) {
                    if (fraseContainsAspecto(frase, aspecto)) {
                        s++;
                        List<Aspecto> superSets = aspecto.getSuperSets();
                        boolean hasSuperSetOnPhrase = false;
                        for (Aspecto superSet : superSets) {
                            if (fraseContainsAspecto(frase, superSet)) {
                                hasSuperSetOnPhrase = true;
                                break;
                            }
                        }
                        if (!hasSuperSetOnPhrase) {
                            k++;
                        }
                    }
                }
            }
            double p_support = (double) k / (double) s;
            if (p_support < p_support_index) {
                aspectosIt.remove();
            }
        }
    }

    public static void doPSupportBasedPruning3(Set<Aspecto> aspectos, List<ResenhaAux> resenhasAux, double p_support_index) {
        Iterator<Aspecto> aspectosIt = aspectos.iterator();
        while (aspectosIt.hasNext()) {
            Aspecto aspecto = aspectosIt.next();
            int s = 0, k = 0;
            for (ResenhaAux resenha : resenhasAux) {
                List<FrasesAux> frasesAux = resenha.getFrasesAux();
                for (FrasesAux frase : frasesAux) {
                    if (fraseContainsAspecto2(frase, aspecto)) {
                        s++;
                        List<Aspecto> superSets = aspecto.getSuperSets();
                        boolean hasSuperSetOnPhrase = false;
                        for (Aspecto superSet : superSets) {
                            if (fraseContainsAspecto2(frase, superSet)) {
                                hasSuperSetOnPhrase = true;
                                break;
                            }
                        }
                        if (!hasSuperSetOnPhrase) {
                            k++;
                        }
                    }
                }
            }
            double p_support = (double) k / (double) s;
            if (p_support < p_support_index) {
                aspectosIt.remove();
            }
        }
    }

    private static boolean fraseContainsAspecto(Frase frase, Aspecto aspecto) {
        List<String> nosFrase = new ArrayList<>();
        Nodo[] nodos = frase.nodos;
        for (Nodo nodo : nodos) {
            nosFrase.add(nodo.lemma);
        }
        List<String> palavrasAspecto = aspecto.getPalavras();
        boolean contains = phraseContainsAnother(nosFrase, palavrasAspecto);
        return contains;
    }

    private static boolean fraseContainsAspecto2(FrasesAux frase, Aspecto aspecto) {
        List<String> nosFrase = frase.getLemmas();
        List<String> palavrasAspecto = aspecto.getPalavras();
        boolean contains = phraseContainsAnother(nosFrase, palavrasAspecto);
        return contains;
    }

    private static boolean phraseContainsAnother(List<String> tokensPossibleSuperSet, List<String> tokensPossibleSubSet) {
        String possibleSubsetFirstWord = tokensPossibleSubSet.get(0);
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < tokensPossibleSuperSet.size(); i++) {
            String superSetToken = tokensPossibleSuperSet.get(i);
            if (possibleSubsetFirstWord.equalsIgnoreCase(superSetToken)) {
                indexes.add(i);
            }
        }
        boolean contains = indexes.size() > 0;
        for (Integer index : indexes) {
            contains = true;
            try {
                for (int i = 1; i < tokensPossibleSubSet.size(); i++) {
                    String tokenSubSet = tokensPossibleSubSet.get(i);
                    String tokenSuperSet = tokensPossibleSuperSet.get(i + index);
                    if (!tokenSubSet.equalsIgnoreCase(tokenSuperSet)) {
                        contains = false;
                        break;
                    }
                }
                if (contains == true) {
                    break;
                }
            } catch (IndexOutOfBoundsException e) {
                contains = false;
            }
        }
        return contains;
    }

    public static List<ResenhaAux> getResenhasNotebooks() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/notebooks/");
    }
    
    public static List<ResenhaAux> getResenhasNotebooksUGC() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/UGC_notebooks/");
    }

    public static List<ResenhaAux> getResenhasSMartvs() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/tvs_smartvs/");
    }
    
    public static List<ResenhaAux> getResenhasSMartvsUGC() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/UGC_tvs_smartvs");
    }

    public static List<ResenhaAux> getResenhasCelulares() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/celulares/");
    }
    
    public static List<ResenhaAux> getResenhasCelularesUGC() {
        return getAllResenhas("/home/arthur/Documentos/Dissertacao Data/SEARCHFOOT/UGC_tvs_smartvs");
    }

    public static List<ResenhaAux> getAllResenhas(String dir_files) {
        List<ResenhaAux> resenhasAux = new ArrayList<>();
        try {
            Tokenizer t = new Tokenizer();
            File f = new File(dir_files);
            if (f.exists()) {
                String[] resenhas = f.list((File dir, String name) -> name.endsWith(".txt"));
                for (String arqResenha : resenhas) {
                    String resenha = "";
                    List<String> linhasResenha = Files.readAllLines(Paths.get(f.getPath() + "/" + arqResenha));
                    resenha = linhasResenha.stream().map((linhaResenha) -> linhaResenha).reduce(resenha, String::concat).toLowerCase();
                    ResenhaAux rAux = new ResenhaAux(resenha);
                    List<FrasesAux> frasesAux = t.getFrasesAux(resenha);
                    List<String> alltokens = new ArrayList<>();
                    frasesAux.stream().map((frasesAux1) -> frasesAux1.getLemmas()).forEachOrdered((tokens) -> {
                        alltokens.addAll(tokens);
                    });
                    rAux.setTokens(alltokens);
                    rAux.setFrasesAux(frasesAux);
                    rAux.setBiGrams(NGrams.getNGrams(alltokens, 2));
                    rAux.setTriGrams(NGrams.getNGrams(alltokens, 3));
                    rAux.setFourGrams(NGrams.getNGrams(alltokens, 4));
                    rAux.setFiveGrams(NGrams.getNGrams(alltokens, 5));
                    resenhasAux.add(rAux);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NewAspectPruning.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resenhasAux;
    }

    private static String removerAcentosString(String string) throws UnsupportedEncodingException {
        return StringUtils.stripAccents(string);
    }

}
