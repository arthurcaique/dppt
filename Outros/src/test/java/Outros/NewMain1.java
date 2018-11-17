/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.utils.Sintatico;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import utils.HashMapVectors;

/**
 *
 * @author arthur
 */
public class NewMain1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        Controller controller = Controller.getController();
        Corpus corpus305E4A = controller.getCorpus305E4A();
        List<String[]> biGrams = new ArrayList<>();
        String[] biGram;
        List<Resenha> resenhas = corpus305E4A.resenhas;
        for (Resenha resenha : resenhas) {
            Frase[] frases = resenha.frases;
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (int i = 1; i < nodos.length; i++) {
                    Nodo nodoAnterior = nodos[i - 1];
                    Nodo nodoAtual = nodos[i];
                    if (Sintatico.isSubstantivo(nodoAnterior.categoria) && Sintatico.isAdjetivo(nodoAtual.categoria)) {
                        biGram = new String[2];
                        biGram[0] = nodoAnterior.texto.toLowerCase();
                        biGram[1] = nodoAtual.texto.toLowerCase();
                        biGrams.add(biGram);
                    } else if (Sintatico.isAdjetivo(nodoAnterior.categoria) && Sintatico.isSubstantivo(nodoAtual.categoria)) {
                        biGram = new String[2];
                        biGram[0] = nodoAnterior.texto.toLowerCase();
                        biGram[1] = nodoAtual.texto.toLowerCase();
                        biGrams.add(biGram);
                    }
                }
            }
        }
        HashMapVectors biGramsFreq = new HashMapVectors();
        for (String[] biGram1 : biGrams) {
            biGramsFreq.putIfAbsent(biGram1, 0);
            Integer freqAtualizada = (Integer) biGramsFreq.get(biGram1);
            freqAtualizada++;
            biGramsFreq.replace(biGram1, freqAtualizada);
        }
        Set<Map.Entry<Object[], Object>> entrySet = biGramsFreq.entrySet();
        for (Map.Entry<Object[], Object> entry : entrySet) {
            System.out.println(Arrays.toString(entry.getKey()) + ": "+entry.getValue().toString());
        }
        
    }

}
