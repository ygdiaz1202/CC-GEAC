package org.example.utils;


import java.util.Arrays;

public class Candidate implements Cloneable {


    private int[] maskZeros = null;
    //    private boolean[] acm=null;
//    private boolean [] acm_counter;
    private int[] attrIndex;
    private int[] minAttrSet;
    private int attrNumber = 0;
    private int minAttrNumber = 0;
    private int nextAttr_pt = -1;
    private int restAttrNumber = 0;


    public Candidate(int[] attrIndex, int[] maskZeros, int attrNumber) {
        this.attrIndex = attrIndex;
        this.maskZeros = maskZeros;
        this.attrNumber = attrNumber;
//        this.acm = acm;
//        this.acm_counter=acm_counter;
    }

    public Candidate(int[] attrs) {
        attrIndex = new int[ComparisonMatrix.colsNumber];
        Arrays.fill(attrIndex, -1);
        for (int attr : attrs) {
            attrIndex[attrNumber++] = attr;
        }
        minAttrSet = new int[ComparisonMatrix.colsNumber];
        Arrays.fill(minAttrSet, -1);
        initZeroMask();
    }

    public Candidate(int attr) {
        attrIndex = new int[ComparisonMatrix.colsNumber];
        Arrays.fill(attrIndex, -1);
        attrIndex[attrNumber++] = attr;
        initZeroMask();
        initMinAttrSet();
    }

    public int[] getMaskZeros() {
        return maskZeros;
    }

    public void setMaskZeros(int[] maskZeros) {
        this.maskZeros = maskZeros;
    }

    public int[] getAttrIndex() {
        return attrIndex;
    }

    public void setAttrIndex(int[] attrIndex) {
        this.attrIndex = attrIndex;
    }

    public int[] getMinAttrSet() {
        return minAttrSet;
    }

    public void initMinAttrSet(int[] minAttrSet) {
        this.minAttrSet = minAttrSet;
    }

    public void initMinAttrSet() {
        if (this.maskZeros != null) {
            minAttrSet = new int[ComparisonMatrix.colsNumber];
            Arrays.fill(minAttrSet, -1);

            int lessOnes = ComparisonMatrix.colsNumber;
            int lessRow = -1;
            for (int row : this.maskZeros) {
                int numbOnes = 0;
                for (int col = this.attrIndex[0] + 1; lessOnes > numbOnes && col < ComparisonMatrix.colsNumber; col++) {
                    if (ComparisonMatrix.matrix[row][col]) numbOnes++;
                }
                if (lessOnes > numbOnes) {
                    lessOnes = numbOnes;
                    lessRow = row;
                }
            }
            int i = 0;
            for (int col = this.attrIndex[0] + 1; col < ComparisonMatrix.colsNumber; col++) {
                if (ComparisonMatrix.matrix[lessRow][col]) {
                    this.minAttrSet[i++] = col;
                } else {
                    this.minAttrSet[lessOnes + restAttrNumber] = col;
                    restAttrNumber++;
                }
            }
            this.minAttrNumber = lessOnes;
            this.nextAttr_pt = 0;
        }
    }

    public void fillMinAttrSet(Candidate father_ref) {
        if (maskZeros != null) {
            minAttrSet = new int[ComparisonMatrix.colsNumber];
            Arrays.fill(minAttrSet, -1);

            int lessOnes = ComparisonMatrix.colsNumber;
            int lessRow = -1;
            int numbElements = father_ref.minAttrNumber + father_ref.restAttrNumber;
            for (int row : this.maskZeros) {
                int numbOnes = 0;
                for (int col = father_ref.nextAttr_pt; lessOnes > numbOnes && col < numbElements; col++) {
                    int attr = father_ref.minAttrSet[col];
                    if (ComparisonMatrix.matrix[row][attr]) numbOnes++;
                }
                if (lessOnes > numbOnes) {
                    lessOnes = numbOnes;
                    lessRow = row;
                }
            }
            int i = 0;
            for (int col = father_ref.nextAttr_pt; col < numbElements; col++) {
                int attr = father_ref.minAttrSet[col];
                if (ComparisonMatrix.matrix[lessRow][attr])
                    this.minAttrSet[i++] = attr;
                else {
                    this.minAttrSet[lessOnes + restAttrNumber] = attr;
                    restAttrNumber++;
                }
            }
            this.nextAttr_pt = 0;
            this.minAttrNumber = lessOnes;
        }
    }


    public void addAttr(int attr) {
        this.minAttrSet[minAttrNumber++] = attr;
    }

    private void initZeroMask() {
        int[] tmp = new int[ComparisonMatrix.rowsNumber];
        int counter = 0;
        for (int i = 0; i < ComparisonMatrix.rowsNumber; i++) {
            boolean onlyZeros = true;
            for (int j = 0; j < this.attrNumber; j++) {
                if (ComparisonMatrix.matrix[i][this.attrIndex[j]]) {
                    onlyZeros = false;
                    break;
                }
            }
            if (onlyZeros) {
                tmp[counter] = i;
                counter++;
            }
        }
        if (counter != 0) {
            this.maskZeros = new int[counter];
            System.arraycopy(tmp, 0, this.maskZeros, 0, counter);
        }
    }

    public Candidate nextCandidate() {
        if (nextAttr_pt < minAttrNumber) {
            int[] newZeroMask = ComparisonMatrix.updateZeroMask(maskZeros.clone(), minAttrSet[nextAttr_pt]);
            int[] newAttrIndex = attrIndex.clone();
            newAttrIndex[attrNumber] = minAttrSet[nextAttr_pt];
            nextAttr_pt++;
            return new Candidate(newAttrIndex, newZeroMask, attrNumber + 1);
        } else return null;
    }

//    public void update_acm() {
//        int i =0;
//        for (boolean val: acm) {
//            boolean cm_ci=ComparisonMatrix.matrix[i][attrIndex[attrNumber-1]];
//            boolean changeable=acm_counter[i];
//            if (changeable){
//                if (cm_ci){
//                    if (val){
//                        acm[i]=false;
//                        acm_counter[i]=false;
//                    }else {
//                        acm_counter[i]=true;
//                    }
//                }
//            }
//            i++;
//        }
//    }

    public boolean isSuperConstruct() {
        return maskZeros == null;
    }

    public boolean isExclusionary() {
        boolean[] acm1 = new boolean[ComparisonMatrix.rowsNumber];
        for (int i = 0; i < ComparisonMatrix.rowsNumber; i++) {
            int c = 0;
            for (int j = 0; c < 2 && j < attrNumber; j++) {
                if (ComparisonMatrix.matrix[i][attrIndex[j]])
                    c++;
            }
            acm1[i] = (c == 1);
        }

        for (int index = 0; index < attrNumber - 1; index++) {
            boolean allZeros = true;
            for (int row = 0; row < ComparisonMatrix.rowsNumber; row++) {
                boolean b = acm1[row] && ComparisonMatrix.matrix[row][attrIndex[index]];
                if (b) {
                    allZeros = false;
                    break;
                }
            }
            if (allZeros)
                return false;
        }
        return true;
    }

    public int[] getConstruct() {
        int[] construct = new int[attrNumber];
        for (int i = 0; i < attrNumber; i++) {
            int attrIndexInM = attrIndex[i];
            construct[i] = ComparisonMatrix.realAttrIndex[attrIndexInM];
        }
        Arrays.sort(construct);
        return construct;
    }


    public Candidate(int[] maskZeros, int[] attrIndex, int[] minAttrSet, int attrNumber, int lastminAttr_pt) {
        this.maskZeros = maskZeros;
        this.attrIndex = attrIndex;
        this.minAttrSet = minAttrSet;
        this.attrNumber = attrNumber;
        this.minAttrNumber = lastminAttr_pt;
    }

    public Candidate clone() {
        try {
            return (Candidate) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Candidate(this.maskZeros, this.attrIndex, this.minAttrSet, this.attrNumber, this.minAttrNumber);

        }
    }
}
