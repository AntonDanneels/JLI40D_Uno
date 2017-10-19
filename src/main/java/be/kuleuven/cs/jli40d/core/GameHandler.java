package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Card;

/**
 * Created by Anton D. on 19/10/2017 using IntelliJ IDEA 14.0
 */
public interface GameHandler
{
    boolean isStarted();
    Card getNextMove();
    void sendMove();
}
