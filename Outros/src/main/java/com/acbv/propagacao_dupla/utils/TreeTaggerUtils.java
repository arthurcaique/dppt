/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.utils;

import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Nodo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;

/**
 *
 * @author arthur
 */
public class TreeTaggerUtils {

    private final TreeTaggerWrapper treeTager;
    private static TreeTaggerUtils ttUtils;
//    public Set<String> tags = new HashSet<>();
    
    private TreeTaggerUtils() throws IOException {
        System.setProperty("treetagger.home", "/home/arthur/Documentos/");
        this.treeTager = new TreeTaggerWrapper();
        this.treeTager.setModel("/home/arthur/Documentos/portuguese2-utf8.par:utf8");
    }

    public static TreeTaggerUtils getTreeTaggerUtils() throws IOException {
        if (ttUtils == null) {
            ttUtils = new TreeTaggerUtils();
        }
        return ttUtils;
    }

//    public List<Nodo> getCategoriaSintatica(String[] tokens) {
//        List<Nodo> nodos = new ArrayList<>();
//        this.treeTager.setHandler((Object token, String pos, String lemma) -> {
//            Nodo nodo = new Nodo((String) token, treeTaggerTagToCategoria_Sintatica(pos));
//            nodo.lemma = lemma;
//            nodos.add(nodo);
//        });
//        return nodos;
//    }
    public List<Nodo> getCategoriaSintatica(List<String> tokens) {
        List<Nodo> nodos = new ArrayList<>();
        try {
            this.treeTager.setHandler((Object token, String pos, String lemma) -> {
                Nodo nodo = new Nodo((String) token, treeTaggerTagToCategoria_Sintatica(pos));
                nodo.lemma = lemma;
                nodos.add(nodo);
            });
            String[] tokensAux = new String[tokens.size()];
            tokensAux = tokens.toArray(tokensAux);
            this.treeTager.process(tokensAux);
        } catch (IOException | TreeTaggerException ex) {
            Logger.getLogger(TreeTaggerUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nodos;
    }

    public String[] lematizar(String[] tokens) throws Exception {
        List<String> lemasList = new ArrayList<>();
        this.treeTager.setHandler((token, pos, lemma)
                -> lemasList.add(lemma));
        this.treeTager.process(tokens);
        String[] lemasVector = new String[lemasList.size()];
        lemasVector = lemasList.toArray(lemasVector);
        return lemasVector;
    }
    
    public String[] lematizar(List<String> tokens) throws Exception {
        List<String> lemasList = new ArrayList<>();
        this.treeTager.setHandler((Object token, String pos, String lemma) -> {
            String lemm = lemma.equalsIgnoreCase("@card@") ? (String) token : lemma;
            lemasList.add(lemm);
        });
        this.treeTager.process(tokens);
        String[] lemasVector = new String[lemasList.size()];
        lemasVector = lemasList.toArray(lemasVector);
        return lemasVector;
    }

    public Categoria_Sintatica treeTaggerTagToCategoria_Sintatica(String pos_tag) {
        Categoria_Sintatica categoria;
        if (pos_tag.startsWith("DET")) {
            categoria = Categoria_Sintatica.DT;
        } else if (pos_tag.startsWith("ADJ")) {
            categoria = Categoria_Sintatica.JJ;
        }
        else if (pos_tag.startsWith("NOUN") && pos_tag.endsWith("Sing")) {
            categoria = Categoria_Sintatica.NN;
        } else if (pos_tag.startsWith("NOUN") || pos_tag.equalsIgnoreCase("Plur")) {
            categoria = Categoria_Sintatica.NNS;
        } 
        else if (pos_tag.startsWith("ADP")) {
            categoria = Categoria_Sintatica.PREP;
        } else if (pos_tag.startsWith("ADV")) {
            categoria = Categoria_Sintatica.RB;
        } else if (pos_tag.startsWith("VERB")) {
            categoria = Categoria_Sintatica.VB;
        } else {
            categoria = Categoria_Sintatica.OUTRAS;
        }
        return categoria;
    }
}
