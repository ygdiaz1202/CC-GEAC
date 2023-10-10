/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.utils;

/**
 *
 * @author Maximus
 */
public class BinaryArray implements Comparable<BinaryArray> {

    private int idArray;
    private int[] array;

    private int first_one;
    private int numbOnes;

    private static final int[] ONES
            = new int[]{1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047,
                4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575,
                2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727,
                268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1
            };

    private static int numUnidades;
    private static int restBits;

    private static int mask1;
    private static int mask2;
    private static int numbBits;

    private boolean onlyOnes = false;
    public static boolean compareByNumbOnes = true;

    public BinaryArray(String binaryArray, int idArray) {
        initArray(idArray);
        setValues(binaryArray, idArray);
    }

    public BinaryArray(int numbBits, int idArray) {
        initArray(idArray);
    }

    public BinaryArray(int[] array, int idArray) {
        this.array = array.clone();
        this.idArray = idArray;
    }
//    private static void initOnes() {
//        String binary_number = "";
//        ONES = new int[32];
//        for (int i = 0; i < 32; i++) {
//            binary_number += "1";
//            int decimal_number = Integer.parseUnsignedInt(binary_number, 2);
//            ONES[i] = decimal_number;
//        }
//    }

    public int compareTo(BinaryArray r) {
        if (compareByNumbOnes) {
            return compareByNumbOnes(r);
        } else {
            return compare(r);
        }
    }

    private int compare(BinaryArray r) {
        int entry = array[0];
        int bit_compa = (entry & r.array[0]);
        int result;
        if (bit_compa == entry) {
            result = 0;
        } else if (bit_compa == r.array[0]) {
            result = 1;
        } else {
            return -1;
        }
        for (int i = 1; i < array.length; i++) {
            entry = array[i];
            bit_compa = (int) (entry & r.array[i]);
            if (result == 0) {
                if (bit_compa != entry) {
                    return -1;
                }
            } else {
                if (bit_compa != r.array[i]) {
                    return -1;
                }
            }
        }
        return result;
    }

    private int compareByNumbOnes(BinaryArray op) {
        if (numbOnes == op.numbOnes) {
            return 0;
        } else {
            return (numbOnes > op.numbOnes) ? 1 : -1;
        }
    }

    private void initArray(int idArray) {
        numbOnes = 0;
        first_one = -1;
        this.idArray = idArray;
        this.array = new int[numUnidades];
    }

    public void setBinaryArray(BinaryArray new_array) {
        for (short i = 0; i < numUnidades; i = (short) (i + 1)) {
            this.array[i] = new_array.array[i];
        }
    }

//    // Function to calculate the
//    // log base 2 of an integer
//    private static int log2(int N) {
//        // calculate log2 N indirectly
//        // using log() method
//        int result = (int) (Math.log(N) / Math.log(2));
//        return result;
//    }
    public boolean zero() {
        return numbOnes == 0;
    }

    public boolean onlyOnes(int numbBits) {
        return numbOnes == numbBits;
    }

    public void setValue(int pos, int valor) {
        int var = 1;
        int k = pos / 32;
        int rk = pos % 32;
        if (valor != 0) {
            array[k] = array[k] | var << rk;
        } else {
            array[k] = array[k] & (var << rk ^ 0xFFFFFFFF);
        }
    }

    private void setValues(String line, int idArray) {
        String[] temp = line.trim().replace("\t", " ").split(" ");
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].charAt(0) == '1') {
                setValue(i, 1);
                numbOnes++;
                if (first_one == -1) {
                    first_one = i;
                }
            }
        }
        this.idArray = idArray + 1;
    }

    public static void init(int numb) {
//        String curren_row = binaryArray.replaceAll("\\s", "");
        numbBits = numb;
        numUnidades = (numbBits + 32 - 1) / 32;
        restBits = numbBits % 32;

//        initOnes();
        mask1 = -1;//All the bists in 1
        mask2 = (restBits == 0) ? -1 : ONES[restBits - 1]; //rest of bits all in 1
    }

    public boolean andNEqZ(BinaryArray operando) {
        for (int i = 0; i < numUnidades; i++) {
            int k = this.array[i] & operando.array[i];
            if (k != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean and(BinaryArray operando) {
        for (int i = 0; i < numUnidades; i++) {
            int k = this.array[i] & operando.array[i];
            if (k != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isEmpty() {
        for (int i = 0; i < numUnidades; i++) {
            if (this.array[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public int operatorAnd(BinaryArray operando) {
        int cont = 0;
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = this.array[i] & operando.array[i];
            cont += this.array[i];
        }
        return cont;
    }

    public boolean mascAcep(BinaryArray operando1, BinaryArray operando2) {
        boolean band = true;
        boolean band2 = true;
        int temp = mask1;
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = operando1.array[i] | operando2.array[i];

            if (band && this.array[i] != operando1.array[i]) {
                band = false;
            }
            if (restBits > 0 && i == numUnidades - 1) {
                temp = mask2;
            }
            if (band2 && this.array[i] != temp) {
                band2 = false;
            }
        }
        this.onlyOnes = band2;
        return band;
    }

    public boolean operatorOr(BinaryArray operando1) {
        boolean band2 = true;
        int temp = mask1;
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = this.array[i] | operando1.array[i];
            if (restBits > 0 && i == numUnidades - 1) {
                temp = mask2;
            }
            if (band2 && this.array[i] != temp) {
                band2 = false;
            }
        }
        this.onlyOnes = band2;
        return band2;
    }

    public static void masks(BinaryArray MA, BinaryArray MC, BinaryArray att) {
        for (int i = 0; i < numUnidades; i++) {
            MC.array[i] = MC.array[i] & (att.array[i] ^ 0xFFFFFFFF) | (MA.array[i] ^ 0xFFFFFFFF) & att.array[i];
            MA.array[i] = MA.array[i] | att.array[i];
        }
    }

    public boolean esUnitario() {
        return this.onlyOnes;
    }

    public void mascComp(BinaryArray mascara, BinaryArray x, BinaryArray mascaraAcep) {
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = mascara.array[i] & (~x.array[i]) | (~mascaraAcep.array[i]) & x.array[i];
        }
    }

    public void setArray(BinaryArray operando) {
        for (short i = 0; i < numUnidades; i++) {
            this.array[i] = operando.array[i];
        }
        this.idArray = operando.idArray;
    }

    public boolean subArray(BinaryArray operando) {
        for (short i = 0; i < numUnidades; i++) {
            if (array[i] != (array[i] & operando.array[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean eq(BinaryArray operando) {
        for (int i = 0; i < numUnidades; i++) {
            if (this.array[i] != operando.array[i]) {
                return false;
            }
        }
        return true;
    }

    public int getNumbOnes() {
        int contador = 0;
        for (int i = 0; i < numbBits; i++) {
            if (getValue(i) == 1) {
                contador++;
            }
        }
        numbOnes = contador;
        return contador;
    }

    public int getNumbOnes(int pos) {
        int contador = 0;
        for (int i = pos; i < numbBits; i++) {
            if (getValue(i) == 1) {
                contador++;
            }
        }
        return contador;
    }

    public int getId() {
        return this.idArray;
    }

    public int getValue(int posicion) {
        int k = posicion / 32;
        int rk = posicion % 32;
        int t = this.array[k] & 1 << rk;
        t = (t != 0) ? 1 : 0;
        return t;
    }

    public int getFirstNZCol() {
        return first_one;
    }

    public boolean only1One() {
        return numbOnes == 1;
    }

    @Override
    public String toString() {
        String str = getId() + ": {";
        for (int i = 0; i < numbBits - 1; i++) {
            str += getValue(i) + " ";
        }
        str += getValue(numbBits - 1);
        str += "}";
        return str;
    }

    public void copy(BinaryArray r1) {
        this.array = r1.array.clone();
        this.idArray = r1.idArray;
        this.numbOnes = r1.numbOnes;
        this.onlyOnes = r1.onlyOnes;
    }

}
