package be.kuleuven.cs.jli40d.core.service.task;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncGameMovesTask extends AsyncTask
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameMoveTask.class );

    private List<GameMove> gameMoves;

    public AsyncGameMovesTask( int serverID, String gameUuid, List<GameMove> gameMoves )
    {
        super( serverID, gameUuid );
        this.gameMoves = gameMoves;
    }

    /**
     * The only method that defines the contract between the caller and callee, where the
     * function invoking this method can expect a <b>blocking</b> task to be executed.
     * <p>
     * This function publishes a list of {@link GameMove} objects.
     *
     * @param databaseGameHandler
     */
    @Override
    public void publish( DatabaseGameHandler databaseGameHandler )
    {
        LOGGER.debug( "Persisting {} game moves", gameMoves.size() );

        try
        {
            databaseGameHandler.addMoves( getServerID(), getGameUuid(), gameMoves );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game moves to remote db cluster. {}", e.getMessage() );
        }
    }

}