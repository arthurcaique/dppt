/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse_ing;

import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.entidades.Relacao_Dependencia;
import com.acbv.propagacao_dupla.entidades.Resenha;
import java.util.List;
import java.util.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author arthur
 */
public class Metodo {

    public static JSONObject corpusToJSON(Corpus corpus) {
        List<Resenha> resenhas = corpus.resenhas;
        JSONObject corpusJSON = new JSONObject();
        JSONArray resenhasJSONArray = new JSONArray();
        for (Resenha resenha : resenhas) {
            JSONObject resenhaJSON = new JSONObject();
            resenhaJSON.put("texto", resenha.texto);
            resenhaJSON.put("texto_pre_processado", resenha.texto_pre_processado);
            Frase[] frases = resenha.frases;
            JSONArray frasesJSONArray = new JSONArray();
            for (Frase frase : frases) {
                JSONObject fraseJSON = new JSONObject();
                fraseJSON.put("frase", frase.texto);
                fraseJSON.put("frase_pre_processada", frase.texto_pre_processado);
                Relacao_Dependencia[] relacoes_dependencia = frase.relacoes;
                JSONArray relacoes_dependenciaJSONArray = new JSONArray();
                for (Relacao_Dependencia relacao_dependencia : relacoes_dependencia) {
                    if (Objects.nonNull(relacao_dependencia)) {
                        JSONObject relacaoJSON = new JSONObject();
                        relacaoJSON.put("dependente", nodoToJSON(relacao_dependencia.dependente));
                        relacaoJSON.put("governante", nodoToJSON(relacao_dependencia.governante));
                        relacaoJSON.put("relacao", relacao_dependencia.relacao.toString());
                        relacoes_dependenciaJSONArray.add(relacaoJSON);
                    }
                }
                fraseJSON.put("relacoes_dependencia", relacoes_dependenciaJSONArray);
                Nodo[] nodos = frase.nodos;
                JSONArray nodosJSONArray = new JSONArray();
                for (Nodo nodo : nodos) {
                    JSONObject nodoJSON = nodoToJSON(nodo);
                    nodosJSONArray.add(nodoJSON);
                }
                fraseJSON.put("nodos", nodosJSONArray);
                frasesJSONArray.add(fraseJSON);
            }
            resenhaJSON.put("frases", frasesJSONArray);
            resenhasJSONArray.add(resenhaJSON);
        }
        corpusJSON.put("corpus", resenhasJSONArray);
        return corpusJSON;
    }

    private static JSONObject nodoToJSON(Nodo nodo) {
        JSONObject nodoJSON = new JSONObject();
        nodoJSON.put("indice", nodo.indice);
        nodoJSON.put("nodo", nodo.texto);
        nodoJSON.put("lemma", nodo.lemma);
        nodoJSON.put("categoria_sintatica", nodo.categoria.toString());
        return nodoJSON;
    }

}
