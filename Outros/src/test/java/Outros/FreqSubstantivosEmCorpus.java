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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author arthur
 */
public class FreqSubstantivosEmCorpus {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        Controller controller = Controller.getController();
        Corpus corporaNotebooks = controller.getCorporaNotebooks();
        HashMap<String, Integer> substantivosFrequency = getSubstantivosFrequency(corporaNotebooks);
        Map<String, Integer> sortByValue = sortByValue(substantivosFrequency);
        Set<Map.Entry<String, Integer>> entrySet = sortByValue.entrySet();
        entrySet.forEach((entry) -> {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        });
    }

    private static HashMap<String, Integer> getSubstantivosFrequency(Corpus corpus) {
        HashMap<String, Integer> subsFreq = new HashMap<>();
        List<Resenha> resenhas = corpus.resenhas;
        resenhas.stream().map((resenha) -> resenha.frases).forEachOrdered((frases) -> {
            for (Frase frase : frases) {
                Nodo[] nodos = frase.nodos;
                for (Nodo nodo : nodos) {
                    if (Sintatico.isSubstantivo(nodo.categoria)) {
                        String nodoStr = nodo.lemma.toLowerCase();
                        subsFreq.putIfAbsent(nodoStr, 0);
                        Integer freqAtualizada = subsFreq.get(nodoStr) + 1;
                        subsFreq.replace(nodoStr, freqAtualizada);
                    }
                }
            }
        });
        return subsFreq;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
