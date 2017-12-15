package be.kuleuven.cs.jli40d.core.database;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;

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

    /**
     * Returns a single {@link Game} object. The ID parameter of the game will be as the application
     * server expects it to be.
     *
     * @param serverID The id provided by the server as an int.
     * @param uuid The uuid of the {@link Game} as seen by the application server.
     * @return A {@link Game} object.
     * @throws GameNotFoundException When a game is not found in the db cluster.
     * @throws RemoteException
     */
    Game getGame( int serverID, String uuid ) throws GameNotFoundException, RemoteException;

    /**
     * Save a game to the database. If it already exists, it will be updated instead.
     *
     * It's not recommended to update the {@link}
     *
     * @param serverID The id provided by the server as an int.
     * @param game The object to persist to the database cluster.
     * @throws RemoteException
     */
    void saveGame( int serverID, Game game ) throws RemoteException;

    /**
     * Add a {@link GameMove} to a {@link Game} object, specified by both the serverID
     * and the gameID.
     *
     * @param serverID The id provided by the server as an int.
     * @param gameUuid The uuid of the {@link Game} as seen by the application server.
     * @param gameMove The {@link GameMove} object to add to the {@link Game}.
     * @throws RemoteException
     */
    void addMove( int serverID,String gameUuid, GameMove gameMove ) throws RemoteException;

    /**
     * Add a list {@link GameMove}  objects to a {@link Game} object, specified by both
     * the serverID and the gameID.
     *
     * @param serverID The id provided by the server as an int.
     * @param gameUuid The uuid of the {@link Game} as seen by the application server.
     * @param gameMoves A list of {@link GameMove} objects.
     * @throws RemoteException
     */
    void addMoves( int serverID, String gameUuid, List<GameMove> gameMoves ) throws RemoteException;

    /**
     * Add a {@link Player} object to the {@link Game}.
     *
     * @param serverID
     * @param gameUuid The uuid of the {@link Game} as seen by the application server.
     * @param player
     * @throws RemoteException
     */
    void addPlayer( int serverID, String gameUuid, Player player ) throws RemoteException;
}
