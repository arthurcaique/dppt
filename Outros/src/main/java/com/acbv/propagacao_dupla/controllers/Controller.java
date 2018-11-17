/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.controllers;

import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.preprocessamento.PreProcessamentoCorpus;
import com.acbv.propagacao_dupla.preprocessamento.StanfordUtils;
import com.acbv.propagacao_dupla.utils.Avaliacao;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import com.acbv.propagacao_dupla.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author arthur
 */
public class Controller {

    private static Controller controller;
    private static StanfordUtils stanfordUtils;
    private static TreeTaggerUtils treetTaggerUtils;

    private Controller() throws IOException {
        stanfordUtils = new StanfordUtils();
        treetTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
    }

    public static Controller getController() throws IOException {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public Corpus getCorpus() throws Exception {
        Corpus corpus;
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8");
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb/acentos/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb_3_comacentos.txt")), "UTF-8");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Área de Trabalho/experimentos 2/resenhas_tv.txt")), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
        List<String> corpusAux = Arrays.asList(reviewsStr);
        List<Resenha> resenhas = stanfordUtils.getResenhas(corpusAux);
        Iterator<Resenha> iterator = resenhas.iterator();
        while (iterator.hasNext()) {
            Resenha resenha = iterator.next();
            if (resenha.texto.trim().isEmpty()) {
                iterator.remove();
            }
        }
        corpus = new Corpus(resenhas);
        corpus.bagOfWords = getBagOfWords(resenhas);
        corpus.bagOf2Grams = getBagNGrams(corpus.resenhas, 2);
        corpus.bagOf3Grams = getBagNGrams(corpus.resenhas, 3);
        corpus.bagOf4Grams = getBagNGrams(corpus.resenhas, 4);
        corpus.bagOf5Grams = getBagNGrams(corpus.resenhas, 5);
        return corpus;
    }

    public Corpus getCorpusAuxiliar() throws Exception {
        Corpus corpus;
        File f = new File("/home/arthur/Documentos/searchfoot/tvs_smartvs/");
        List<String> corpusAux = new ArrayList<>();
        if (f.exists()) {
            String[] resenhas = f.list((File dir, String name) -> name.endsWith(".txt"));
            for (String arqResenha : resenhas) {
                String resenha = "";
                List<String> linhasResenha = Files.readAllLines(Paths.get(f.getPath() + "/" + arqResenha));
                resenha = linhasResenha.stream().map((linhaResenha) -> linhaResenha).reduce(resenha, String::concat);
                corpusAux.add(resenha);
            }
        }
        List<Resenha> resenhas = stanfordUtils.getResenhas(corpusAux);
        Iterator<Resenha> iterator = resenhas.iterator();
        while (iterator.hasNext()) {
            Resenha resenha = iterator.next();
            if (resenha.texto.trim().isEmpty()) {
                iterator.remove();
            }
        }
        corpus = new Corpus(resenhas);
        corpus.bagOfWords = getBagOfWords(resenhas);
        corpus.bagOf2Grams = getBagNGrams(corpus.resenhas, 2);
        corpus.bagOf3Grams = getBagNGrams(corpus.resenhas, 3);
        corpus.bagOf4Grams = getBagNGrams(corpus.resenhas, 4);
        corpus.bagOf5Grams = getBagNGrams(corpus.resenhas, 5);
        return corpus;
    }
 
    public Corpus getCorpusMD101BzOriginais() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/MD101BZ/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    
    public Corpus getCorpusun40d6500_40_polegadasOriginais() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/UN40D6500/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    
    public Corpus getCorpusun40d6500_40_polegadasUGCNormal() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/UGC/UN40D6500/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    
    public Corpus getCorpusMD101BzUGCNormal() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/UGC/MD101BZ/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    
    public Corpus getCorpusMotoG_XT_1032_8GbNormal() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/UGC/XT1032/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    
    public Corpus getCorpusMotoG_XT_1032_8GbOriginal() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/Dissertacao Data/ORIGINAIS/XT1032/Reviews/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }
    

    public Corpus getCorpus305E4A() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/305E4A/resenhas_ugc_normal").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }

    public Corpus getCorporaNotebooks() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/searchfoot/notebooks/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        System.out.println(resenhas.size());
        resenhas = resenhas.subList(0, 50);
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        return corpus1;
    }
    
    public Corpus getCorporaSmartvs() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/searchfoot/tvs_smartvs/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        resenhas = resenhas.subList(0, 1000);
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        return corpus1;
    }
    
    public Corpus getCorporaCelulares() throws IOException, Exception {
        File[] listFiles = new File("/home/arthur/Documentos/searchfoot/celulares/").listFiles((File pathname) -> pathname.getPath().endsWith(".txt"));
        List<File> arquivosTxt = Arrays.asList(listFiles);
        List<String> resenhas = new ArrayList<>();
        for (File file : arquivosTxt) {
            List<String> linhasArq = Files.readAllLines(Paths.get(file.getPath()));
            String resenhaAux = "";
            resenhaAux = linhasArq.stream().map((string) -> string + "\n").reduce(resenhaAux, String::concat);
            resenhas.add(resenhaAux);
        }
        //resenhas = resenhas.subList(0, 500);
        String[] resenhaArray = new String[resenhas.size()];
        resenhaArray = resenhas.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        return corpus1;
    }

    public Set<String> getAspectosMD101Bz() throws IOException {
        File extraidosFile = new File("/home/arthur/Documentos/md101bz/aspectos/aspectos");
        List<String> aspectosExtraidos = Files.readAllLines(extraidosFile.toPath());
        Set<String> aspectosExtraidosSet = new HashSet<>(aspectosExtraidos);
        return aspectosExtraidosSet;
    }

    public Corpus getCorpusReLi() throws IOException, Exception {
        File file = new File("/home/arthur/Documentos/reli/resenhas_reli_pre_proce.txt");
        String corpus = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        String[] resenhasArray = corpus.split("\\[r\\]");
        List<String> resenhasList = new ArrayList<>(Arrays.asList(resenhasArray));
        resenhasList.remove(0);
        String[] resenhaArray = new String[resenhasList.size()];
        resenhaArray = resenhasList.toArray(resenhaArray);
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(resenhaArray);
        Corpus corpus1 = pre.getCorpus();
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }

    public Corpus getCorpus2() throws Exception {
//        Corpus corpus;
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8");
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/DIssertação Henrique/reviews2")), "UTF-8");
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Documentos/resenhas_reli.txt")), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
//        String[] reviewsStr = new String[]{"Eu amo o iPhone.", "Eu adorei o IPhone.", "Amei o iPhone", "Amei o IPhone"};
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(reviewsStr);
        Corpus corpus1 = pre.getCorpus();
//        List<Resenha> resenhas = corpus1.resenhas;
//        Iterator<Resenha> iterator = resenhas.iterator();
//        while (iterator.hasNext()) {
//            Resenha resenha = iterator.next();
//            if (resenha.texto.trim().isEmpty()) {
//                iterator.remove();
//            }
//        }
//        corpus = new Corpus(resenhas);
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }

    public Corpus getCorpus3(String caminho) throws Exception {
//        Corpus corpus;
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8");
        String fileText = new String(Files.readAllBytes(Paths.get(caminho)), "UTF-8");
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Documentos/resenhas_reli.txt")), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
//        String[] reviewsStr = new String[]{"Eu amo o iPhone.", "Eu adorei o IPhone.", "Amei o iPhone", "Amei o IPhone"};
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(reviewsStr);
        Corpus corpus1 = pre.getCorpus();
//        List<Resenha> resenhas = corpus1.resenhas;
//        Iterator<Resenha> iterator = resenhas.iterator();
//        while (iterator.hasNext()) {
//            Resenha resenha = iterator.next();
//            if (resenha.texto.trim().isEmpty()) {
//                iterator.remove();
//            }
//        }
//        corpus = new Corpus(resenhas);
        corpus1.bagOfWords = getBagOfWords(corpus1.resenhas);
        corpus1.bagOf2Grams = getBagNGrams(corpus1.resenhas, 2);
        corpus1.bagOf3Grams = getBagNGrams(corpus1.resenhas, 3);
        corpus1.bagOf4Grams = getBagNGrams(corpus1.resenhas, 4);
        corpus1.bagOf5Grams = getBagNGrams(corpus1.resenhas, 5);
        return corpus1;
    }

    private HashMap<String, Integer> getBagOfWords(List<Resenha> resenhas) {
        HashMap<String, Integer> bow = new HashMap<>();
        resenhas.stream().map((resenha) -> resenha.frases).forEachOrdered((frases) -> {
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (Nodo nodo : nodos) {
                    String token = nodo.texto.toLowerCase();
                    bow.putIfAbsent(token, 0);
                    Integer freqAnterior = bow.get(token) + 1;
                    bow.replace(token, freqAnterior);
                }
            }
        });
        return bow;
    }

    private HashMap<String, Integer> getBagNGrams(List<Resenha> resenhas, int n) {
        HashMap<String, Integer> bow = new HashMap<>();
        resenhas.stream().map((resenha) -> resenha.frases).forEachOrdered((frases) -> {
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (int i = 0; i < nodos.length; i++) {
                    Nodo nodo_i = nodos[i];
                    String token = nodo_i.texto.toLowerCase();
                    if (!Utils.removerCaracteresEspeciais(token).trim().isEmpty()) {
                        for (int j = 1; j < n; j++) {
                            try {
                                String nodo_j = nodos[i + j].texto.toLowerCase();
                                if (!Utils.removerCaracteresEspeciais(nodo_j).trim().isEmpty()) {
                                    token = token + " " + nodo_j;
                                } else {
                                    break;
                                }
                            } catch (IndexOutOfBoundsException ex) {
                                break;
                            }
                        }
                        if (token.split(" ").length == n) {
                            bow.putIfAbsent(token, 0);
                            Integer freqAnterior = bow.get(token) + 1;
                            bow.replace(token, freqAnterior);
                        }
                    }
                }
            }
        });
        return bow;
    }

    public Set<String> getExpectedAspects() throws IOException, Exception {
        String expectedStr = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb/acentos/aspectos_3")), "UTF-8");
//        String expectedStr = new String(Files.readAllBytes(Paths.get("/home/arthur/Documentos/aspectos_resenhas_reli.txt")), "UTF-8");
        List<String> expectedAux = Arrays.asList(expectedStr.split("\n"));
        Set<String> expectedAux_ = new HashSet<>();
        expectedAux.forEach((string) -> {
            expectedAux_.add((string.trim().toLowerCase()));
        });
        return expectedAux_;
    }

    public String lemmatizar(String[] dale) throws Exception {
        return treetTaggerUtils.lematizar(dale)[0];
    }

    public Set<String> getMaisFrequentes(Set<String> alvosExtraidos, HashMap<String, Integer> bagOfWords, double perc) {
        TreeMap<String, Integer> freqAspectos = getFreqAspectos(alvosExtraidos, bagOfWords);
        int numSubstantivos = alvosExtraidos.size();
        int percAux = (int) (((double) numSubstantivos / 100) * perc);
        List<Map.Entry<String, Integer>> entriesSortedByValues = entriesSortedByValues(freqAspectos);
        List<Map.Entry<String, Integer>> subList = entriesSortedByValues.subList(0, percAux - 1);
        Set<String> substantivosFreq = new HashSet<>();
        subList.forEach((entry) -> {
            substantivosFreq.add(entry.getKey());
        });
        return substantivosFreq;
    }

    public <K, V extends Comparable<? super V>>
            List<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        List<Map.Entry<K, V>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (Map.Entry<K, V> e1, Map.Entry<K, V> e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }

    public TreeMap<String, Integer> getFreqAspectos(Set<String> alvosExtraidos, HashMap<String, Integer> bagOfWords) {
        TreeMap<String, Integer> freqAspectos = new TreeMap<>();
        alvosExtraidos.forEach((alvo) -> {
            int ui = bagOfWords.get(alvo) != null ? bagOfWords.get(alvo) : 0;
            freqAspectos.put(alvo, ui);
        });
        return freqAspectos;
    }

    public Set<String> getBestConfig(Set<String> aspectosExtraidos, Set<String> aspectosEsperados, HashMap<String, Integer> bagOfWords) {
        double perc;
        double precision = 0.0;
        double melhorConfig = 10;
        Set<String> aspects = new HashSet<>();
        Avaliacao avaliacaoFinal = null;
        for (int i = 1; i <= 10; i++) {
            perc = i * 10;
            Set<String> aspectosMaisFrequentes = getMaisFrequentes(aspectosExtraidos, bagOfWords, perc);
            Avaliacao avaliacao = new Avaliacao(aspectosEsperados, aspectosMaisFrequentes);
            if (avaliacao.precision > precision) {
                aspects = aspectosMaisFrequentes;
                melhorConfig = perc;
                avaliacaoFinal = avaliacao;
            }
        }
        System.out.println(aspects);
        System.out.println("A melhor configuração é: " + melhorConfig);
        System.out.println("Avaliação: " + avaliacaoFinal);
        return aspects;
    }

    public Set<String> getDiferente(Set<String> alvosExtraidos, Set<String> expectedAspects) {
        Set<String> swag = new HashSet<>();
        alvosExtraidos.stream().filter((alvoExtraido) -> (!expectedAspects.contains(alvoExtraido))).forEachOrdered((alvoExtraido) -> {
            swag.add(alvoExtraido);
        });
        return swag;
    }

    public void searchForAparicoes(Corpus corpus, String alvoEsperado) {
        System.out.println("Alvo esperado: " + alvoEsperado);
        System.out.println("________________________________");
        Integer frequencia = corpus.bagOfWords.get(alvoEsperado) != null
                ? corpus.bagOfWords.get(alvoEsperado)
                : corpus.bagOf2Grams.get(alvoEsperado) != null
                ? corpus.bagOf2Grams.get(alvoEsperado)
                : corpus.bagOf3Grams.get(alvoEsperado) != null
                ? corpus.bagOf3Grams.get(alvoEsperado)
                : corpus.bagOf4Grams.get(alvoEsperado) != null
                ? corpus.bagOf4Grams.get(alvoEsperado)
                : corpus.bagOf5Grams.get(alvoEsperado) != null
                ? corpus.bagOf5Grams.get(alvoEsperado)
                : null;
        System.out.println("Frequência: " + frequencia);
        List<Resenha> resenhas = corpus.resenhas;
        System.out.println("APARECE EM: ");
        for (Resenha resenha : resenhas) {
            if (Utils.removerAcentos(resenha.texto).contains(alvoEsperado)) {
                System.out.println(resenha.texto);
            }
        }
        System.out.println("________________________________");
    }

    public Set<String> stopSubsWords() {
        Set<String> stopSubsWords = new HashSet<>();
//        stopSubsWords.add("dias");
//        stopSubsWords.add("necessidades");
//        stopSubsWords.add("expectativas");
//        stopSubsWords.add("coisa");
        return stopSubsWords;
    }

}
