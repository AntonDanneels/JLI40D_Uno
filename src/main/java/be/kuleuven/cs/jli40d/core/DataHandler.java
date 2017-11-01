package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class provides an endpoint for application servers to connect to
 * database servers.
 *
 * @author Pieter
 * @version 1.0
 */
public interface DataHandler extends Remote, Serializable
{
    List<Game> getGames() throws RemoteException;

    Game getGame( long id ) throws RemoteException;

    void addMove( long gameID, GameMove gameMove) throws RemoteException;

    boolean registerUser() throws RemoteException;

}
