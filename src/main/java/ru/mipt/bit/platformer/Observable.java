package ru.mipt.bit.platformer;


public interface Observable {
    public String subscribe(Observer observer);
    public void unsubscribe(String key);
    public void notifyObservers();
}
