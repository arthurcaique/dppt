/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.acbv.propagacao_dupla.entidades.Categoria_Sintatica;
import com.acbv.propagacao_dupla.entidades.Nodo;
import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import com.acbv.propagacao_dupla.utils.Avaliacao;
import com.acbv.propagacao_dupla.utils.TreeTaggerUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author arthur
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
//        HashMap<String, String> constructs2 = getConstructs2();
//        Set<Map.Entry<String, String>> entrySet = constructs2.entrySet();
//        preProcesReLi();

        File extraidosFile = new File("/home/arthur/Documentos/reli/dp_saida_reli_pre_proces");
        List<String> aspectosExtraidos = Files.readAllLines(extraidosFile.toPath());
        Set<String> aspectosExtraidosSet = new HashSet<>(aspectosExtraidos);
        File aspectosFile = new File("/home/arthur/Documentos/reli/aspectos_reli_pre_proce.txt");
        List<String> aspectosReLi = Files.readAllLines(aspectosFile.toPath());
        Set<String> aspectosReLiSet = new HashSet<>(aspectosReLi);
        Avaliacao avaliacao = new Avaliacao(aspectosReLiSet, aspectosExtraidosSet);
        System.out.println(avaliacao);
    }

    private static void preProcesReLi() throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        TreeTaggerUtils treeTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
        List<String> reLi = getReLi();
        HashMap<String, String> constructs2 = getConstructs2();
        String resenhaAux;
        List<String> reLiPreProcess = new ArrayList<>();
        for (String resenha : reLi) {
            resenhaAux = "[r] ";
            List<String> tokens = tokenizer.getTokens(resenha);
            List<Nodo> nodos = treeTaggerUtils.getCategoriaSintatica(tokens);
            for (int i = 0; i < nodos.size(); i++) {
                Nodo nodoAtual = nodos.get(i);
                try {
                    Nodo nodoProximo = nodos.get(i + 1);
                    String biGram = nodoAtual.texto + " " + nodoProximo.texto;
                    if (constructs2.containsKey(biGram)) {
                        resenhaAux += constructs2.get(biGram) + " ";
                        i++;
                    } else {
                        resenhaAux += nodoAtual.texto + " ";
                    }
                } catch (IndexOutOfBoundsException e) {
                    resenhaAux += nodoAtual.texto + " ";
                }
            }
            resenhaAux = resenhaAux.trim();
            reLiPreProcess.add(resenhaAux);
        }
        Files.write(Paths.get("/home/arthur/Documentos/reli/resenhas_reli_pre_proce.txt"), reLiPreProcess);
    }

    private static void preProcessAspectosReLi() throws IOException, Exception {
        Tokenizer tokenizer = new Tokenizer();
        TreeTaggerUtils treeTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
        File aspectosFile = new File("/home/arthur/Documentos/reli/aspectos_resenhas_reli (cópia).txt");
        List<String> aspectosReLi = Files.readAllLines(aspectosFile.toPath());
        Set<String> aspectosReLiSet = new HashSet<>(aspectosReLi);
        HashMap<String, String> constructs2 = getConstructs2();
        String aspectoAux;
        Set<String> reLiPreProcess = new HashSet<>();
        for (String aspecto : aspectosReLiSet) {
            aspectoAux = "";
            List<String> tokens = tokenizer.getTokens(aspecto);
            List<Nodo> nodos = treeTaggerUtils.getCategoriaSintatica(tokens);
            for (int i = 0; i < nodos.size(); i++) {
                Nodo nodoAtual = nodos.get(i);
                try {
                    Nodo nodoProximo = nodos.get(i + 1);
                    String biGram = nodoAtual.texto + " " + nodoProximo.texto;
                    if (constructs2.containsKey(biGram)) {
                        aspectoAux += constructs2.get(biGram) + " ";
                        i++;
                    } else {
                        aspectoAux += nodoAtual.texto + " ";
                    }
                } catch (IndexOutOfBoundsException e) {
                    aspectoAux += nodoAtual.texto + " ";
                    break;
                }
            }
            aspectoAux = aspectoAux.trim() + "\n";
            reLiPreProcess.add(aspectoAux.toLowerCase());
        }
        Files.write(Paths.get("/home/arthur/Documentos/reli/aspectos_reli_pre_proce.txt"), reLiPreProcess);
    }

    private static List<String> getReLi() throws IOException {
        File file = new File("/home/arthur/Documentos/reli/resenhas_reli.txt");
        String corpus = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        String[] resenhasArray = corpus.split("\\[r\\]");
        List<String> resenhasList = new ArrayList<>(Arrays.asList(resenhasArray));
        resenhasList.remove(0);
        return resenhasList;
    }

    private static void getConstructs() throws Exception {
        Tokenizer tokenizer = new Tokenizer();
        TreeTaggerUtils treeTaggerUtils = TreeTaggerUtils.getTreeTaggerUtils();
        List<String> reLi = getReLi();
        Set<String> constructs = new HashSet<>();
        for (String resenha : reLi) {
            List<String> tokens = tokenizer.getTokens(resenha);
            List<Nodo> nodos = treeTaggerUtils.getCategoriaSintatica(tokens);
            for (int i = 1; i < nodos.size(); i++) {
                Nodo nodoAtual = nodos.get(i);
                Nodo nodoAnterior = nodos.get(i - 1);
                if (nodoAnterior.categoria == Categoria_Sintatica.PREP && nodoAtual.categoria == Categoria_Sintatica.DT) {
                    constructs.add(nodoAnterior.texto + " " + nodoAtual.texto);
                }
            }

        }
        constructs.forEach((construct) -> {
            System.out.println(construct);
        });//        
    }

    private static HashMap<String, String> getConstructs2() throws IOException {
        HashMap<String, String> constructs = new HashMap<>();
        FileInputStream excelFile = new FileInputStream(new File("/home/arthur/Área de Trabalho/prep + artigo do reli.xlsx"));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            String a1 = "";
            String a2 = "";
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell.getColumnIndex() == 0) {
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        a1 = currentCell.getStringCellValue();
                    }
                } else if (currentCell.getColumnIndex() == 1) {
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        a2 = currentCell.getStringCellValue();
                    }
                }
            }
            constructs.put(a1, a2);
        }
        return constructs;
    }

}
