/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acbv.propagacao_dupla.entidades;

/**
 *
 * @author arthur
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Aspecto aspecto = new Aspecto("arthur");
        Aspecto aspecto1 = new Aspecto("arthur");
        aspecto1.getPalavras().add("caíque");
        if(aspecto.equals(aspecto1)){
            System.out.println("FOI");
        }
    }
    
}
