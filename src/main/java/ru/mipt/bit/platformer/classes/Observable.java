package ru.mipt.bit.platformer.classes;

import ru.mipt.bit.platformer.classes.Observer;

public interface Observable {
    public String subscribe(Observer observer);
    public void unsubscribe(String key);
    public void notifyObservers();
}
