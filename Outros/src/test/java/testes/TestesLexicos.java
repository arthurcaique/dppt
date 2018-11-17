/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.utils.lexicoUtils.EntradaLexico;
import com.acbv.propagacao_dupla.utils.lexicoUtils.Lexico;
import com.acbv.propagacao_dupla.utils.lexicoUtils.LexicoUtils;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author arthur
 */
public class TestesLexicos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Lexico lexico = LexicoUtils.getOpLex();
        List<EntradaLexico> entradasLexico = lexico.getEntradas();
        int cont_negativo = 0;
        int cont_neutro = 0;
        int cont_positivo = 0;
        int cont_varias_polaridades = 0;
        for (EntradaLexico entradaLexico : entradasLexico) {
            List<Integer> polaridades = entradaLexico.getPolaridades();
            if (polaridades.size() == 1) {
                Integer polaridade = polaridades.get(0);
                if (polaridade < 0) {
                    cont_negativo++;
                } else if (polaridade == 0) {
                    cont_neutro++;
                } else {
                    cont_positivo++;
                }
            } else if(polaridades.size() > 1) {
                cont_varias_polaridades++;
            }
        }
        System.out.println("Negativos: " + cont_negativo);
        System.out.println("Neutros: " + cont_neutro);
        System.out.println("Positivos: " + cont_positivo);
        System.out.println("Várias polaridades: " + cont_varias_polaridades);
    }

}
