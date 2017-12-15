package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.service.TaskQueueService;
import be.kuleuven.cs.jli40d.core.service.task.*;
import be.kuleuven.cs.jli40d.server.application.GameListHandler;
import be.kuleuven.cs.jli40d.server.application.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
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
    private Map<String, Game> localGameCache;

    //remote async publisher
    private TaskQueueService taskQueueService;
    private Deque<AsyncTask> tasks;

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

        serverID = UUID.randomUUID().hashCode();
        LOGGER.debug( "Server received id = {} (self-generated).", serverID );

        tasks = new ConcurrentLinkedDeque<>();
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
    public Game getGameByUuid( String uuid ) throws GameNotFoundException
    {
        Game g = null;

        if ( localGameCache.containsKey( uuid ) )
        {
            g = localGameCache.get( uuid );
        }
        else
        {
            LOGGER.warn( "fetching game with id = {} from remote db cluster. This action is not cached.", uuid );
            try
            {
                g = gameHandler.getGame( serverID, uuid );
            }
            catch ( RemoteException e )
            {
                LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
            }
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
    public List<GameSummary> getAllGames()
    {
        try
        {
            // TODO: find a way to get these games in a synchronised way,
            // Currently, this locks the db

            //fetching remote games
            //List<GameSummary> gameSummaries = gameHandler.getGames( serverID );

            //adding games hosted on this host
            List<GameSummary> localGames = localGameCache.values().stream()
                    .map( g -> new GameSummary(
                            g.getUuid(),
                            g.getName(),
                            g.getNumberOfJoinedPlayers(),
                            g.getMaximumNumberOfPlayers(),
                            g.isStarted() ) )
                    .collect( Collectors.toList() );

            //localGames.addAll( gameSummaries );

            return localGames;
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

    public synchronized void addMoves( String gameUuid, List<GameMove> moves )
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
