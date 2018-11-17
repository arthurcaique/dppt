/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author arthur
 */
public class ErroInternoStfdException extends Exception {

    public ErroInternoStfdException() {
        super("Ocorreu um erro interno");
    }

    public ErroInternoStfdException(String message) {
        super(message);
    }

    public ErroInternoStfdException(Throwable cause) {
        super(cause);
    }

}
