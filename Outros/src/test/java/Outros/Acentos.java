/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.acbv.propagacao_dupla.preprocessamento.Tokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
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
public class Acentos {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> corpus = getReviews("/home/arthur/PycharmProjects/webscrapper/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb.xlsx");
        Set<String> bagOfWords = getBagOfWords(corpus);
        List<String> lexico3 = getLexico3("/home/arthur/Documentos/Delaf2015v04_2.dic");
        HashMap<String, String> pegarTokensComAcentosFaltantes = pegarTokensComAcentosFaltantes(bagOfWords, lexico3);
        Set<Map.Entry<String, String>> entrySet = pegarTokensComAcentosFaltantes.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            System.out.println(entry);
        }
        List<String> substituir = substituir(corpus, pegarTokensComAcentosFaltantes);
        Files.write(Paths.get("/home/arthur/PycharmProjects/webscrapper/apple-macbook-pro-md101bz-intel-core-i5-2-5-ghz-4096-mb-500-gb_acentos.txt"), substituir);
    }

    public static List<String> getCorpus() throws Exception {
        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - smartphone/Documento sem titulo")), "UTF-8").toLowerCase();
//        String fileText = new String(Files.readAllBytes(Paths.get("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/Submarino - IPhone/reviews.txt")), "UTF-8");
        String[] reviewsStr = fileText.split("\\[r\\]");
        List<String> corpus = Arrays.asList(reviewsStr);
        return corpus;
    }

    private static List<String> getReviews(String dir_arq) throws IOException {
        List<String> reviews = new ArrayList<>();
        FileInputStream excelFile = new FileInputStream(new File(dir_arq));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Cell currentCell = currentRow.getCell(1);
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            while (cellIterator.hasNext()) {
                if (cellIterator.next().getColumnIndex() == 1) {
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        reviews.add(currentCell.getStringCellValue());
                    }
                }
            }
        }
        return reviews;
    }

    private static List<String> getLexico3(String diretorio) throws IOException {
        System.out.println("Pegando léxico");
        List<String> lexicoList = Files.readAllLines(Paths.get(diretorio));
        System.out.println("Pegou léxico");
        return lexicoList;
    }

    private static Set<String> getBagOfWords(List<String> corpus) throws Exception {
        System.out.println("Tokenizando");
        Tokenizer stfUtils = new Tokenizer();
        Set<String> tokensSet = new HashSet<>();
        for (String corpu : corpus) {
            tokensSet.addAll(stfUtils.getTokens(corpu));
        }
        System.out.println("Tokenizou");
        return tokensSet;
    }

    public static HashMap<String, String> pegarTokensComAcentosFaltantes(Set<String> tokens, List<String> lexico) throws UnsupportedEncodingException {
        HashMap<String, String> tokens_token = new HashMap<>();
        System.out.println("Pegando tokens inexistentes");
        System.out.println("-> Tirando acento dos tokens");
        List<String> lexicoSemAcento = tirarAcentosLexico(new ArrayList(lexico));
        System.out.println("-> Acentos retirados");
        tokens.forEach((token) -> {
            boolean contem = lexico.stream().anyMatch(x -> x.equalsIgnoreCase(token));
            if (contem) {
            } else {
                boolean contem2 = lexicoSemAcento.stream().anyMatch(x -> x.equalsIgnoreCase(token));
                if (contem2) {
                    int indice = IntStream.range(0, lexicoSemAcento.size())
                            .filter(i -> lexicoSemAcento.get(i).equalsIgnoreCase(token)).findFirst().getAsInt();
                    tokens_token.put(token, lexico.get(indice));
                }
            }
        });
        System.out.println("Pegou tokens inexistentes");
        return tokens_token;
    }

    private static List<String> tirarAcentosLexico(List<String> lexico) throws UnsupportedEncodingException {
        for (int i = 0; i < lexico.size(); i++) {
            lexico.set(i, StringUtils.stripAccents(lexico.get(i)));
        }
        return lexico;
    }

    private static List<String> substituir(List<String> corpus, HashMap<String, String> pegarTokensComAcentosFaltantes) throws Exception {
        List<String> preProcessado = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer();
        for (String corpu : corpus) {
            List<String> tokens = tokenizer.getTokens(corpu);
            Set<String> palavrasSemAcento = pegarTokensComAcentosFaltantes.keySet();
            for (int i = 0; i < tokens.size(); i++) {
                for (String palavraSemAcento : palavrasSemAcento) {
                    String newToken = "";
                    String token = tokens.get(i);
                    if (token.equalsIgnoreCase(palavraSemAcento)) {
                        String[] tokenSplit = token.split("");
                        String[] palavraComAcentoSplit = pegarTokensComAcentosFaltantes.get(palavraSemAcento).split("");
                        if (tokenSplit.length == palavraComAcentoSplit.length) {
                            for (int j = 0; j < tokenSplit.length; j++) {
                                char t = tokenSplit[j].charAt(0);
                                String t2 = palavraComAcentoSplit[j];
                                boolean isUpper = Character.isUpperCase(t);
                                if (isUpper) {
                                    newToken = newToken.concat(t2.toUpperCase());
                                } else {
                                    newToken = newToken.concat(t2);
                                }
                            }
                        } else {
                            newToken = token;
                        }
                    } else {
                        newToken = token;
                    }
                    tokens.set(i, newToken);
                }
            }
            String texto = "";
            for (String token : tokens) {
                texto = texto.concat(token + " ");
            }
            preProcessado.add("[r] " + texto.trim() + "\n");
        }
        return preProcessado;
    }

}
