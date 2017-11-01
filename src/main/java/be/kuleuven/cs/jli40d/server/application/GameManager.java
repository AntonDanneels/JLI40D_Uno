package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameEndedException;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidGameMoveException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameManager extends UnicastRemoteObject implements GameHandler, GameListHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GameManager.class );

    private List<Game> games;

    private UserTokenHandler userManager;

    public GameManager( UserTokenHandler userManager ) throws RemoteException
    {
        this.games = new ArrayList<>();

        this.userManager = userManager;
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
    public boolean isStarted( String token, long gameID ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException
    {
        Game game = getGameByID( gameID );
        userManager.findUserByToken( token );


        //If the game has ended or all players have joined it
        return game.isStarted();

    }

    @Override
    public synchronized boolean myTurn( String token, long gameID ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException
    {
        Game game = getGameByID( gameID );

        String username = userManager.findUserByToken( token );

        return game.getCurrentPlayerUsername().equals( username );
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
     * @throws GameNotFoundException When the game is not found.
     */
    @Override
    public synchronized GameMove getNextMove( String token, long gameID, long nextGameMoveID ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            GameEndedException
    {
        Game   game     = getGameByID( gameID );
        String username = userManager.findUserByToken( token ); //TODO check if authenticated for game

        while ( game.getMoves().size() <= nextGameMoveID )
        {
            if( game.isEnded() )
                throw new GameEndedException();
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                LOGGER.error( "Thread interrupted while waiting for next game move." );
                Thread.currentThread().interrupt();
            }
        }

        notifyAll();

        LOGGER.debug( "Sending move with id = {} for game {} to {}", nextGameMoveID, game, username );

        return game.getMoves().get( (int) nextGameMoveID );
    }

    /**
     * Send a {@link GameMove} object to update the state of a certain game.
     * <p>
     * This method also checks if the player was authorised and it was his/her
     * turn to make a move.
     *
     * @param token  The token given to the user for authentication.
     * @param gameID The id of the game.
     * @throws InvalidTokenException
     * @throws RemoteException
     * @throws GameNotFoundException    When the game is not found.
     * @throws InvalidGameMoveException When the move is invalid.
     */
    @Override
    public synchronized void sendMove( String token, long gameID, GameMove move ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            InvalidGameMoveException
    {
        Game   game     = getGameByID( gameID );
        String username = userManager.findUserByToken( token );

        if ( !game.getCurrentPlayerUsername().equals( username )
                || !GameLogic.testMove( game, move ) )
        {
            throw new InvalidGameMoveException( "Either not your turn or invalid move" );
        }

        GameLogic.applyMove( game, move );

        if( GameLogic.hasGameEnded( game ) )
        {
            LOGGER.debug( "The game has ended, marking it & waking the other threads" );
            game.setEnded( true );
            Player winner = GameLogic.getWinner( game );
            int score = GameLogic.calculateScoreForPlayer( winner.getUsername(), game );

            // TODO: save score in DB here
        }

        LOGGER.debug( "{} added a move to game {}", username, game );

        notifyAll();

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
    public Game getGameByID( long id ) throws GameNotFoundException
    {
        //if the game is not in the list, throw an error
        if ( games.get( (int) id ) == null )
        {
            LOGGER.warn( "joinGame method called with gameId = {}, but game not found. ", id );

            throw new GameNotFoundException( "Game not found in the list" );
        }

        return games.get( (int) id );
    }

    @Override
    public List<Game> getAllGames()
    {
        return games;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        GameManager manager = ( GameManager ) o;

        return games != null ? games.equals( manager.games ) : manager.games == null;
    }

    @Override
    public int hashCode()
    {
        return games != null ? games.hashCode() : 0;
    }
}
