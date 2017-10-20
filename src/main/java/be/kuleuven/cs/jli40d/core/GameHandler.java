package be.kuleuven.cs.jli40d.core;

import java.io.Serializable;

import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.GameMove;

/**
 * Created by Anton D. on 19/10/2017 using IntelliJ IDEA 14.0
 */
public interface GameHandler extends Serializable
{
    boolean isStarted();
    GameMove getNextMove();
    void sendMove( GameMove move );
}
