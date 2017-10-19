package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.GameFullException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToCreateGameException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToJoinGameException;

import java.util.List;

/**
 * The lobby has three main functions:
 * <ul>
 * <li>Viewing a list of all games.</li>
 * <li>Making a new game.</li>
 * <li>Joining a existing game (either as spectator or player).</li>
 * </ul>
 */
public interface LobbyHandler
{

    /**
     * Request a list with all games that are currently ongoing.
     *
     * @param token Token received by the {@link UserHandler}.
     * @return A list of all Game objects.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     */
    List <Game> currentGames( String token ) throws InvalidTokenException;

    /**
     * @param token           Token received by the {@link UserHandler}.
     * @param gameName        The desired name of the game, this is displayed in the lobby.
     * @param numberOfPlayers The number of games the game should have.
     * @return
     * @throws InvalidTokenException       When the token is invalid (expired or not found).
     * @throws UnableToCreateGameException When the game cannot be created for some reason (like exceeded limits).
     */
    int makeGame( String token, String gameName, int numberOfPlayers ) throws InvalidTokenException, UnableToCreateGameException;

    /**
     * Join a game with an id, either provided by the {@link #makeGame} or {@link #currentGames} method.
     * <p>
     * If the game is full, a {@link GameFullException} is thrown. In other cases, like when the player should
     * already have joined, the more general {@link UnableToJoinGameException} is thrown.
     *
     * @param token  Token received by the {@link UserHandler}.
     * @param gameId The id of the game to join.
     * @return A Game object.
     * @throws UnableToJoinGameException When the user cannot join the game for various reasons.
     * @throws InvalidTokenException     When the token is invalid (expired or not found).
     */
    Game joinGame( String token, int gameId ) throws UnableToJoinGameException, InvalidTokenException;
}
