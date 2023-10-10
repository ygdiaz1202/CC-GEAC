package org.example.utils;

import org.example.CCGEAC;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComparisonMatrix {
    //    public long contadorComprobaciones = 0L;
    public static int colsNumber, rowsNumber;

    public static boolean [][] matrix;
    public static int[] kernel;
    public static int kernelSize;
    public static int minAttrSize;
    public static int[] MAS;
    public static int [] realAttrIndex;
    public  static  int[] onesPerCol;
//    private int[][] attrSetPartition;

    public ComparisonMatrix(File filename) {
        init_matrix(filename);
    }

    private void init_matrix(File filename) {
        try ( BufferedReader br = new BufferedReader(new FileReader(filename))) {
            rowsNumber = Integer.parseInt(br.readLine().trim());

            colsNumber = Integer.parseInt(br.readLine().trim());
            System.out.println("rows: "+rowsNumber+" cols: "+colsNumber);

            String line = br.readLine();
            int r_id = 0;
            kernelSize = 0;
            matrix = new boolean[rowsNumber][colsNumber];
            //initialization of the rows on the matrix
            kernel = new int[colsNumber];
            onesPerCol = new int[colsNumber];
            Arrays.fill(kernel,-1);
            MAS = new int[colsNumber];
            Arrays.fill(MAS,-1);
            minAttrSize = colsNumber+1;
            while (line!=null) {
                boolean copied=setRow(line, r_id);
                if (copied){
                    r_id++;
                }
                line = br.readLine();
            }
            if (minAttrSize==colsNumber+1)
                minAttrSize=0;
            rowsNumber=r_id; //all the rows that wasn't copied are removed
            Arrays.sort(kernel,0, kernelSize);
            realAttrIndex=new int[colsNumber];
            Arrays.fill(realAttrIndex,-1);
//            for (int i = 0; i < colsNumber; i++) {
//                realAttrIndex[i]=i;
//            }
            matrix=sortMatrix();

//            printArr(matrix);
//            printArray(kernel,kernelSize);
//            colsNumber=colsNumber- kernelSize; //the number of attributes is updated
//            realAttrIndex.toString();
//            MAS.toString();
        } catch (IOException ex) {
            Logger.getLogger(CCGEAC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private @NotNull boolean[][] sortMatrix() {
        int minAttrStart=0;
        int minAttEnd=minAttrSize;
        int kernelStart=(colsNumber-kernelSize);
        int kernelIndex=0;
//        System.out.println(rowsNumber+" , "+colsNumber);
        boolean[][] tmpMatrix=new boolean[rowsNumber][colsNumber];
        int numberOfZeroCols=0;
        for (int c = 0; c < colsNumber; c++) {
             if(minAttrStart<minAttrSize && c==MAS[minAttrStart]){
                 copyCol(matrix,c,tmpMatrix,minAttrStart);
                 realAttrIndex[minAttrStart]=MAS[minAttrStart];
                 minAttrStart++;
             }else if(kernelIndex<kernelSize && c==kernel[kernelIndex]){
                 copyCol(matrix,c,tmpMatrix,kernelStart);
                 realAttrIndex[kernelStart]=c;
                 kernelStart++;
                 kernelIndex++;
             }else{
                 if (onesPerCol[c]>0)
                 {
                 copyCol(matrix,c,tmpMatrix,minAttEnd);
                 realAttrIndex[minAttEnd]=c;
                 minAttEnd++;
                 }else{
                     numberOfZeroCols++;
                 }
             }
        }
//        System.out.println(numberOfZeroCols);
        colsNumber-=(numberOfZeroCols+kernelSize);
        return tmpMatrix;
    }

    private void copyCol(boolean[][] matrix, int ma, boolean[][] tmpMatrix, int c) {
        for (int i = 0; i < rowsNumber; i++) {
            tmpMatrix[i][c]=matrix[i][ma];
        }
    }

    //   return true if the row was copied
    @Contract(pure = true)
    private boolean setRow(@NotNull String line, int r_id) {
        String[] temp = line.trim().replace("\t", " ").split(" ");
        boolean [] row = new boolean[colsNumber];
        int [] tmp= new int[colsNumber];
        int col=0;
        int numberOfOnes= 0;
        int attrIndex = 0;

        for (String value: temp) {
            boolean val= (value.charAt(0) == '1');
            if (val) {
                attrIndex=col;
                if (numberOfOnes<minAttrSize){
                    tmp[numberOfOnes]=col;
                }
                numberOfOnes++;
                onesPerCol[col]++;
            }
            row[col] = val;
            col++;
        }
        if(numberOfOnes==1){
            kernel[kernelSize]=attrIndex;
            kernelSize++;
        }else {
            if (numberOfOnes>1 && numberOfOnes<minAttrSize){
                minAttrSize=numberOfOnes;
                System.arraycopy(tmp, 0, MAS, 0, numberOfOnes);
            }
            System.arraycopy(row,0,matrix[r_id],0,colsNumber);
            return true;
        }
        return false;
    }


    public static void printArray(int [] arr, int elements) {
        if (elements==-1){
            elements=arr.length;
        }
        System.out.print("[");
        for (int i = 0; i < elements-1; i++) {
            System.out.print((arr[i] + 1) + ", ");
        }
        System.out.print((arr[elements-1] + 1));
        System.out.println("]");
    }

    public void printCore() {
        System.out.println("showing the core: ");
        printArray(kernel, kernelSize);
    }

    public void printMAS() {System.out.println("showing the MAS: ");
        printArray(MAS,minAttrSize);
    }


    public int[] getCandidates(int[] actualCandidate){

        return null;
    }

    public  static int @Nullable [] updateZeroMask(int @NotNull [] mask, int newAttr){
          int count=0;
          for (int i=0;i<mask.length;i++){
              if (matrix[mask[i]][newAttr]){
                  mask[i]=-1;
              }else
                  count++;
          }
          if (count!=0){
            int []tmp=new int[count];
            int i=0;
            for (int value:mask){
                if (value!=-1)
                    tmp[i++]=value;
            }
            return tmp;
          }else return null;
    }

    public static boolean theKernelIsConstruct(){
        return (rowsNumber==0);
    }

    public static void printArr(boolean[][]arr){
        for (boolean[] booleans : arr) {
            for (int j = 0; j < arr[0].length; j++) {
                if (booleans[j])
                    System.out.print(1 + " ");
                else
                    System.out.print(0 + " ");
            }
            System.out.println();
        }
    }

}
