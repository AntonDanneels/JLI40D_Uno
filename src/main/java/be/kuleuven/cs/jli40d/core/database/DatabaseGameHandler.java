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
     * Obtains a list with all the games as a {@link GameSummary} list.
     *
     * @return A {@link List} with {@link GameSummary} objects.
     * @throws RemoteException
     */
    List<GameSummary> getGames() throws RemoteException;

    Game getGame( int id ) throws RemoteException;

    Game saveGame( Game game ) throws RemoteException;

    int addMove( int gameID, GameMove gameMove) throws RemoteException;

}
