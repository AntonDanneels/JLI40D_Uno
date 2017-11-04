package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class PersistenceUpdateGameService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( PersistenceUpdateGameService.class );

    private DatabaseGameHandler gameHandler;
    private Game                game;

    public PersistenceUpdateGameService( DatabaseGameHandler gameHandler, Game game )
    {
        this.gameHandler = gameHandler;
        this.game = game;
    }

    /**
     * Sends an update to the database cluster.
     */
    @Override
    public void run()
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
}
