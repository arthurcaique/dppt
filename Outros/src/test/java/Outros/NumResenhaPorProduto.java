/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
public class NumResenhaPorProduto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File file = new File("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/tvs_smartvs");
        int maior = 0;
        File[] filesXls = file.listFiles((dir, name) -> {
            return name.endsWith(".xlsx"); //To change body of generated lambdas, choose Tools | Templates.
        });
        int numProd = filesXls.length;
        double contReviews = 0;
        for (File filesXl : filesXls) {
            List<String> reviews = getReviews(filesXl.getAbsolutePath());
            contReviews += reviews.size();
            if (reviews.isEmpty()) {
                numProd--;
            }
            if(reviews.size() > maior){
                maior = reviews.size();
            }
            if(reviews.size() > 50){
                System.out.println(filesXl);
            }
        }
        System.out.println("Mais resenhas: "+maior);
        System.out.println("Número de produtos: " + numProd);
        double mediaResenhasProd = contReviews / numProd;
        System.out.println("Número total de resenhas: "+contReviews);
        System.out.println("Média de resenhas por produto: " + mediaResenhasProd);
//        List<String> reviews = getReviews("/home/arthur/Dropbox/Dissertacao/Reviews Portugues/buscapé/notebooks/Apple Macbook Pro MD101BZ Intel Core i5 2.5 GHz 4096 MB 500 GB.xlsx");
//        System.out.println(reviews.size());
    }

    private static List<String> getReviews(String dir_arq) throws IOException {
        List<String> reviews = new ArrayList<>();
        FileInputStream excelFile = new FileInputStream(new File(dir_arq));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = datatypeSheet.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            i++;
            Row currentRow = iterator.next();
            Cell currentCell = currentRow.getCell(3);
            if (Objects.nonNull(currentCell) && currentCell.getCellTypeEnum() == CellType.STRING && !currentCell.getStringCellValue().trim().isEmpty()) {
                reviews.add(currentCell.getStringCellValue());
            }
        }
        return reviews;
    }
}
