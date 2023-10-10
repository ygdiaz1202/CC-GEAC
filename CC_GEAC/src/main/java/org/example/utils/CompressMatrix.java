/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maximus
 */
public class CompressMatrix {

    private static int NUMB_ELEMENTS = 100;//initial numbers of rows in the matriz

    private int top = -1;
    private BinaryArray[] _m_;
    private BinaryArray64[] cols;
    private int[] core;
    private int core_size;
    private int cols_number;

    public CompressMatrix(String filename) {
        try {
            init_matrix_from_file(filename);
        } catch (IOException ex) {
            Logger.getLogger(CompressMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getTop() {
        return top;
    }

    public BinaryArray[] getM_() {
        return _m_;
    }

    public BinaryArray64[] getCols() {
        return cols;
    }

    public int getCore_size() {
        return core_size;
    }

    public int getCols_number() {
        return cols_number;
    }

    public CompressMatrix(DataSet dataset, boolean disc_m, boolean simplify) {
        init_matrix_from_dataset(dataset, disc_m, simplify);
    }

    public CompressMatrix() {
    }

    public int getRowsNumber() {
        return top + 1;
    }

    public BinaryArray[] getRows() {
        return _m_;
    }

    public void printM() {
        if (isEmpty()) {
            System.out.println("[]");
        }
        String str = "[\n";
        for (int i = 0; i <= top; i++) {
            str += "[" + _m_[i].getId() + "]\n";
        }
        str += "]";
        System.out.println(str);
    }

    private void init_matrix_from_file(String filename) throws IOException {
        try ( BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int rows_number = Integer.parseInt(br.readLine().trim());

            cols_number = Integer.parseInt(br.readLine().trim());
            core = new int[cols_number];
            core_size = 0;

            _m_ = new BinaryArray[rows_number];
            cols = new BinaryArray64[cols_number];
            String line = br.readLine();
            String[] colsAsStr = new String[cols_number];
            int r_id = 0;
            //initialitation of the rows of the matrix
            while (line != null) {
                String row = line.replaceAll("\\s", "");
                _m_[++top] = new BinaryArray(row, r_id++);
                if (_m_[top].esUnitario()) {
                    core[core_size++] = _m_[top].getFirstNZCol();
                }
                for (int col_id = 0; col_id < row.length(); col_id++) {
                    colsAsStr[col_id] += row.charAt(col_id);
                }
                line = br.readLine();
            }
            //initialitation of the cols of the matrix
            for (int col_id = 0; col_id < colsAsStr.length; col_id++) {
                cols[col_id] = new BinaryArray64(colsAsStr[col_id], col_id);
            }
        }
    }

    public void remove(Set<Integer> indx_to_remove) {
        if (!indx_to_remove.isEmpty()) {
            if (indx_to_remove.size() - 1 == top) {
                removeAll();
            } else {
                int[] arr = indx_to_remove.stream()
                        .mapToInt(Integer::intValue)
                        .toArray();
                for (int index : arr) {
                    if (_m_[top] == _m_[index]) {
                        top--;
                    } else {
                        BinaryArray tmp = _m_[top];
                        _m_[top--] = _m_[index];
                        _m_[index] = tmp;
                    }
                }
            }
        }
    }

    public void remove(int indx_to_remove) {
        if (top == 0) {
            removeAll();
        } else {
            if (_m_[top] == _m_[indx_to_remove]) {
                top--;
            } else {
                BinaryArray tmp = _m_[top];
                _m_[top--] = _m_[indx_to_remove];
                _m_[indx_to_remove] = tmp;
            }

        }
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public boolean isFull() {
        return top == _m_.length - 1;
    }

    private void removeAll() {
        top = -1;
    }

    public int size() {
        return top + 1;
    }

    public void add(BinaryArray r) {
        if (isEmpty()) {
            _m_ = new BinaryArray[NUMB_ELEMENTS];
            _m_[++top] = r;
            return;
        }
        if (isFull()) {
            BinaryArray[] arr = new BinaryArray[_m_.length + NUMB_ELEMENTS];
            System.arraycopy(_m_, 0, arr, 0, _m_.length);
            arr[++top] = r;
            _m_ = arr;
        } else {
            _m_[++top] = r;
        }
    }

    /**
     *
     * @param r the row to add into _m_
     * @code only add r if r no is super-row of any row in _m_
     * remove repeated rows and super rows of _m_
     */
    public void addBasicRow(BinaryArray r) {
        if (isEmpty()) {
            _m_ = new BinaryArray[NUMB_ELEMENTS];
            _m_[++top] = r;
            return;
        }
        if (is_basic_row(r)) {
            if (isFull()) {
                BinaryArray[] arr = new BinaryArray[_m_.length + NUMB_ELEMENTS];
                System.arraycopy(_m_, 0, arr, 0, _m_.length);
                arr[++top] = r;
                _m_ = arr;
            } else {
                _m_[++top] = r;
            }
        }
    }

    public void addFirst(BinaryArray r) {
        if (isEmpty()) {
            _m_ = new BinaryArray[NUMB_ELEMENTS];
            _m_[++top] = r;
            return;
        }
        BinaryArray[] arr;
        if (isFull()) {
            arr = new BinaryArray[_m_.length + NUMB_ELEMENTS];
            arr[0] = r;
            System.arraycopy(_m_, 0, arr, 1, _m_.length);
            _m_ = arr;
        } else {
            arr = new BinaryArray[_m_.length + 1];
            arr[0] = r;
            System.arraycopy(_m_, 0, arr, 1, _m_.length);
        }
        top++;
        _m_ = arr;
    }

    public void write_matrix(FileWriter fw) {
        try {
            for (int i = 0; i <= top; i++) {
                fw.write(_m_[i].getId() + "\n");
            }
        } catch (IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    private void init_matrix_from_dataset(DataSet dataset, boolean disc, boolean simplify) {
        cols_number = dataset.getAttrs() - 1;
        if (disc) {
            genDiscMatrix(dataset, simplify);
        } else {
            genIdiscMatrix(dataset, simplify);
        }
    }

    private void genDiscMatrix(DataSet dataset, boolean simplify) {
        int[] inst_p_class = dataset.getNumbInstancesPerClass();
        int last_intance = 0;
        int last_index = inst_p_class.length - 1;
//        System.out.println(last_index);
        int last_obj_to_compare = dataset.getInstances() - inst_p_class[last_index];
        int r_id = 0;
        for (int i = 0; i < inst_p_class.length; i++) {
            int intances_in_curr_class = inst_p_class[i];
            int first = last_intance;
            last_intance = first + intances_in_curr_class;
            if (first == last_obj_to_compare) {
                return;
            }
            for (int j = first; j < last_intance; j++) {
                Row r1 = dataset.getRows()[j];
                for (int k = last_intance; k < dataset.getInstances(); k++) {
                    Row r2 = dataset.getRows()[k];
                    String result = r1.disc(r2);
                    if (result != null) {
                        BinaryArray r_c = new BinaryArray(result, r_id++);
                        if (simplify) {
                            addBasicRow(r_c);
                        } else {
                            add(r_c);
                        }
                    }
                }
            }

        }
    }

    private void genIdiscMatrix(DataSet dataset, boolean simplify) {
        int[] inst_p_class = dataset.getNumbInstancesPerClass();
        int last_index = inst_p_class.length - 1;
//        System.out.println(last_index);
        int last_obj_to_compare = dataset.getInstances() - inst_p_class[last_index];
        int r_id = 0;
        int first_intance;
        int last_intance = 0;
        for (int i = 0; i < inst_p_class.length; i++) {
            int intances_in_curr_class = inst_p_class[i];
            first_intance = last_intance;
            last_intance = first_intance + intances_in_curr_class;
            for (int j = first_intance; j < last_intance; j++) {
                Row r1 = dataset.getRows()[j];
                for (int k = j + 1; k < last_intance; k++) {
                    Row r2 = dataset.getRows()[k];
                    String result = r1.indisc(r2);
                    if (result != null) {
                        BinaryArray r_c = new BinaryArray(result, r_id++);
                        if (simplify) {
                            addBasicRow(r_c);
                        } else {
                            add(r_c);
                        }
                    }
                }
            }

        }
    }

    public void simplify_matrix() {
        Set<Integer> set_rows_to_eliminate = new HashSet<>();
        int i = 0;
        while (i <= top) {
            boolean is_super_row = false;
            BinaryArray r1 = _m_[i];
            Set<Integer> set_rows = new HashSet<>();
            for (int j = i + 1; j <= top; j++) {
                BinaryArray r2 = _m_[j];
                int relation = r1.compareTo(r2);
                // relation=0 r1 subrow r2, relation=1 r2 sub row of r1 and relation=-1 diferent
                switch (relation) {
                    case 0 -> {
                        set_rows.add(j);
                    }
                    case 1 -> {
                        set_rows_to_eliminate.add(i);
                    }
                }
            }
            remove(set_rows);
            ++i;
        }
        remove(set_rows_to_eliminate);
    }

    /**
     *
     * @param r1 the row to check if is basic in _m_
     * @return true if r1 is basic row and remove from _m_ the super rows of r1
     */
    public boolean is_basic_row(BinaryArray r1) {
        Set<Integer> set_rows = new HashSet<>();
        for (int j = 0; j <= top; j++) {
            BinaryArray r2 = _m_[j];
            int relation = r1.compareTo(r2);
            // relation=0 r1 subrow r2, relation=1 r2 sub row of r1 and relation=-1 diferent
            switch (relation) {
                case 0 -> {
                    set_rows.add(j);
                }
                case 1 -> {
                    return false;
                }
            }
        }
        remove(set_rows);
        return true;
    }

    public void combine_matriz(CompressMatrix disc_m, CompressMatrix middle_m) {
        Set<Integer> set_rows_to_eliminate_m1 = new HashSet<>();
        Set<Integer> set_rows_to_eliminate_m2 = new HashSet<>();
        Set<Integer> set_sub_rows_m1 = new HashSet<>();
        Set<Integer> set_sub_rows_m2 = new HashSet<>();
        for (int i = 0; i < this.size(); i++) {
            BinaryArray indisc_m1 = this._m_[i];
            boolean is_super_row = false;
            boolean is_sub_row = false;
            for (int j = 0; j < disc_m.size(); j++) {
                BinaryArray r2 = disc_m.getRows()[j];
                int comparation = indisc_m1.compareTo(r2);
                switch (comparation) {
                    case 0 -> {
                        set_rows_to_eliminate_m2.add(j);
                        if (!is_super_row) {
                            set_sub_rows_m1.add(i);
                            is_super_row = true;
                        }
                    }
                    case 1 -> {
                        set_sub_rows_m2.add(j);
                        if (!is_sub_row) {
                            set_rows_to_eliminate_m1.add(i);
                            is_sub_row = true;
                        }
                    }
                }
            }
            disc_m.remove(set_rows_to_eliminate_m2);
            set_rows_to_eliminate_m2 = new HashSet<>();
        }
        if (middle_m != null) {
            for (Integer index : set_sub_rows_m2) {
                middle_m.add(disc_m.getRows()[index]);
                set_rows_to_eliminate_m2.add(index);
            }
            set_sub_rows_m1.forEach(index -> {
                middle_m.addFirst(this._m_[index]);
                set_rows_to_eliminate_m1.add(index);
            });
        }
        this.remove(set_rows_to_eliminate_m1);
        disc_m.remove(set_rows_to_eliminate_m2);
    }

    public void printCore() {
        System.out.print("[");
        if (core_size > 0) {
            for (int i = 0; i < core_size - 1; i++) {
                System.out.print(core[i] + ",");
            }
            System.out.print(core[core_size - 1]);
        }
        System.out.print("]");
    }
}
