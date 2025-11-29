package ru.mipt.bit.platformer.classes;

public interface HasHealth {
    public float getHealth();
    public float getMaxHealth();
    public void updateHealth(float diff);
}
