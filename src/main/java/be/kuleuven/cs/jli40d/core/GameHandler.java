package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.GameMove;

import java.rmi.Remote;

/**
 * All methods expect a token to identify and authenticate the user.
 */
public interface GameHandler extends Remote
{
    boolean isStarted( String token );

    boolean myTurn( String token );

    GameMove getNextMove( String token, int ID );

    void sendMove( String token, GameMove move );
}
