package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;
import be.kuleuven.cs.jli40d.core.service.TaskQueueService;
import be.kuleuven.cs.jli40d.core.service.task.*;
import be.kuleuven.cs.jli40d.server.application.GameListHandler;
import be.kuleuven.cs.jli40d.server.application.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
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
    private Map <String, Game> localGameCache;

    //remote async publisher
    private TaskQueueService          taskQueueService;
    private BlockingDeque <AsyncTask> tasks;

    private int serverID;

    /**
     * This creates a RemoteGameService, that communicates with a remote db cluster
     * and implements {@link GameListHandler} to provide a list-like api.
     * <p>
     * On creation, this will register itself with the server as a server and receive an id.
     *
     * @param gameHandler The remote RMI object.
     * @param me
     */
    public RemoteGameService( DatabaseGameHandler gameHandler, Server me )
    {
        this.gameHandler = gameHandler;
        this.localGameCache = new HashMap <>( 32 );

        serverID = me.getID();
        LOGGER.debug( "Server received id = {} from deployer.", serverID );

        tasks = new LinkedBlockingDeque <>();
        taskQueueService = new TaskQueueService( tasks, gameHandler );
        new Thread( taskQueueService ).start();
        LOGGER.info( "Started a remote publishing service [ {} ].", taskQueueService.getClass().getSimpleName() );
    }

    @Override
    public synchronized void add( Game game )
    {
        //local persistence
        if ( !this.localGameCache.containsKey( game.getGameID() ) )
        {
            LOGGER.debug( "Adding game with id {} to local cache.", game.getGameID() );
            this.localGameCache.put( game.getUuid(), game );
        }

        //remote persistence
        tasks.add( new AsyncGameTask( serverID, game ) );

        notifyAll();
    }

    @Override
    public Game getGameByUuid( String uuid ) throws GameNotFoundException, WrongServerException
    {
        Game g = null;

        if ( localGameCache.containsKey( uuid ) )
        {
            g = localGameCache.get( uuid );
        }
        else
        {
            LOGGER.warn( "Game {} is hosted on another server, throwing exception.", uuid );
            throw new WrongServerException();
        }

        //if the game is not in the list, throw an error
        if ( g == null )
        {
            LOGGER.warn( "joinGame method called with gameId = {}, but game not found. ", uuid );

            throw new GameNotFoundException( "Game not found in the list" );
        }

        return g;
    }

    @Override
    public List <GameSummary> getAllGames()
    {
        try
        {
            //fetching remote games
            List<GameSummary> gameSummaries = gameHandler.getGames();

            //adding games hosted on this host
            /*
            List <GameSummary> localGames = localGameCache.values().stream()
                    .map( g -> new GameSummary(
                            g.getUuid(),
                            g.getName(),
                            g.getNumberOfJoinedPlayers(),
                            g.getMaximumNumberOfPlayers(),
                            g.isStarted() ) )
                    .collect( Collectors.toList() );
            */

            return gameSummaries;
        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

        return Collections.emptyList();
    }

    public synchronized void addMove( String gameUuid, GameMove move )
    {
        LOGGER.debug( "Added move to persist async." );

        tasks.add( new AsyncGameMoveTask( serverID, gameUuid, move ) );

        notifyAll();
    }

    public synchronized void addMoves( String gameUuid, List <GameMove> moves )
    {
        LOGGER.debug( "Added moves to persist async." );

        tasks.add( new AsyncGameMovesTask( serverID, gameUuid, moves ) );

        notifyAll();
    }

    public synchronized void addPlayer( String gameUuid, Player player )
    {
        LOGGER.debug( "Added player to persist async." );

        tasks.add( new AsyncPlayerTask( serverID, gameUuid, player ) );

        notifyAll();
    }
}
