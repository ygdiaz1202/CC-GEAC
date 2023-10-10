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
public class CandGeneration {

    public int lastCol;
    public ReductCandidate CurrentCandidate;
    public int nextX; //Index of the attribute being processed 
    public int currentX;//Index of the last attribute in the current candidate to reduct
    int attr;

    public CandGeneration(int fstRowOnes, int attr) {
        this.lastCol = fstRowOnes;
        this.CurrentCandidate = new ReductCandidate();
        this.CurrentCandidate.setRange(attr);
        this.CurrentCandidate.push(0);
        this.currentX = -1;
        this.nextX = 0;
        this.attr = attr;
    }

    public boolean genNextAttrSubset(boolean super_reduct, boolean Contributes, boolean reduct, boolean hasFuture) {
        if (this.CurrentCandidate.attrs[this.CurrentCandidate.last] == this.attr - 1) {
            if (reduct || !super_reduct) {
                int last = this.CurrentCandidate.removeLast();
                if (last <= 0) {
                    return false;
                }
                while (this.CurrentCandidate.attrs[this.CurrentCandidate.last] == last - 1) {
                    last = this.CurrentCandidate.removeLast();
                    if (last <= 0) {
                        return false;
                    }
                    if (this.CurrentCandidate.last == 0) {
                        break;
                    }
                }
            } else {
                this.CurrentCandidate.removeLast();
            }

            this.nextX = this.CurrentCandidate.removeLast() + 1;

            if (this.CurrentCandidate.last == -1) {
                this.currentX = -1;
            } else {
                this.currentX = this.CurrentCandidate.attrs[this.CurrentCandidate.last];
            }

            this.CurrentCandidate.push(this.nextX);
        } else {
            if (hasFuture) {
                if (!Contributes || super_reduct) {
                    this.nextX = this.CurrentCandidate.removeLast() + 1;
                    this.currentX = (this.CurrentCandidate.last == -1) ? -1 : this.CurrentCandidate.attrs[this.CurrentCandidate.last];
                    this.CurrentCandidate.push(this.nextX);
                }
                if (Contributes && !super_reduct) {
                    this.currentX = this.nextX;

                    this.CurrentCandidate.push(++this.nextX);
                }
            } else {
                this.CurrentCandidate.removeLast();
                this.nextX = this.CurrentCandidate.removeLast() + 1;
                this.currentX = (this.CurrentCandidate.last == -1) ? -1 : this.CurrentCandidate.attrs[this.CurrentCandidate.last];
                this.CurrentCandidate.push(this.nextX);
            }
        }

        return !(this.CurrentCandidate.attrs[0] >= this.lastCol);
    }


 //
//    public boolean getCurrentCandidate(boolean super_reduct, boolean Contributes) {
//        if (this.CurrentCandidate.attrs[this.CurrentCandidate.last] == this.attr - 1) {
//            this.CurrentCandidate.removeLast();
//
//            this.nextX = this.CurrentCandidate.removeLast() + 1;
//
//            if (this.CurrentCandidate.last == -1) {
//                this.currentX = -1;
//            } else {
//                this.currentX = this.CurrentCandidate.attrs[this.CurrentCandidate.last];
//            }
//
//            this.CurrentCandidate.push(this.nextX);
//        } else {
//
//            if (!Contributes || super_reduct) {
//
//                this.nextX = this.CurrentCandidate.removeLast() + 1;
//                this.currentX = (this.CurrentCandidate.last == -1) ? -1 : this.CurrentCandidate.attrs[this.CurrentCandidate.last];
//                this.CurrentCandidate.push(this.nextX);
//            }
//
//            if (Contributes && !super_reduct) {
//                this.currentX = this.nextX;
//
//                this.CurrentCandidate.push(++this.nextX);
//            }
//        }
//
//        return !(this.CurrentCandidate.attrs[0] == this.lastCol);
//    }

    public String toString() {
        StringBuilder candidate = new StringBuilder("$");
        for (int i = 0; i <= this.CurrentCandidate.last; i++) {
            candidate.append("c_").append(this.CurrentCandidate.attrs[i]).append(",");
        }
        return candidate.substring(0, candidate.length() - 1) + "$";
    }

}
