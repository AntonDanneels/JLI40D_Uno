package be.kuleuven.cs.jli40d.core.service.task;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncPlayerTask extends AsyncTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncPlayerTask.class );

    private Player player;


    public AsyncPlayerTask( int serverID, String gameUuid, Player player )
    {
        super( serverID, gameUuid );
        this.player = player;
    }

    /**
     * The only method that defines the contract between the caller and callee, where the
     * function invoking this method can expect a <b>blocking</b> task to be executed.
     * <p>
     * In this case, a {@link Player} object will be published to the remote database.
     *
     * @param databaseGameHandler
     */
    @Override
    public void publish( DatabaseGameHandler databaseGameHandler )
    {
        LOGGER.debug( "Persisting player {}:{}", player.getUsername(), player.getId() );

        try
        {
            databaseGameHandler.addPlayer( getServerID(), getGameUuid(), player );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the player for game {} to remote db cluster. {}", getGameUuid(), e.getMessage() );
        }
    }
}
