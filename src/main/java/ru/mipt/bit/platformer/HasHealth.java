package ru.mipt.bit.platformer;

public interface HasHealth {
    public float getHealth();
    public float getMaxHealth();
    public void updateHealth(float diff);
}
