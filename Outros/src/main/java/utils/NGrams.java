/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arthur
 */
public class NGrams {

    public static List<String[]> getNGrams(String[] tokens, int n) {
        List<String[]> nGrams = new ArrayList<>();
        int i = 0;
        while (i + n <= tokens.length) {
            String[] nGram = new String[n];
            int k = 0;
            for (int j = i; j < (i + n); j++) {
                nGram[k] = tokens[j];
                k++;
            }
            nGrams.add(nGram);
            i++;
        }
        return nGrams;
    }
    
    public static List<String[]> getNGrams(List<String> tokens, int n) {
        List<String[]> nGrams = new ArrayList<>();
        int i = 0;
        while (i + n <= tokens.size()) {
            String[] nGram = new String[n];
            int k = 0;
            for (int j = i; j < (i + n); j++) {
                nGram[k] = tokens.get(j);
                k++;
            }
            nGrams.add(nGram);
            i++;
        }
        return nGrams;
    }
    
}
