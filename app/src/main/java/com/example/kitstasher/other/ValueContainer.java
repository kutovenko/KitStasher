package com.example.kitstasher.other;

/**
 * Created by Алексей on 16.05.2017.
 */

public class ValueContainer<T> {
    private T val;

    public ValueContainer() {
    }

    public ValueContainer(T v) {
        this.val = v;
    }

    public T getVal() {
        return val;
    }

    public void setVal(T val) {
        this.val = val;
    }
}
