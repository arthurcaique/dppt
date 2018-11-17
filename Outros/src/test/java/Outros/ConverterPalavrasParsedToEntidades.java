/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.entidades.Resenha;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author arthur
 */
public class ConverterPalavrasParsedToEntidades {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File directory = new File("/home/arthur/Documentos/md101bz/md101bz_pre_process_ugc_normal_parsed_palavras");
        if (directory.isDirectory()) {
            File[] resenhas = directory.listFiles();
            for (File resenhaFile : resenhas) {
                String resenhaStr = new String(Files.readAllBytes(resenhaFile.toPath()), "UTF-8");
                resenhaStrToResenhaPalavrasParser(resenhaStr);
//                System.out.println(resenhaStr);
                break;
            }
        }
    }

    private static Resenha resenhaStrToResenhaPalavrasParser(String resenhaStr) {
//        Resenha resenha = new Resenha(frases, resenhaStr);
        String[] frases = resenhaStr.split("</ß>");
        for (String frase : frases) {
            frase = frase.replace("<ß>", "");
            if (!frase.trim().isEmpty()) {
                String[] tokens = frase.split("\n");
                for (String tokenFeatsStr : tokens) {
                    if (!tokenFeatsStr.trim().isEmpty()) {
                        String[] tokenFeats = tokenFeatsStr.split(" ");
                        String tokenStr = tokenFeats[0];
                        String lemma;
                        
                        if(tokenFeats[1].startsWith("#")){
                            lemma = tokenStr;
                        } else {
                            lemma = tokenFeats[1].replace("[", "").replace("]", "");
                        }
                        System.out.println("Token: "+tokenFeatsStr);
//                        System.out.println("Token: " + tokenStr);
//                        System.out.println("Lemma: " + lemma);
                    }
                }
            }
        }
        return null;
    }
}
