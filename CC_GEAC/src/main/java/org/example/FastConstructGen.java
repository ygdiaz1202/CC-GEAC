package org.example;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.example.utils.BinaryArray;
import org.example.utils.BinaryComparisonMatrix;
import org.example.utils.CandGeneration;

import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Yanir Gonzalez
 */
public class FastConstructGen {

    private BinaryComparisonMatrix bcm;
    private BinaryArray[] acceptanceMasks;
    //    BinaryArray[] mascaraComp;
    private String dis_file_path;
    private CandGeneration genC;
    private long candidates;
    private BinaryArray[] acm;

    public FastConstructGen(File disFile) {
        this.dis_file_path = disFile.getAbsolutePath();
    }

    private void init() {
        bcm = new BinaryComparisonMatrix(dis_file_path);
        bcm.sortMatrix();
        bcm.genAttributePartition();
        acceptanceMasks = new BinaryArray[bcm.numbCols + 1];
        genC = new CandGeneration(bcm.minAttrSize, bcm.numbCols);
        //            mascaraComp[i] = new BinaryArray(bcm.numbRows, -1);
        IntStream.range(0, bcm.numbCols + 1).forEach(i -> acceptanceMasks[i] = new BinaryArray(bcm.numbRows, -1));
        if (bcm.coreSize > 0) {
            if (bcm.coreSize == 1) {
                acceptanceMasks[0] = bcm.matrix[0];
            } else {
                BinaryArray mask = new BinaryArray(bcm.numbRows, -1);
                mask.copy(bcm.matrix[0]);
                for (int i = 1; i < bcm.coreSize; i++) {
                    acceptanceMasks[0].mascAcep(mask, bcm.matrix[i]);
                    mask.copy(acceptanceMasks[0]);
                }
            }
        }
        initAccumulativeMasks();
    }

    public void getAllConstructs() {
        long startTime = System.currentTimeMillis();
        init();
        boolean nextAttrSubset = true;
        candidates = 0;
        long super_construct = 0;
        long constructs = 0;
        int attrIndex;
        if (acceptanceMasks[0].esUnitario()) {//If the core is a constructs then is the only construct
            constructs++;
        } else {
            while (nextAttrSubset) {
                BinaryArray AMl;
                boolean isSuperConstruct = false;
                boolean isConstruct = false;
                if (genC.currentX == -1) {
                    AMl = acceptanceMasks[0];
                } else {
                    AMl = acceptanceMasks[genC.currentX + 1];
                }
                attrIndex = bcm.attrSetPartition[genC.nextX][0];
                boolean contributes = !acceptanceMasks[genC.nextX + 1].mascAcep(AMl, bcm.matrix[attrIndex]);
                boolean hasFuture = true;
                candidates++;

                if (contributes) {
                    hasFuture = canBePartOfAnyConstruct(AMl, genC.nextX);
                    if (hasFuture && acceptanceMasks[genC.nextX + 1].esUnitario()) {
                        isSuperConstruct = true;
                        super_construct++;
                        if (bcm.reduct(genC.CurrentCandidate, acceptanceMasks)) {
                            constructs++;
                            isConstruct = true;
                            //Processing possibles subsets taking in account the rest of
                            //the attributes in the partition of the attribute set 
                            constructs += genRestSubsets();
//                            printR(null);
                        }
                    }
                }
                nextAttrSubset = genC.genNextAttrSubset(isSuperConstruct, contributes, isConstruct, hasFuture);
            }
        }
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        printLog(time, constructs, super_construct, candidates);
    }

    private int genRestSubsets() {
        int count = 0;
        int last_attr_index = genC.CurrentCandidate.last;
        boolean anySubset = false;
        int[] last = new int[last_attr_index + 1];
        for (int i = 0; i < last.length; i++) {
            last[i] = bcm.attrSetPartition[genC.CurrentCandidate.attrs[i]].length - 1;
            if (!anySubset && last[i] != 0) {
                anySubset = true;
            }
        }
        int[] sol = new int[last_attr_index + 1];
        while (anySubset) {
            int index = last_attr_index;
            boolean spaceFound = false;
            while (!spaceFound && index >= 0) {
                int currF = sol[index];
                if (currF < last[index]) {
                    int nextF = currF + 1;
                    sol[index] = nextF;//The next solution is set up
                    for (int i = index + 1; i <= last_attr_index; i++) {
                        int currentValue = sol[i] + 1;
                        if (currentValue <= last[i]) {
                            sol[i] = currentValue;
                        } else {
                            sol[i] = 0;
                        }
                    }
                    spaceFound = true;
                }
                index--;
            }
            if (!spaceFound) {
                anySubset = false;
            } else {
                candidates++;
                count++;
                printConstruct(sol);
            }
        }
//        System.out.println("----------------------");
        return count;
    }

    private void printLog(long time, long constructs, long super_construct, long candidates) {
        System.out.println("time: " + time + "ms");
        System.out.println("Constructs: " + constructs);
        System.out.println("Super Constructs: " + super_construct);
        System.out.println("Candidates: " + candidates);
    }

    private void printConstruct(int[] constr) {
        int subsetIndex;
        int indexInSubset;
        int attrIndex;
        int size = bcm.numbCols + bcm.coreSize;
        int[] construct = new int[size];
        Arrays.fill(construct, -1);
        boolean first = constr == null;
        int constructSize = 0;
        for (int i = 0; i < bcm.coreSize; i++) {
            construct[i] = (bcm.core[i] + 1);
            constructSize++;
        }

        for (int i = 0; i <= genC.CurrentCandidate.last; i++) {
            subsetIndex = genC.CurrentCandidate.attrs[i];
            indexInSubset = (first) ? 0 : constr[i];
            attrIndex = bcm.attrSetPartition[subsetIndex][indexInSubset];
            construct[i + bcm.coreSize] = bcm.matrix[attrIndex].getId();
            constructSize++;
        }
        Arrays.sort(construct, 0, constructSize);
        System.out.print("[");
        for (int i = 0; i < constructSize; i++) {
            System.out.print(construct[i] + " ");
        }
        System.out.println("]");
    }

    private void printArr(int[] arr, int numbElem) {
        System.out.print("[");
        for (int i = 0; i <= numbElem; i++) {
            System.out.print(" " + arr[i] + " ");
        }
        System.out.println("]");
    }

    private void initAccumulativeMasks() {
        int numbAttrs = bcm.attrSetPartition.length;
        acm = new BinaryArray[numbAttrs];
        int attr_index;
        for (int i = 0; i < numbAttrs; i++) {
            attr_index = bcm.attrSetPartition[i][0];
            acm[i] = new BinaryArray(bcm.numbRows, -1);
            acm[i].copy(acceptanceMasks[0]);
            acm[i].operatorOr(bcm.matrix[attr_index]);
            for (int j = i + 1; j < numbAttrs; j++) {
                attr_index = bcm.attrSetPartition[j][0];
                acm[i].operatorOr(bcm.matrix[attr_index]);
            }
        }
    }

    private boolean canBePartOfAnyConstruct(BinaryArray AMl, int next) {
        BinaryArray masc = new BinaryArray(bcm.numbRows, -1);
        masc.copy(AMl);
        if (acm == null) {
            System.out.println("It's necessary to initialize the accumulative matrix");
            System.exit(1);
        }
        return masc.operatorOr(acm[next]);
    }
}
