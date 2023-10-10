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
public class BinaryArray64 {

    private int idArray;
    private long[] array;

    private int first_one;
    private int numbOnes;

    private static long[] ONES;

    private static int numUnidades;
    private static int restBits;

    private static long mask1;
    private static long mask2;
    private static int numbBits;

    private static boolean initialized = false;
    private boolean onlyOnes = false;

    public BinaryArray64(String binaryArray, int idArray) {
        if (!initialized) {
            init(binaryArray.length(), idArray);
            initialized = true;
        }
        setValues(binaryArray);
    }

    public BinaryArray64(int numbBits, int idArray) {
        if (!initialized) {
            init(numbBits, idArray);
        }
        initArray();
    }

    protected void initOnes() {
        String binary_number = "";
        ONES=new long[64];
        for (int i = 0; i < 64; i++) {
            binary_number += "1";
            long decimal_number = Long.parseUnsignedLong(binary_number, 2);
            ONES[i] = decimal_number;
        }
    }

    public int compareTo(BinaryArray64 r) {
        if (array != null) {
            return compare(r);
        } else {
            System.out.println(System.err);
            System.exit(1);
        }
        return -1;
    }

    private int compare(BinaryArray64 r) {
        if (array == null || r.array == null) {
            System.out.println("Error null array");
            return -1;
        }
        if (array.length != r.array.length) {
            return -1;
        } else {
            long entry = array[0];
            long bit_compa = (entry & r.array[0]);
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
                bit_compa = (int) (entry & r.array[0]);
                if (bit_compa == entry) {
                    if (result != 0) {
                        return -1;
                    } else if (bit_compa == r.array[0]) {
                        if (result != 1) {
                            return -1;
                        } else {
                            return -1;
                        }
                    }
                }
            }
            return result;
        }
    }

    private void initArray() {
        numbOnes = 0;
        first_one = -1;
        this.array = new long[numUnidades];
    }

    public void setBinaryArray(BinaryArray64 new_array) {
        for (short i = 0; i < numUnidades; i++) {
            this.array[i] = new_array.array[i];
        }
    }

    // Function to calculate the
    // log base 2 of an long
    private static int log2(long N) {
        // calculate log2 N indirectly
        // using log() method
        int result = (int) (Math.log(N) / Math.log(2));
        return result;
    }

    public boolean zero() {
        return numbOnes == 0;
    }

    public boolean onlyOnes(int numbBits) {
        return numbOnes == numbBits;
    }

    public void setValue(int pos, int valor) {
        long var = 1;
        int k = pos / 64;
        int rk = pos % 64;
        if (valor != 0) {
            array[k] = array[k] | var << rk;
        } else {
            array[k] = array[k] & (var << rk^ 0xFFFFFFFF);
        }
    }

    public void setValues(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '1') {
                setValue(i, 1);
                numbOnes++;
                if (first_one == -1) {
                    first_one = i;
                }
            }
        }
    }

    private void init(int numb, int idArray) {
//        String curren_row = binaryArray.replaceAll("\\s", "");
        initialized = true;
        this.idArray = idArray;
        numbBits = numb;
        numUnidades = (numbBits + 64 - 1) / 64;
        restBits = numbBits % 64;

        initOnes();
        mask1 = -1;
        mask2 = (restBits == 0) ? -1 : ONES[restBits - 1];
    }

    public boolean andNEqZ(BinaryArray64 operando) {
        for (int i = 0; i < numUnidades; i++) {
            long k = this.array[i] & operando.array[i];
            if (k != 0) {
                return false;
            }
        }
        return true;
    }

    public boolean and(BinaryArray64 operando) {
        for (int i = 0; i < numUnidades; i++) {
            long k = this.array[i] & operando.array[i];
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

    public int operatorAnd(BinaryArray64 operando) {
        int cont = 0;
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = this.array[i] & operando.array[i];
            cont += this.array[i];
        }
        return cont;
    }

    public boolean mascAcep(BinaryArray64 operando1, BinaryArray64 operando2) {
        boolean band = true;
        boolean band2 = true;
        long temp = mask1;
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

    public static void masks(BinaryArray64 MA, BinaryArray64 MC, BinaryArray64 att) {
        for (int i = 0; i < numUnidades; i++) {
            MC.array[i] = MC.array[i] & (att.array[i]^ 0xFFFFFFFF) | (MA.array[i]^ 0xFFFFFFFF) & att.array[i];
            MA.array[i] = MA.array[i] | att.array[i];
        }
    }

    public boolean esUnitario() {
        return this.onlyOnes;
    }

    public void mascComp(BinaryArray64 mascara, BinaryArray64 x, BinaryArray64 mascaraAcep) {
        for (int i = 0; i < numUnidades; i++) {
            this.array[i] = mascara.array[i] & (x.array[i]^ 0xFFFFFFFF) | (mascaraAcep.array[i]^ 0xFFFFFFFF) & x.array[i];
        }
    }

    public void setArray(BinaryArray64 operando) {
        for (short i = 0; i < numUnidades; i++) {
            this.array[i] = operando.array[i];
        }
        this.idArray = operando.idArray;
    }

    public boolean subArray(BinaryArray64 operando) {
        for (short i = 0; i < numUnidades; i++) {
            if (array[i] != (array[i] & operando.array[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean gq(BinaryArray64 operando, int opc) {
        if (opc == 2) {
            return (cantValoresUnitarios() > operando.cantValoresUnitarios());
        }
        for (int i = numUnidades - 1; i >= 0; i--) {
            if (this.array[i] != operando.array[i]) {
                return this.array[i] > operando.array[i];
            }
        }
        return false;
    }

    public boolean lq(BinaryArray64 operando, int opc) {
        if (opc == 2) {
            return (cantValoresUnitarios() < operando.cantValoresUnitarios());
        }
        for (int i = numUnidades - 1; i >= 0; i--) {
            if (this.array[i] != operando.array[i]) {
                return this.array[i] < operando.array[i];
            }
        }
        return false;
    }

    public boolean eq(BinaryArray64 operando) {
        for (int i = 0; i < numUnidades; i++) {
            if (this.array[i] != operando.array[i]) {
                return false;
            }
        }
        return true;
    }

    public int cantValoresUnitarios() {
        int contador = 0;
        for (int i = 0; i < numbBits; i++) {
            if (getValue(i) == 1) {
                contador++;
            }
        }
        return contador;
    }

    public int cantValoresUnitarios(int pos) {
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

    public long getValue(int posicion) {
        int k = posicion / 64;
        int rk = posicion % 64;
        long t = this.array[k] & 1 << rk;
        t = (t != 0) ? 1 : 0;
        return t;
    }

    @Override
    public String toString() {
        String str = idArray + ": {";
        for (int i = 0; i < numbBits - 1; i++) {
            str += getValue(i) + " ";
        }
        str += getValue(numbBits - 1);
        str += "}";
        return str;
    }
}
