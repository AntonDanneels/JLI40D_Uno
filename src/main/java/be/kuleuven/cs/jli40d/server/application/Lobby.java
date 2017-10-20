package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.GameFullException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToCreateGameException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToJoinGameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a {@link LobbyHandler} interface.
 * <p>
 * Validation of tokens happens in a implementation of the {@link UserTokenHandler}.
 *
 * @author Pieter
 * @version 1.0
 */
public class Lobby extends UnicastRemoteObject implements LobbyHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Lobby.class);

    private UserTokenHandler userManager;

    private List<Game> games;

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     * <p>
     * <p>The object is exported with a server socket
     * created using the {@link RMISocketFactory} class.
     *
     * @param userManager A {@link UserTokenHandler} implementation that keeps track of all tokens.
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    protected Lobby( UserTokenHandler userManager ) throws RemoteException
    {
        this.userManager = userManager;

        games = new ArrayList<>();
    }

    /**
     * Request a list with all games that are currently ongoing.
     *
     * @param token Token received by the {@link UserHandler}.
     * @return A list of all Game objects.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     */
    public List<Game> currentGames( String token ) throws InvalidTokenException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        return games;
    }

    /**
     * @param token           Token received by the {@link UserHandler}.
     * @param gameName        The desired name of the game, this is displayed in the lobby.
     * @param numberOfPlayers The number of games the game should have.
     * @return
     * @throws InvalidTokenException       When the token is invalid (expired or not found).
     * @throws UnableToCreateGameException When the game cannot be created for some reason (like exceeded limits).
     */
    public int makeGame( String token, String gameName, int numberOfPlayers ) throws InvalidTokenException, UnableToCreateGameException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        Game game = new Game(); //TODO: get data from game like game id

        return 0;
    }

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
    public Game joinGame( String token, int gameId ) throws UnableToJoinGameException, InvalidTokenException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        //if the game is not in the list, throw an error
        if (games.get( gameId ) == null) {
            LOGGER.warn( "joinGame method called by {} with gameId = {}, but game not found. ", username, gameId );

            throw new UnableToJoinGameException( "Game not found in the list" );
        }

        return null;
    }
}
