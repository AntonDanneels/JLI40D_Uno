package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.*;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * All methods expect a token to identify and authenticate the user.
 */
public interface GameHandler extends Remote, Serializable
{
    /**
     * Returns if a game is started (can also be finished) or not.
     * <p>
     * This is not a blocking call.
     *
     * @param token    The token given to the user for authentication.
     * @param gameUuid The uuid of the game to join.
     * @return False if the game is not yet started, true otherwise.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    boolean isStarted( String token, String gameUuid ) throws InvalidTokenException, RemoteException, GameNotFoundException, WrongServerException;

    /**
     * Returns true if it's the server determines the players (identified
     * by the provided token) turn.
     * <p>
     * This method is not blocking.
     *
     * @param token    The token given to the user for authentication.
     * @param gameUuid The uuid of the game to join.
     * @return True if the player that invoked the function has its turn.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    boolean myTurn( String token, String gameUuid ) throws InvalidTokenException, RemoteException, GameNotFoundException, WrongServerException;


    /**
     * {@link GameMove} objects are how we transport updates to a game
     * to all listening/participating users.
     * <p>
     * This call is blocking, meaning it will not provide a new GameMove until one
     * is ready.
     *
     * @param token          The token given to the user for authentication.
     * @param gameUuid       The uuid of the game to join.
     * @param nextGameMoveID The id of the next gameMove for a certain game.
     * @return The next GameMove when one is ready.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    GameMove getNextMove( String token, String gameUuid, int nextGameMoveID ) throws InvalidTokenException, RemoteException, GameNotFoundException, GameEndedException, WrongServerException;

    /**
     * Send a {@link GameMove} object to update the state of a certain game.
     * <p>
     * This method also checks if the player was authorised and it was his/her
     * turn to make a move.
     *
     * @param token    The token given to the user for authentication.
     * @param gameUuid The uuid of the game to join.
     * @throws InvalidTokenException
     * @throws RemoteException
     * @throws GameNotFoundException    When the game is not found.
     * @throws InvalidGameMoveException When the move is invalid.
     */
    void sendMove( String token, String gameUuid, GameMove move ) throws InvalidTokenException, RemoteException, GameNotFoundException, InvalidGameMoveException, WrongServerException;
}
