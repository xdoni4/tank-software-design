package ru.mipt.bit.platformer.classes;

public class ExecutionSuppressor {
    public int suppressed = 1;

    public void update(int newv) {
        suppressed = suppressed ^ newv;
    }
}