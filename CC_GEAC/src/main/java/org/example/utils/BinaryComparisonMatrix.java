/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.utils;

import org.example.CCGEAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximus
 */
public class BinaryComparisonMatrix {

//    public long contadorComprobaciones = 0L;
    public int numbCols, numbRows;

    public int costructsCount;
    public BinaryArray[] matrix;
    public int[] core;
    public int coreSize;
    public int minAttrSize;

    public int[] currentCandidate;
    public int currentAttr;

    public int[][] attrSetPartition;

    public BinaryComparisonMatrix() {
        this.numbRows = 0;
        this.numbCols = 0;
    }

    public BinaryComparisonMatrix(String filename) {
        init_matrix(filename);
    }

    public void set(int numb_rows, int numb_cols) {
        this.numbRows = numb_rows;
        this.numbCols = numb_cols;
        this.matrix = new BinaryArray[numb_cols];
        for (int i = 0; i < numb_cols; i++) {
            this.matrix[i] = new BinaryArray(numb_rows, i + 1);
        }
    }

    public void pushValue(int valor, int row, int column) {
        this.matrix[column].setValue(row, valor);
    }

    public int getValue(int row, int column) {
        return this.matrix[column].getValue(row);
    }

    public void rotate_matrix(int dir) {
        int beforeNumbRows = numbRows;
        int beforeNumbCols = numbCols;
        BinaryArray[] tmp_arr = new BinaryArray[numbCols];
        for (int i = 0; i < numbCols; i++) {
            BinaryArray.init(numbRows);
            tmp_arr[i] = new BinaryArray(numbRows, i + 1);
            for (int j = 0; j < numbRows; j++) {
                if (dir == 0) {
                    tmp_arr[i].setValue(j, matrix[j].getValue(i));
                } else if (dir == 1) {
                    tmp_arr[i].setValue(j, matrix[numbRows - 1 - j].getValue(i));
                }
            }
        }
        this.matrix = tmp_arr;
//        this.numbRows = beforeNumbCols;
//        this.numbCols = beforeNumbRows;
    }

    public void printMatrix() {
        for (BinaryArray attr : matrix) {
            System.out.println(attr.toString());
        }
    }

    private void init_matrix(String filename) {
        try ( BufferedReader br = new BufferedReader(new FileReader(filename))) {
            numbRows = Integer.parseInt(br.readLine().trim());

            numbCols = Integer.parseInt(br.readLine().trim());

            String line = br.readLine();
            int r_id = 0;
            coreSize = 0;
            BinaryArray.init(numbCols); //matrix constructed in base of rows
            matrix = new BinaryArray[numbRows];
            //initialization of the rows on the matrix
            core = new int[numbCols];
            while (line != null) {
                matrix[r_id] = new BinaryArray(line, r_id);
                if (matrix[r_id].only1One()) {
                    core[coreSize++] = matrix[r_id].getFirstNZCol();
                }
                r_id++;
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(CCGEAC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printCore() {
        for (int i = 0; i < coreSize; i++) {
            System.out.print(" " + (core[i] + 1) + " ");
        }
    }

    private int sortCols() {
        int p = 0;
        quickSort(matrix, 0, matrix.length - 1);
//        printMatrix();
        rotate_matrix(0);
//        printMatrix();
        quickSort(core, 0, coreSize - 1);
        for (int i = 0; i < coreSize; i++) {
            if (core[i] != i) {
                BinaryArray tmp = this.matrix[i];
                this.matrix[i] = this.matrix[core[i]];
                this.matrix[core[i]] = tmp;
            }
            p++;
        }
        int zeroCols = 0;
        for (int i = coreSize; i < this.numbCols - zeroCols;) {
            if (matrix[i].isEmpty()) {
                zeroCols++;
                BinaryArray tmp = this.matrix[i];
                this.matrix[i] = this.matrix[this.numbCols - zeroCols];
                this.matrix[this.numbCols - zeroCols] = tmp;
            } else {
                i++;
            }
        }
        this.numbCols = this.numbCols - zeroCols;
//        printMatrix();
        this.minAttrSize = 0;
        for (int i = coreSize; i < this.numbCols; i++) {
            if (this.matrix[p].getValue(coreSize) == 1) {
                p++;
                this.minAttrSize++;
            } else if (this.matrix[i].getValue(coreSize) == 1) {
                BinaryArray tmp = this.matrix[p];
                this.matrix[p] = this.matrix[i];
                this.matrix[i] = tmp;
                p++;
                this.minAttrSize++;
            }
        }
//        if (p+1<numbCols) {
//            quickSort(matrix, p+1, matrix.length - 1);
//        }
//        System.out.println("min attr size: "+minAttrSize);
        return this.minAttrSize;
    }

    public void quickSort(BinaryArray arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(BinaryArray arr[], int begin, int end) {
        BinaryArray pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            int comp_value = arr[j].compareTo(pivot);
            if (comp_value == 0 || comp_value == -1) {
                i++;
                BinaryArray swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        BinaryArray swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;

        return i + 1;
    }

    public int sortMatrix() {
        this.minAttrSize = sortCols();
        return this.minAttrSize;
    }

    public static enum Direction {
        LEFT, RIGHT
    }

    public int getCoreSize() {
        return coreSize;
    }

    public int getMinAttrSize() {
        return minAttrSize;
    }

    public int[][] genAttributePartition() {
        BinaryArray.compareByNumbOnes = false;
        int minAttrNumb = minAttrSize;
        boolean[] visited = new boolean[numbCols];
        int numb_subsets = 0;
        int counter;
        int[][] partition = new int[numbCols][numbCols + 1];
        int numbAttrs;
        for (int i = 0; i < numbCols; i++) {
            if (!visited[i]) {
                partition[numb_subsets][0] = i;
                partition[numb_subsets][1] = 0;
                counter = 0;
                for (int j = i + 1; j < numbCols; j++) {
                    if (!visited[j]) {
                        boolean equals = matrix[i].eq(matrix[j]);
                        if (equals) {//super col
                            counter++;
                            partition[numb_subsets][counter + 2] = j;
                            visited[j] = true;
                            if (i >= coreSize && i < this.minAttrSize + coreSize && j < this.minAttrSize + coreSize) {
                                minAttrNumb--;
                            }
                        }
                    }
                }
                partition[numb_subsets][1] = counter;
                numb_subsets++;
            }
        }
        numbAttrs = numb_subsets - coreSize;
        int[][] attrSubsets = new int[numbAttrs][];
        int index = 0;
        for (int i = coreSize; i < numb_subsets; i++) {
            int cols = partition[i][1] + 1;
            attrSubsets[index] = new int[cols];
            attrSubsets[index][0] = partition[i][0];
            for (int j = 1; j < cols; j++) {
                attrSubsets[index][j] = partition[i][j + 2];
            }
            index++;
        }
        this.minAttrSize = minAttrNumb;
//        System.out.println("min attr size after partition: "+minAttrNumb);
        BinaryArray.compareByNumbOnes = true;
        attrSetPartition = attrSubsets;
        numbCols = numbAttrs;
        return attrSubsets;
    }

    public boolean reduct(ReductCandidate s_reduct, BinaryArray[] acceptanceMasks) {
        BinaryArray AMl = new BinaryArray(this.numbRows, -1);
        BinaryArray CMl = new BinaryArray(this.numbRows, -1);
        int i;
        for (i = 0; i <= s_reduct.last; i++) {
            int subsetIndex = s_reduct.attrs[i];
            int attr_index = this.attrSetPartition[subsetIndex][0];
            CMl.mascComp(CMl, matrix[attr_index], AMl);
            AMl = acceptanceMasks[subsetIndex + 1];
        }

        for (i = 0; i <= s_reduct.last; i++) {
            int subsetIndex = s_reduct.attrs[i];
            int attr_index = this.attrSetPartition[subsetIndex][0];
            if (this.matrix[attr_index].andNEqZ(CMl)) {
                return false;
            }
        }
        return true;
    }

    public boolean isR(ReductCandidate s_reduct, BinaryArray[] mascaraComp, int index, BinaryArray enCurso_x, BinaryArray[] mascaraAcep) {
        mascaraComp[index + 1].mascComp(mascaraComp[index], enCurso_x, mascaraAcep[index]);
        boolean condicion2 = false;
        for (int i = 0; i <= index; i++) {
            int subsetIndex = s_reduct.attrs[i];
            int attr_index = this.attrSetPartition[subsetIndex][0];
            if (matrix[attr_index].and(mascaraComp[index + 1])) {
                condicion2 = true;
                break;
            }
        }
        return condicion2;
    }

    public void printPartition() {
        for (int[] row : attrSetPartition) {
            System.out.print("[");
            for (int value : row) {
                System.out.print(" " + matrix[value].getId() + " ");
            }
            System.out.println("]");
        }
    }

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /* This function takes last element as pivot, places
   the pivot element at its correct position in sorted
   array, and places all smaller (smaller than pivot)
   to left of pivot and all greater elements to right
   of pivot */
    static int partition(int[] arr, int low, int high) {

        // pivot
        int pivot = arr[high];

        // Index of smaller element and
        // indicates the right position
        // of pivot found so far
        int i = (low - 1);

        for (int j = low; j <= high - 1; j++) {

            // If current element is smaller
            // than the pivot
            if (arr[j] < pivot) {

                // Increment index of
                // smaller element
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    /* The main function that implements QuickSort
          arr[] --> Array to be sorted,
          low --> Starting index,
          high --> Ending index
     */
    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {

            // pi is partitioning index, arr[p]
            // is now at right place
            int pi = partition(arr, low, high);

            // Separately sort elements before
            // partition and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

}
