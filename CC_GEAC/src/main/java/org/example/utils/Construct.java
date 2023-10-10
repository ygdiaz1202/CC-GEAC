package org.example.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Construct implements Comparable {
    private int[] values;

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public Construct(int[] values) {
        this.values = values;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (this == o) return 0;
        if (!(o instanceof Construct)) {
            return -1;
        }
        Construct construct = (Construct) o;
        return Arrays.compare(construct.values,this.values);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Construct)) return false;
        Construct construct = (Construct) o;
        return Arrays.compare(construct.values,this.values)==0;
    }

    @Override
    public String toString() {
        String str = "{";
        for (int i = 0; i < values.length - 1; i++) {
            str += "x" + (values[i] + 1) + ", ";
        }
        str += "x" + (values[values.length - 1]+1) + "}";
        return str;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getValues());
    }
}
