/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unitexpb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author arthur
 */
public class UnitexPB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        List<String> readAllLines = Files.readAllLines(Paths.get("/home/arthur/Documentos/Delaf2015v04_2.dic"));
    }
    
}
