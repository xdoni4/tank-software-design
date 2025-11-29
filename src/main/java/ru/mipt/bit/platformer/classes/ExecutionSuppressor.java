package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.GameObject;

public class ExecutionSuppressor extends GameObject {
    public int suppressed = 1;
    public int updateCoolDownMax = 75;
    public int updateCoolDown = 0;

    public void update(int newv) {
        suppressed = suppressed ^ newv;
    }

    @Override
    public void tick() {
        if (updateCoolDown > 0) {
            updateCoolDown--;
        }
    }
}