/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arthur
 */
public class ReLi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            List<String> resenhas_pre_processadas = new ArrayList<>();
            Set<String> aspectos = new HashSet<>();
            String reliCorpus = new String(Files.readAllBytes(Paths.get("/home/arthur/Área de Trabalho/ReLi-Amado.txt")), "UTF-8");
            List<String> resenhas = new ArrayList<>(Arrays.asList(reliCorpus.split("#Corpo_")));
            resenhas.remove(0);
            resenhas.forEach((resenha_aux) -> {
                String resenha = "";
                List<String> linhas_resenha = new ArrayList<>(Arrays.asList(resenha_aux.split("\n")));
                linhas_resenha.remove(0);
                List<String> objetos = new ArrayList<>();
                for (int i = 0; i < linhas_resenha.size(); i++) {
                    String linha = linhas_resenha.get(i);
                    if (!linha.startsWith("#Livro_")
                            && !linha.startsWith("#Corpo_")
                            && !linha.startsWith("#Resenha_")
                            && !linha.startsWith("#Nota_")
                            && !linha.startsWith("#Título_")) {
                        resenha += linha.split("\t")[0] + " ";
                        try {
                            String obj = linha.split("\t")[2];
                            if (obj.startsWith("OBJ")) {
                                objetos.add("<Aspecto>" + linha.split("\t")[0] + "<\\Aspecto><Objeto>" + obj + "<\\Objeto><Índice>" + i + "<\\Índice>");
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                }
                String obj_atual = "", obj_anterior;
                String aspecto_anterior, aspecto_atual = "";
                int indice_atual = 0, indice_anterior;
                for (int i = 0; i < objetos.size(); i++) {
                    String objeto = objetos.get(i);
                    if (i == 0) {
                        indice_anterior = -999;
                        aspecto_anterior = "";
                        obj_anterior = "";
                    } else {
                        obj_anterior = obj_atual;
                        aspecto_anterior = aspecto_atual;
                        indice_anterior = indice_atual;
                    }
                    obj_atual = objeto.split("<Índice>")[0].split("<Objeto>")[1].replace("<\\Objeto>", "");
                    aspecto_atual = objeto.split("<Objeto>")[0].replace("<Aspecto>", "").replace("<\\Aspecto>", "");
                    indice_atual = Integer.valueOf(objeto.split("<Índice>")[1].replace("<\\Índice>", ""));
                    int j = i + 1;
                    if (j == 1) {
                    } else {
                        if (obj_atual.equalsIgnoreCase(obj_anterior) && indice_atual - indice_anterior == 1) {
                            aspecto_atual = aspecto_anterior + " " + aspecto_atual;
                            if (j == objetos.size()) {
                                aspectos.add(aspecto_atual);
                            }
                        } else {
                            aspectos.add(aspecto_anterior);
                        }
                    }
                }
//                System.out.println("Resenha: " + resenha);
//                System.out.println("Aspectos: " + aspectos);
                resenhas_pre_processadas.add("[r]" + resenha + "\n");
            });
            Files.write(Paths.get("/home/arthur/Documentos/resenhas_reli.txt"), resenhas_pre_processadas);
            Files.write(Paths.get("/home/arthur/Documentos/aspectos_resenhas_reli.txt"), aspectos);
        } catch (IOException ex) {
            Logger.getLogger(ReLi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
