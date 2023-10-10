/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example;

import java.io.File;

/**
 *
 * @author Yanir Gonzalez
 */
public class CCGEAC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
////        long startTime = System.currentTimeMillis();
//        // TODO code application logic here
////        long startTime = System.currentTimeMillis();
        if (args.length != 1) {
            System.out.println("The only one argument is the input file name!");
        }
        String filename = args[0];
//        String filename = "bases/test_disc_0.2.bol";
        File disFile = new File(filename);
        System.out.println(disFile.getName());
        FastConstructGen fcg = new FastConstructGen(disFile);
        fcg.getAllConstructs();

//        fcg.bcm.printPartition();
    }

}
