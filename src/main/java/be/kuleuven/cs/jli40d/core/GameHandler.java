package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.GameMove;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * All methods expect a token to identify and authenticate the user.
 */
public interface GameHandler extends Remote
{
    boolean isStarted( String token ) throws RemoteException;

    boolean myTurn( String token ) throws RemoteException;

    GameMove getNextMove( String token, int id ) throws RemoteException;

    void sendMove( String token, GameMove move ) throws RemoteException;
}
