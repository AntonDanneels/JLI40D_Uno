package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.application.GameListHandler;
import be.kuleuven.cs.jli40d.server.application.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public RemoteGameService( DatabaseGameHandler gameHandler )
    {
        this.gameHandler = gameHandler;
        this.localGameCache = new HashMap<>( 32 );
    }

    @Override
    public void add( Game game )
    {
        //local persistence
        if ( !this.localGameCache.containsKey( game.getGameID() ) )
        {
            //This should be here for an accurate list
            try
            {
                game = gameHandler.saveGame( game ); //this is because of RMI

                LOGGER.debug( "Persisted game and received id = {}", game.getGameID() );
            }
            catch ( RemoteException e )
            {
                LOGGER.error( "Error while saving the game to remote db cluster. {}", e.getMessage() );
            }

            LOGGER.debug( "Adding game with id {} to local cache.", game.getGameID() );
            this.localGameCache.put( game.getGameID(), game );
        }
        else
        {
            //remote persistence
            new Thread( new PersistenceUpdateGameService( gameHandler, game ) ).start();
        }
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
                g = gameHandler.getGame( id );
                localGameCache.put( id, g );
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
            return gameHandler.getGames();
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

        return Collections.emptyList();
    }

    public void addMove( int gameID, GameMove move )
    {
        try
        {
            gameHandler.addMove( gameID, move );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

    }

    public Game getGameByID( int id, boolean forceInvalidation )
    {

        LOGGER.warn( "Fetching game from remote db, forced cache invalidation." );

        Game g = null;
        try
        {
            g = gameHandler.getGame( id );
            localGameCache.put( id, g );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

        localGameCache.put(g.getGameID(), g);

        return g;
    }
}
