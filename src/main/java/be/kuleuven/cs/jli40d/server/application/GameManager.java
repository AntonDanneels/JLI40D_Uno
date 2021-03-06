package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.*;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameManager extends UnicastRemoteObject implements GameHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GameManager.class );

    private CachedUserManager userManager;
    private RemoteGameService gameService;

    public GameManager( CachedUserManager userManager, RemoteGameService gameService ) throws RemoteException
    {
        this.gameService = gameService;
        this.userManager = userManager;
    }

    /**
     * Returns if a game is started (can also be finished) or not.
     *
     * @param token    The token given to the user for authentication.
     * @param gameUuid The uuid of the game.
     * @return False if the game is not yet started, true otherwise.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    @Override
    public boolean isStarted( String token, String gameUuid ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            WrongServerException
    {
        Game game = gameService.getGameByUuid( gameUuid );
        userManager.findUserByToken( token );

        //If the game has ended or all players have joined it
        return game.isStarted();

    }

    @Override
    public synchronized boolean myTurn( String token, String gameUuid ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            WrongServerException
    {
        // Initiate server switch
        if( ApplicationMain.IS_SHUTTING_DOWN )
            throw new WrongServerException();

        // Lock the server from receiving new moves
        if( !ApplicationMain.IS_RUNNING )
            return false;

        Game game = gameService.getGameByUuid( gameUuid );

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
     * @param gameUuid       The uuid of the game.
     * @param nextGameMoveID The id of the next gameMove for a certain game.
     * @return The next GameMove when one is ready.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws RemoteException
     * @throws GameNotFoundException When the game is not found.
     */
    @Override
    public synchronized GameMove getNextMove( String token, String gameUuid, int nextGameMoveID ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            GameEndedException,
            WrongServerException
    {
        if( ApplicationMain.IS_SHUTTING_DOWN )
            throw new WrongServerException();

        Game   game     = gameService.getGameByUuid( gameUuid );
        String username = userManager.findUserByToken( token ); //TODO check if authenticated for game

        while ( game.getMoves().size() <= nextGameMoveID )
        {
            if( ApplicationMain.IS_SHUTTING_DOWN )
                throw new WrongServerException();

            if ( game.isEnded() )
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

        return game.getMoves().get( nextGameMoveID );
    }

    /**
     * Send a {@link GameMove} object to update the state of a certain game.
     * <p>
     * This method also checks if the player was authorised and it was his/her
     * turn to make a move.
     *
     * @param token    The token given to the user for authentication.
     * @param gameUuid The uuid of the game.
     * @throws InvalidTokenException
     * @throws RemoteException
     * @throws GameNotFoundException    When the game is not found.
     * @throws InvalidGameMoveException When the move is invalid.
     */
    @Override
    public synchronized void sendMove( String token, String gameUuid, GameMove move ) throws
            InvalidTokenException,
            RemoteException,
            GameNotFoundException,
            InvalidGameMoveException,
            WrongServerException
    {
        Game   game     = gameService.getGameByUuid( gameUuid );
        String username = userManager.findUserByToken( token );

        if ( !game.getCurrentPlayerUsername().equals( username )
                || !GameLogic.testMove( game, move ) )
        {
            throw new InvalidGameMoveException( "Either not your turn or invalid move" );
        }

        int currentMove = game.getMoves().size();

        GameLogic.applyMove( game, move );

        if ( GameLogic.hasGameEnded( game ) )
        {
            LOGGER.debug( "The game has ended, marking it & waking the other threads" );
            game.setEnded( true );
            Player winner = GameLogic.getWinner( game );
            int    score  = GameLogic.calculateScoreForPlayer( winner.getUsername(), game );

            userManager.updateScore( winner.getUsername(), score );
        }
        int endMove = game.getMoves().size();

        for ( int i = currentMove; i < endMove; i++ )
            gameService.addMove( gameUuid, game.getMoves().get( i ) );

        LOGGER.debug( "{} added a move to game {}", username, game );

        notifyAll();

    }
}
