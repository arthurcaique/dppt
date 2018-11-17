/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.utils;

import java.io.IOException;

/**
 *
 * @author arthur
 */
public class Teste {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
//        HashMap<String, Boolean> lexicoLIWC_PTBR2 = LexicoUtils.getLexicoLIWC_PTBR2();
//        int positivos = 0, negativos = 0;
//        Set<Map.Entry<String, Boolean>> entrySet = lexicoLIWC_PTBR2.entrySet();
//        for (Map.Entry<String, Boolean> entry : entrySet) {
//            positivos = entry.getValue() ? positivos+1: positivos;
//            negativos = !entry.getValue() ? negativos+1: negativos;
//        }
//        System.out.println("Total: "+lexicoLIWC_PTBR2.size());
//        System.out.println("Positivos: "+positivos);
//        System.out.println("Negativos: "+negativos);
//        System.out.println(LexicoUtils.getLexicoLIWC_PTBR3().size());
//        String a = new String("CaracterÃ­sticas positivas:";
//        byte[] bytes = new String("CaracterÃ­sticas".getBytes(), "ISO-8859-1").getBytes("UTF-8");
        byte[] isoBytes = "CaracterÃ­sticas".getBytes("ISO-8859-1");
        System.out.println(new String(isoBytes, "UTF-8"));
//        System.out.println(new String(.getBytes(), "ISO-8859-1"));
    }

}
