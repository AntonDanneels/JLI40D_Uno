package be.kuleuven.cs.jli40d.core.database;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class provides an endpoint for application servers to connect to
 * database servers and manage game functions.
 *
 * @author Pieter
 * @version 1.0
 */
public interface DatabaseGameHandler extends Remote, Serializable
{

    /**
     * Registers an application server to the database.
     *
     * @return An int with the application server id.
     * @throws RemoteException
     */
    int registerServer() throws RemoteException;

    /**
     * Obtains a list with all the games as a {@link GameSummary} list.
     *
     * @return A {@link List} with {@link GameSummary} objects.
     * @throws RemoteException
     */
    List<GameSummary> getGames() throws RemoteException;

    Game getGame( int serverID, int gameID ) throws RemoteException;

    Game saveGame( int serverID, Game game ) throws RemoteException;

    int addMove( int serverID, int gameID, GameMove gameMove) throws RemoteException;

}
