package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.application.GameListHandler;
import be.kuleuven.cs.jli40d.server.application.GameManager;
import be.kuleuven.cs.jli40d.server.application.service.async.AsyncGameMoveService;
import be.kuleuven.cs.jli40d.server.application.service.async.AsyncGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This service is an endpoint to the {@link GameManager} and provides
 * two services:
 * <ul>
 * <li>Maintaining a local cache of games.</li>
 * <li>Sending updates of invalidated games to the db cluster.</li>
 * </ul>
 *
 * @author Pieter
 * @version 1.0
 */
public class RemoteGameService implements GameListHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteGameService.class );

    private DatabaseGameHandler gameHandler;

    //Cache
    private Map<Integer, Game> localGameCache;

    private int serverID;

    /**
     * This creates a RemoteGameService, that communicates with a remote db cluster
     * and implements {@link GameListHandler} to provide a list-like api.
     * <p>
     * On creation, this will register itself with the server as a server and receive an id.
     *
     * @param gameHandler The remote RMI object.
     */
    public RemoteGameService( DatabaseGameHandler gameHandler )
    {
        this.gameHandler = gameHandler;
        this.localGameCache = new HashMap<>( 32 );

        try
        {
            serverID = gameHandler.registerServer();
            LOGGER.debug( "Server received id = {}.", serverID );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while requesting the server id." );
        }
    }

    @Override
    public void add( Game game )
    {
        //local persistence
        if ( !this.localGameCache.containsKey( game.getGameID() ) )
        {
            LOGGER.debug( "Adding game with id {} to local cache.", game.getGameID() );
            this.localGameCache.put( game.getGameID(), game );
        }

        //remote persistence
        new Thread( new AsyncGameService( serverID, gameHandler, game ) ).start();

    }

    @Override
    public Game getGameByID( int id ) throws GameNotFoundException
    {
        Game g = null;

        if ( localGameCache.containsKey( id ) )
        {
            g = localGameCache.get( id );
        }
        else
        {
            LOGGER.warn( "fetching game with id = {} from remote db cluster. This action is not cached.", id );
            try
            {
                g = gameHandler.getGame( serverID, id );
            }
            catch ( RemoteException e )
            {
                LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
            }
        }

        //if the game is not in the list, throw an error
        if ( g == null )
        {
            LOGGER.warn( "joinGame method called with gameId = {}, but game not found. ", id );

            throw new GameNotFoundException( "Game not found in the list" );
        }

        return g;
    }

    @Override
    public List<GameSummary> getAllGames()
    {
        try
        {
            //fetching remote games
            List<GameSummary> gameSummaries = gameHandler.getGames( serverID );

            //adding games hosted on this host
            List<GameSummary> localGames = localGameCache.values().stream()
                    .map( g -> new GameSummary(
                            g.getGameID(),
                            g.getName(),
                            g.getNumberOfJoinedPlayers(),
                            g.getMaximumNumberOfPlayers(),
                            g.isStarted() ) )
                    .collect( Collectors.toList() );

            localGames.addAll( gameSummaries );

            return localGames;
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

        return Collections.emptyList();
    }

    public void addMove( int gameID, GameMove move )
    {

        //remote persistence
        new Thread( new AsyncGameMoveService( serverID, gameID, move, gameHandler ) ).start();

        try
        {
            gameHandler.addMove( serverID, gameID, move );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

    }
}
