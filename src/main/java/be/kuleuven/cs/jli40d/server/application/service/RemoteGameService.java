package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.application.GameListHandler;
import be.kuleuven.cs.jli40d.server.application.GameManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

/**
 * This service is an endpoint to the {@link GameManager} and provides
 * two services:
 * <ul>
 *     <li>Maintaining a local cache of games.</li>
 *     <li>Sending updates of invalidated games to the db cluster.</li>
 * </ul>
 *
 * @author Pieter
 * @version 1.0
 */
public class RemoteGameService implements GameListHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteGameService.class);

    private DatabaseGameHandler gameHandler;

    //TODO add cache

    public RemoteGameService( DatabaseGameHandler gameHandler )
    {
        this.gameHandler = gameHandler;
    }

    @Override
    public void add( Game game )
    {
        LOGGER.debug( "Persisting game {}", game.getGameID() );

        try
        {
            game = gameHandler.saveGame( game ); //this is because of RMI
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game to remote db cluster. {}", e.getMessage() );
        }
    }

    @Override
    public Game getGameByID( int id ) throws GameNotFoundException
    {
        //if the game is not in the list, throw an error
        Game g = null;
        try
        {
            g = gameHandler.getGame( id );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching the game from remote. {}", e.getMessage() );
        }

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
}
