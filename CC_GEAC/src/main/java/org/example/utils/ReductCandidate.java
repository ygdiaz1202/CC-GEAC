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
public class ReductCandidate {
    
    public int[] attrs;
    public int last = -1;
    private int max = 0;

    public ReductCandidate(int[] attrs, int last) {
        this.attrs = attrs;
        this.last =last;
    }

    public ReductCandidate() {
    }
    
    public void setRange(int rang) {
        this.max = rang;
        this.attrs = new int[rang];
    }

    public void push(int attrIndex) {
        if (this.last < this.max - 1) {
            this.last++;
            this.attrs[this.last] = attrIndex;
        }
    }

    public int removeLast() {
        if (this.last >= 0) {
            return this.attrs[this.last--];
        }
        return -1;
    }

    public int get(int n) {
        if (n >= 0 && n <= this.last) {
            return this.attrs[n];
        }
        return this.attrs[0];
    }

    public void cut(int n) {
        if (n >= 0 && n <= this.last) {
            this.last = n;
        }
    }

    public boolean empty() {
        return this.last == -1;
    }
}
