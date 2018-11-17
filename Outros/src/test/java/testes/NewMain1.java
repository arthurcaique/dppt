/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import com.acbv.propagacao_dupla.entidades.Aspecto;
import com.acbv.propagacao_dupla.entidades.Frase;
import com.acbv.propagacao_dupla.entidades.ResenhaAux;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author arthur
 */
public class NewMain1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String frase = "O fone de ouvido é muito bom.";
        Aspecto aspecto = new Aspecto("fone");
        Aspecto aspecto2 = new Aspecto("fone de ouvido");
        List<Aspecto> supersets = new ArrayList<>();
        supersets.add(aspecto2);
        aspecto.setSuperSets(supersets);
        Frase frase;
        ResenhaAux re;
    }
    
}
