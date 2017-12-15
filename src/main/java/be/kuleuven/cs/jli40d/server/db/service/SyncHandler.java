package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;

import java.rmi.RemoteException;

/**
 * This is an interface for the database servers, providing some endpoints other
 * databases can use to add various objects to the implementing database context.
 *
 * @author Pieter
 * @version 0.1
 */
public interface SyncHandler
{

    /**
     * Propagates a {@link Game} object to other databases.
     *
     * @param sender The id of the sender.
     * @param game   The {@link Game} object.
     * @throws RemoteException
     */
    void propagateGame( int sender, Game game ) throws RemoteException;

    /**
     * Propagates a {@link GameMove} object to other databases.
     *
     * @param sender   The id of the sender.
     * @param gameUuid The uuid of the game.
     * @param move     The {@link GameMove} object.
     * @throws RemoteException
     */
    void propagateMove( int sender, String gameUuid, GameMove move ) throws RemoteException;

    /**
     * Propagates a {@link Player} object to other databases.
     *
     * @param sender   The id of the sender.
     * @param gameUuid The uuid of the game.
     * @param game     The {@link Game} object.
     * @throws RemoteException
     */
    void propagatePlayer( int sender, String gameUuid, Player game ) throws RemoteException;
}
