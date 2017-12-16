package be.kuleuven.cs.jli40d.core.service.task;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncGameMoveTask extends AsyncTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameMoveTask.class );

    private GameMove gameMove;

    public AsyncGameMoveTask( int serverID, String gameUuid, GameMove gameMove )
    {
        super( serverID, gameUuid );
        this.gameMove = gameMove;
    }

    /**
     * The only method that defines the contract between the caller and callee, where the
     * function invoking this method can expect a <b>blocking</b> task to be executed.
     *
     * This function publishes a {@link GameMove}.
     *
     * @param databaseGameHandler
     */
    @Override
    public void publish( DatabaseGameHandler databaseGameHandler )
    {
        LOGGER.debug( "Persisting game move {}", gameMove.getId() );

        try
        {
            databaseGameHandler.addMove( getServerID(), getGameUuid(), gameMove );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game move to remote db cluster. {}", e.getMessage() );
        }
    }
}