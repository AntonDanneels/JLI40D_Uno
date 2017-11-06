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

    /**
     * Like {@link #getGames()}, this returns a list with {@link GameSummary} objects,
     * but removes the games hosted by the provided server.
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @return A {@link List} with {@link GameSummary} objects, filtered to remove those hosted by one server.
     * @throws RemoteException
     */
    List<GameSummary> getGames( int serverID ) throws RemoteException;

    /**
     * Returns a single {@link Game} object. The ID parameter of the game will be as the application
     * server expects it to be.
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @param gameID The id of the {@link Game} as seen by the application server.
     * @return A {@link Game} object.
     * @throws GameNotFoundException When a game is not found in the db cluster.
     * @throws RemoteException
     */
    Game getGame( int serverID, int gameID ) throws GameNotFoundException, RemoteException;

    /**
     * Save a game to the database. If it already exists, it will be updated instead.
     *
     * It's not recommended to update the {@link}
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @param game The object to persist to the database cluster.
     * @throws RemoteException
     */
    void saveGame( int serverID, Game game ) throws RemoteException;

    /**
     * Add a {@link GameMove} to a {@link Game} object, specified by both the serverID
     * and the gameID.
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @param gameID The id of the {@link Game} as seen by the application server.
     * @param gameMove The {@link GameMove} object to add to the {@link Game}.
     * @throws RemoteException
     */
    void addMove( int serverID, int gameID, GameMove gameMove ) throws RemoteException;

    /**
     * Add a list {@link GameMove}  objects to a {@link Game} object, specified by both
     * the serverID and the gameID.
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @param gameID The id of the {@link Game} as seen by the application server.
     * @param gameMoves A list of {@link GameMove} objects.
     * @throws RemoteException
     */
    void addMoves( int serverID, int gameID, List<GameMove> gameMoves ) throws RemoteException;

    /**
     * Add a {@link Player} object to the {@link Game}.
     *
     * @param serverID
     * @param gameID
     * @param player
     * @throws RemoteException
     */
    void addPlayer( int serverID, int gameID, Player player ) throws RemoteException;
}
