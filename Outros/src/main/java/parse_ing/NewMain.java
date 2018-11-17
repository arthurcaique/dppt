/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parse_ing;
import com.acbv.propagacao_dupla.controllers.Controller;
import com.acbv.propagacao_dupla.entidades.Corpus;
import com.acbv.propagacao_dupla.entidades.Resenha;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONObject;
/**
 *
 * @author arthur
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        Controller controller = Controller.getController();
        Corpus corpus = controller.getCorpusMD101Bz();
        List<Resenha> resenhas = corpus.resenhas;
        corpus.resenhas = resenhas.subList(0, 2);
        JSONObject corpusToJSON = Metodo.corpusToJSON(corpus);
        System.out.println(corpusToJSON.toString());
    }
    
}
