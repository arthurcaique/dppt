/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author arthur
 */
public class PegarTokensDesconhecidos {

    private static List<String> lexico;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {
        String[] corpus = getCorpus2();
//        String[] corpus = getCorpus();
//        Set<String> tokens = PreProcessamentoUtils.getTokensNaoSendoNumerosOuPontuacoes(corpus);
        getLexico("/home/arthur/Documentos/Delaf2015v04_2.dic");
//        Set<String> tokensInexistentesNoLexico = getTokensInexistentesNoLexico(lexico, tokens);
//        tokensInexistentesNoLexico.forEach((string) -> {
//            System.out.println(string);
//        });
    }

    private static void getLexico(String diretorio) throws IOException {
        System.out.println("Pegando léxico");
        lexico = Files.readAllLines(Paths.get(diretorio));
        System.out.println("Pegou léxico");
    }

    private static String[] getCorpus() throws Exception {
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8");
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - IPhone/reviews_acentos.txt")), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
        return reviewsStr;
    }

    private static String[] getCorpus2() throws Exception {
        File[] filesDirs = getFilesDirs("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/smart-tv-samsung-serie-4-un32j4300ag-32-polegadas-led-plana/acentos");
        List<String> aux = new ArrayList<>();
        for (File filesDir : filesDirs) {
            String fileText = new String(Files.readAllBytes(Paths.get(filesDir.getPath())), "UTF-8");
            String[] reviewsStr = fileText.split("\\[r\\]");
            aux.addAll(Arrays.asList(reviewsStr));
        }
        return aux.toArray(new String[aux.size()]);
    }
    

    private static File[] getFilesDirs(String folderDir) {
        File folder = new File(folderDir);
        boolean directory = folder.isDirectory();
        File[] files = null;
        if (directory) {
            files = folder.listFiles((File dir, String name) -> name.endsWith(".txt"));
        }
        return files;
    }

    private static Set<String> getTokensInexistentesNoLexico(List<String> lexico, Set<String> tokens) {
        Set<String> tokensInexistentes = new HashSet<>();
        tokens.forEach((token) -> {
            boolean contem = lexico.stream().anyMatch(x -> x.equalsIgnoreCase(token));
            if (contem) {
            } else {
                boolean contem2 = tokensInexistentes.stream().anyMatch(x -> x.equalsIgnoreCase(token));
                if (contem2) {
                } else {
                    tokensInexistentes.add(token);
                }
            }
        });
        return tokensInexistentes;
    }

//    private static String[] getReviews(String dir_arq) throws IOException {
//        List<String> reviews = new ArrayList<>();
//        FileInputStream excelFile = new FileInputStream(new File(dir_arq));
//        Workbook workbook = new XSSFWorkbook(excelFile);
//        Sheet datatypeSheet = workbook.getSheetAt(0);
//        Iterator<Row> iterator = datatypeSheet.iterator();
//        int i = 0;
//        while (iterator.hasNext()) {
//            Row currentRow = iterator.next();
//            Cell currentCell = currentRow.getCell(1);
//            Iterator<Cell> cellIterator = currentRow.cellIterator();
//            while (cellIterator.hasNext()) {
//                if (cellIterator.next().getColumnIndex() == 1) {
//                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
//                        reviews.add(currentCell.getStringCellValue());
//                    }
//                }
//            }
//        }
//        String[] rev = new String[reviews.size()];
//        rev = reviews.toArray(rev);
//        return rev;
//    }
}
