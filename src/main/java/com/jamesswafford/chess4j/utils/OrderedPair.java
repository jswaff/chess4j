package com.jamesswafford.chess4j.utils;

import java.io.Serializable;

public class OrderedPair<T1,T2> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3572202581972146545L;
    private T1 e1;
    private T2 e2;

    public OrderedPair(T1 e1,T2 e2) {
        this.e1 = e1;
        this.e2 = e2;
    }

    public T1 getE1() {
        return e1;
    }

    public T2 getE2() {
        return e2;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof OrderedPair)) return false;
        @SuppressWarnings("rawtypes")
        OrderedPair op = (OrderedPair)obj;

        if (this.e1==null && op.e1!=null) return false;
        if (!this.e1.equals(op.e1)) return false;
        if (this.e2==null && op.e2!=null) return false;
        if (!this.e2.equals(op.e2)) return false;

        return true;
    }

    public int hashCode() {
        int hc = 1;

        hc *= 17 + (e1==null?0:e1.hashCode());
        hc *= 31 + (e2==null?0:e2.hashCode());

        return hc;
    }

    public String toString() {
        return "OrderedPair [e1=" + e1 + ", e2=" + e2 + "]";
    }

}
