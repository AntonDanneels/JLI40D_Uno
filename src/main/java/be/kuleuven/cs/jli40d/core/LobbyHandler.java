package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.*;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The lobby has three main functions:
 * <ul>
 * <li>Viewing a list of all games.</li>
 * <li>Making a new game.</li>
 * <li>Joining a existing game (either as spectator or player).</li>
 * </ul>
 */
public interface LobbyHandler extends Remote, Serializable
{

    /**
     * Request a list with all {@link GameSummary} objects that are currently ongoing.
     *
     * @param token Token received by the {@link UserHandler}.
     * @return A list of all GameSummary objects.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     */
    List<GameSummary> currentGames( String token ) throws RemoteException, InvalidTokenException, WrongServerException;

    /**
     * @param token           Token received by the {@link UserHandler}.
     * @param gameName        The desired name of the game, this is displayed in the lobby.
     * @param numberOfPlayers The number of games the game should have.
     * @return The uuid of the game as a string
     * @throws InvalidTokenException       When the token is invalid (expired or not found).
     * @throws UnableToCreateGameException When the game cannot be created for some reason (like exceeded limits).
     * @throws RemoteException
     */
    String makeGame( String token, String gameName, int numberOfPlayers ) throws RemoteException, InvalidTokenException, UnableToCreateGameException, WrongServerException;

    /**
     * Join a game with an id, either provided by the {@link #makeGame} or {@link #currentGames} method.
     * <p>
     * If the game is full, a {@link GameFullException} is thrown. In other cases, like when the player should
     * already have joined, the more general {@link UnableToJoinGameException} is thrown.
     * <p>
     * This method is blocking. This means that if no exception is thrown, the method will return a {@link Game}
     * object only when all players have joined.
     *
     * @param token  Token received by the {@link UserHandler}.
     * @param gameUuid The uuid of the game to join.
     * @return A Game object.
     * @throws UnableToJoinGameException When the user cannot join the game for various reasons.
     * @throws InvalidTokenException     When the token is invalid (expired or not found).
     * @throws RemoteException
     */
    Game joinGame( String token, String gameUuid ) throws RemoteException, UnableToJoinGameException, InvalidTokenException,
            GameEndedException, WrongServerException;
}
