/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.Relacao_Dependencia;
import com.acbv.propagacao_dupla.entidades.Resenha;
import com.acbv.propagacao_dupla.entidades.Tipo_Relacao;
import com.acbv.propagacao_dupla.preprocessamento.PreProcessamentoCorpus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author arthur
 */
public class NewMain2 {

    /**
     * @param args the command line arguments
     */
    static List<Tipo_Relacao> MR = new ArrayList<>(Arrays.asList(new Tipo_Relacao[]{Tipo_Relacao.ADVMOD, Tipo_Relacao.AMOD, Tipo_Relacao.NMOD, Tipo_Relacao.CSUBJ, Tipo_Relacao.DOBJ, Tipo_Relacao.IOBJ,
        Tipo_Relacao.NSUBJ, Tipo_Relacao.PREP, Tipo_Relacao.XSUBJ}));

    public static void main(String[] args) throws Exception {
        String[] corpus = new String[]{"Eu amei o iPhone."};
        PreProcessamentoCorpus pre = PreProcessamentoCorpus.getINSTANCIA(corpus);
        Corpus corpus1 = pre.getCorpus();
        Resenha resenhax = corpus1.resenhas.get(0);
        System.out.println(Arrays.toString(resenhax.frases[0].relacoes));
        List<Resenha> resenhas = corpus1.resenhas;
        for (Resenha resenha : resenhas) {
            Frase[] frases = resenha.frases;
            for (Frase frase : frases) {
                Relacao_Dependencia[] relacoes = frase.relacoes;
                for (Relacao_Dependencia relacoe : relacoes) {
                    System.out.println(relacoe);
                    if (Objects.nonNull(relacoe) && MR.contains(relacoe.relacao)) {
                        System.out.println("OPA");
                        //System.out.println(frase.texto);
                    }
                }
            }
        }
    }

}
