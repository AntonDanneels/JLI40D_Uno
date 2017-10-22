package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;

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
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @return False if the game is not yet started, true otherwise.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    boolean isStarted( String token, int gameID ) throws InvalidTokenException, RemoteException, GameNotFoundException;

    /**
     * Returns true if it's the server determines the players (identified
     * by the provided token) turn.
     *
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @return True if the player that invoked the function has its turn.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     */
    boolean myTurn( String token, int gameID ) throws InvalidTokenException, RemoteException, GameNotFoundException;


    /**
     * {@link GameMove} objects are how we transport updates to a game
     * to all listening/participating users.
     * <p>
     * This call is blocking, meaning it will not provide a new GameMove until one
     * is ready.
     *
     * @param token          The token given to the user for authentication.
     * @param gameID         The id of the game.
     * @param nextGameMoveID The id of the next gameMove for a certain game.
     * @return The next GameMove when one is ready.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    GameMove getNextMove( String token, int gameID, int nextGameMoveID ) throws InvalidTokenException, RemoteException, GameNotFoundException;

    /**
     * Send a {@link GameMove} object to update the state of a certain game.
     *
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @param move   The {@link GameMove}.
     * @throws InvalidTokenException
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    void sendMove( String token, int gameID, GameMove move ) throws InvalidTokenException, RemoteException, GameNotFoundException;
}
