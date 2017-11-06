package be.kuleuven.cs.jli40d.server.application.service.async;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncGameService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameService.class );

    private int serverID;
    private DatabaseGameHandler gameHandler;
    private Game                game;

    public AsyncGameService( int serverID, DatabaseGameHandler gameHandler, Game game )
    {
        this.serverID = serverID;
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
            gameHandler.saveGame(serverID, game );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game to remote db cluster. {}", e.getMessage() );
        }
    }
}
