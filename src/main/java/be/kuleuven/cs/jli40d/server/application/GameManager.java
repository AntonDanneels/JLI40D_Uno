package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameManager implements GameHandler, GameListHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GameManager.class );

    private List<Game> games;

    public GameManager()
    {
        this.games = new ArrayList<>();
    }

    /**
     * Returns if a game is started (can also be finished) or not.
     *
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @return False if the game is not yet started, true otherwise.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    @Override
    public boolean isStarted( String token, int gameID ) throws InvalidTokenException, RemoteException, GameNotFoundException
    {
        Game game = getGameByID( gameID );

        if (game.isEnded()) {
            return true;
        }

        return false;
    }

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
    @Override
    public boolean myTurn( String token, int gameID ) throws InvalidTokenException, RemoteException
    {
        return false;
    }

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
     */
    @Override
    public GameMove getNextMove( String token, int gameID, int nextGameMoveID ) throws InvalidTokenException, RemoteException
    {
        return null;
    }

    /**
     * Send a {@link GameMove} object to update the state of a certain game.
     *
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @param move   The {@link GameMove}.
     * @throws InvalidTokenException
     * @throws RemoteException
     */
    @Override
    public void sendMove( String token, int gameID, GameMove move ) throws InvalidTokenException, RemoteException
    {

    }

    @Override
    public void add( Game game )
    {
        games.add( game );
    }

    @Override
    public int nextID()
    {
        return games.size();
    }

    @Override
    public Game getGameByID( int id ) throws GameNotFoundException
    {
        //if the game is not in the list, throw an error
        if ( games.get( id ) == null )
        {
            LOGGER.warn( "joinGame method called with gameId = {}, but game not found. ", id );

            throw new GameNotFoundException( "Game not found in the list" );
        }

        return games.get( id );
    }

    @Override
    public List<Game> getAllGames()
    {
        return games;
    }
}
