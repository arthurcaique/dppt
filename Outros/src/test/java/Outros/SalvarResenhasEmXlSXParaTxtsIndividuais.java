/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Outros;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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
public class SalvarResenhasEmXlSXParaTxtsIndividuais {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        List<String> reviews = getReviews("/home/arthur/Documentos/Smartphone Motorola Moto G XT1032 8 GB/Smartphone Motorola Moto G XT1032 8 GB.xlsx");
        for (int i = 0; i < reviews.size(); i++) {
            String resenha = reviews.get(i);
            int j = i + 1;
            Files.write(resenha, new File("/home/arthur/Documentos/Smartphone Motorola Moto G XT1032 8 GB/resenhas originais/"+j+".txt"), Charset.forName("UTF-8"));
        }
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
            if (Objects.nonNull(currentCell) && currentCell.getCellTypeEnum() == CellType.STRING) {
                reviews.add(currentCell.getStringCellValue());
            }
        }
        return reviews;
    }
}
