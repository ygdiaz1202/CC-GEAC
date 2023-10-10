/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.example.utils;

import java.util.Date;
/**
 *
 * @author Maximus
 */
public class Column {

    private String name;
    private int index;
    private DataSet.DataType type;
    private double epsilon;
    public static int CLASS_INDEX;

    public Column() {
        this.type = DataSet.DataType.NONE;
    }

    public Object convert(String data) {
        if (data.equals("?")) {
            return null;
        }
        if (type == DataSet.DataType.Integer) {
            return Integer.parseInt(data);
        }
        if (type == DataSet.DataType.Double) {
            return Double.parseDouble(data);
        }
        if (type == DataSet.DataType.Date) {
            return Date.parse(data);
        } else {
            return data;
        }
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public DataSet.DataType getType() {
        return type;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setType(DataSet.DataType type) {
        this.type = type;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

}
