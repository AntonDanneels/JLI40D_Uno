package be.kuleuven.cs.jli40d.server.application.service.task;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncGameTask extends AsyncTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameTask.class );

    private Game game;

    public AsyncGameTask( int serverID, Game game )
    {
        super( serverID, game.getGameID() );
        this.game = game;
    }

    /**
     * The only method that defines the contract between the caller and callee, where the
     * function invoking this method can expect a <b>blocking</b> task to be executed.
     * <p>
     * This function publishes a {@link Game} object.
     *
     * @param databaseGameHandler
     */
    @Override
    public void publish( DatabaseGameHandler databaseGameHandler )
    {
        LOGGER.debug( "Persisting game {}", game.getGameID() );

        try
        {
            databaseGameHandler.saveGame( getServerID(), game );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game to remote db cluster. {}", e.getMessage() );
        }

    }
}
