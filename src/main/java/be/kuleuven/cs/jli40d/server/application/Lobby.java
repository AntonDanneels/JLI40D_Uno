package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.*;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of a {@link LobbyHandler} interface.
 * <p>
 * Validation of tokens happens in a implementation of the {@link UserTokenHandler}.
 * <p>
 * This is a local implementation, meaning every application server has its own list
 * of games in memory. It's not persisted in any way.
 *
 * @author Pieter
 * @version 1.0
 */
public class Lobby extends UnicastRemoteObject implements LobbyHandler, Serializable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Lobby.class );

    private UserTokenHandler userManager;

    private RemoteGameService games;

    private ServerRegistrationHandler dispatcherRegister;

    private Server me;

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     * <p>
     * The object is exported with a server socket
     * created using the {@link RMISocketFactory} class.
     *
     * @param userManager A {@link UserTokenHandler} implementation that keeps track of all tokens.
     * @param games       A {@link GameListHandler} implementation.
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    Lobby( UserTokenHandler userManager, RemoteGameService games, ServerRegistrationHandler dispatcherRegister, Server server ) throws RemoteException
    {
        super();
        this.userManager = userManager;
        this.games = games;
        this.dispatcherRegister = dispatcherRegister;
        this.me = server;
    }

    /**
     * Request a list with all games that are currently ongoing.
     *
     * @param token Token received by the {@link UserHandler}.
     * @return A list of all Game objects.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     */
    public List<GameSummary> currentGames( String token ) throws InvalidTokenException
    {
        LOGGER.debug( "Requested list of games by {}", token );

        //Check authentication, throws error if token is invalid
        userManager.findUserByToken( token );

        List<GameSummary> currentGames = games.getAllGames();
        //currentGames.removeIf( game -> game.isEnded() ); TODO we might ignore it

        return currentGames;
    }

    /**
     * @param token           Token received by the {@link UserHandler}.
     * @param gameName        The desired name of the game, this is displayed in the lobby.
     * @param numberOfPlayers The number of games the game should have.
     * @return
     * @throws InvalidTokenException       When the token is invalid (expired or not found).
     * @throws UnableToCreateGameException When the game cannot be created for some reason (like exceeded limits).
     */
    public String makeGame( String token, String gameName, int numberOfPlayers ) throws
            InvalidTokenException,
            UnableToCreateGameException,
            RemoteException
    {
        //initial check for token
        userManager.findUserByToken( token );

        Game game = new Game( gameName, numberOfPlayers );

        game.setUuid( UUID.randomUUID().toString() );

        GameLogic.generateDeck( game );
        GameLogic.putInitialCardInTheMiddle( game );

        games.add( game );

        dispatcherRegister.registerGame( game.getUuid(), me.getUuid() );

        return game.getUuid();
    }

    /**
     * Join a game with an id, either provided by the {@link #makeGame} or {@link #currentGames} method.
     * <p>
     * If the game is full, a {@link GameFullException} is thrown. In other cases, like when the player should
     * already have joined, the more general {@link UnableToJoinGameException} is thrown.
     * <p>
     * This method is blocking. This means that if no exception is thrown, the method will return a {@link Game}
     * object only when all players have joined.
     * <p>
     * If the player tries to join a {@link Game} where he already joined, this function will not add a new player
     * and will not throw a {@link UnableToJoinGameException} exception either.
     *
     * @param token  Token received by the {@link UserHandler}.
     * @param gameUuid The uuid of the game to join.
     * @return A Game object.
     * @throws UnableToJoinGameException When the user cannot join the game for various reasons.
     * @throws InvalidTokenException     When the token is invalid (expired or not found).
     */
    public synchronized Game joinGame( String token, String gameUuid ) throws
            UnableToJoinGameException,
            InvalidTokenException,
            GameEndedException,
            WrongServerException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        LOGGER.debug( "{} tries to join game with id {}.", username, gameUuid );

        //throws an error if the game is not in the list
        Game requestedGame;
        try
        {
            requestedGame = games.getGameByUuid( gameUuid );
        }
        catch ( GameNotFoundException e )
        {
            LOGGER.warn( "User {} tried to join a non-existing game with id = {}.", username, gameUuid );
            throw new UnableToJoinGameException( "Game not found" );
        }

        if ( requestedGame.hasPlayer( username ) )
        {
            LOGGER.debug( "Player {} tried to re-join a game.", username );
        }
        else if ( requestedGame.getNumberOfJoinedPlayers() >= requestedGame.getMaximumNumberOfPlayers() )
        {
            LOGGER.info( "{} tried to join a full game ( {} ).", username, gameUuid );
            throw new UnableToJoinGameException( "Game full." );
        }
        else if ( requestedGame.isEnded() )
        {
            LOGGER.info( "{} tried to join a game ( {} ) that has ended.", username, gameUuid );
            throw new UnableToJoinGameException( "Game has ended." );
        }
        else
        {
            //create a new player with the next id of the list
            Player player = new Player( requestedGame.getPlayers().size(), username );
            player.setUuid( UUID.randomUUID().toString() );
            requestedGame.getPlayers().add( player );

            //send player to db
            games.addPlayer(requestedGame.getUuid(), player);

            LOGGER.info( "Player {} added to game {} ({}/{}).",
                    username,
                    gameUuid,
                    requestedGame.getPlayers().size(),
                    requestedGame.getMaximumNumberOfPlayers());
            notifyAll();
        }

        //blocking until all players joined
        while ( requestedGame.getNumberOfJoinedPlayers() < requestedGame.getMaximumNumberOfPlayers() )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                LOGGER.error( "Thread interrupted. SAD. {}", e.getMessage() );
                Thread.currentThread().interrupt();
            }
        }

        //Only distribute cards when no moves have been played.
        if ( !requestedGame.isStarted() )
        {
            LOGGER.debug( "Game not yet started, distributing cards." );

            GameLogic.distributeCards( requestedGame );

            //persist game moves that distributed cards
            games.addMoves( gameUuid, requestedGame.getMoves() );

        }

        notifyAll();

        LOGGER.debug( "Returning joinGame method calls." );

        return requestedGame;
    }

    /**
     * This method is in spirit the same as {@link #joinGame(String, String)}, but without the joining part. It also
     * hosts the game on a server, and will only return is the server actually hosts the game.
     * <p>
     * If the game has ended, the user is also unable to spectate. This is because watching a deck in the middle of
     * the table without anything happening isn't that interesting.
     *
     * @param token    Token received by the {@link UserHandler}.
     * @param gameUuid The uuid of the game to join.
     * @return A {@link Game} object.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     * @throws UnableToJoinGameException When the user cannot join the game for various reasons.
     * @throws GameEndedException    An error when the game has ended.
     * @throws WrongServerException  When the {@link Game} is not hosted on this server.
     */
    @Override
    public synchronized Game spectateGame( String token, String gameUuid ) throws RemoteException, UnableToJoinGameException, InvalidTokenException, GameEndedException, WrongServerException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        LOGGER.debug( "{} tries to join game with id {}.", username, gameUuid );

        //throws an error if the game is not in the list
        Game requestedGame;
        try
        {
            requestedGame = games.getGameByUuid( gameUuid );
        }
        catch ( GameNotFoundException e )
        {
            LOGGER.warn( "User {} tried to join a non-existing game with id = {}.", username, gameUuid );
            throw new UnableToJoinGameException( "Game not found" );
        }

        //blocking until all players joined
        while ( requestedGame.getNumberOfJoinedPlayers() < requestedGame.getMaximumNumberOfPlayers() )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                LOGGER.error( "Thread interrupted. SAD. {}", e.getMessage() );
                Thread.currentThread().interrupt();
            }
        }

        return requestedGame;
    }


    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;

        Lobby lobby = ( Lobby ) o;

        if ( userManager != null ? !userManager.equals( lobby.userManager ) : lobby.userManager != null ) return false;
        return games != null ? games.equals( lobby.games ) : lobby.games == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( userManager != null ? userManager.hashCode() : 0 );
        result = 31 * result + ( games != null ? games.hashCode() : 0 );
        return result;
    }
}
