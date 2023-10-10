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
public class Row implements Comparable<Row> {

    private Object[] data;
    private String class_name;

    public String getClassName() {
        return class_name;
    }

    public void print() {
        for (Object data1 : data) {
            if (data1 != null) {
                System.out.print(data1 + ", ");
            } else {
                System.out.print("?, ");
            }
        }
        System.out.print(class_name);
        System.out.println("");
    }

    public void parse(String row) {
        String[] r = row.split(",");
        data = new Object[r.length-1];

        if (Column.CLASS_INDEX != r.length - 1) {
            String tmp = r[r.length - 1];
            r[r.length - 1] = r[Column.CLASS_INDEX];
            r[Column.CLASS_INDEX] = tmp;
            Column.CLASS_INDEX = r.length - 1;

            Column col_tmp = DataSet.columns[DataSet.columns.length - 1];
            DataSet.columns[DataSet.columns.length - 1] = DataSet.columns[Column.CLASS_INDEX];
            DataSet.columns[Column.CLASS_INDEX] = col_tmp;
        }

        for (int col = 0; col < r.length-1; col++) {
            data[col] = DataSet.columns[col].convert(r[col]);
        }
        class_name = r[r.length - 1].replace("\r", "");

    }

    public String disc(Row other) {
        String comparation = " ";
        boolean all_zero = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null || other.data[i] == null) {
                comparation += "1 ";
                all_zero = false;
            } else {
                if (data[i].equals(other.data[i])) {
                    comparation += "0 ";
                } else {
                    comparation += "1 ";
                    all_zero = false;
                }
            }
        }
        if (all_zero) {
            return null;
        }
        return comparation;
    }

    public String indisc(Row other) {
        String comparation = " ";
        boolean all_zero = true;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == null || other.data[i] == null) {
                comparation += "0 ";
            } else {
                if (data[i].equals(other.data[i])) {
                    comparation += "1 ";
                    all_zero = false;
                } else {
                    comparation += "0 ";
                }
            }
        }
        if (all_zero) {
            return null;
        }
        return comparation;
    }

    public boolean equals(Row other) {
        return class_name.equals(other.class_name);
    }

    @Override
    public int compareTo(Row o) {
        return class_name.compareTo(o.class_name);
    }
}
