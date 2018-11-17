/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author arthur
 */
public class TestSavingListOfStringToTxt {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String teste = "Léxico: OPLex	p-support: 0.1\n"
                + "Avaliacao{recall=0.6566523605150214, precision=0.3, f_measure=0.4118438761776581}\n"
                + "Léxico: OPLex	p-support: 0.1\n"
                + "Avaliacao{recall=0.5879828326180258, precision=0.2952586206896552, f_measure=0.3931133428981349}\n"
                + "Léxico: OPLex	p-support: 0.1\n"
                + "Avaliacao{recall=0.5708154506437768, precision=0.298876404494382, f_measure=0.39233038348082594}\n"
                + "Léxico: OPLex	p-support: 0.1\n"
                + "Avaliacao{recall=0.5665236051502146, precision=0.3013698630136986, f_measure=0.3934426229508196}\n"
                + "Léxico: OPLex	p-support: 0.1\n"
                + "Avaliacao{recall=0.5536480686695279, precision=0.29930394431554525, f_measure=0.3885542168674698}";
        String[] split = teste.split("\n");
        List<String> asList = Arrays.asList(split);
        saveFile("teste.txt", asList);
    }

 
}
