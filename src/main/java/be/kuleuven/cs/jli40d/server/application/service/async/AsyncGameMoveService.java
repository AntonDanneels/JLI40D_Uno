package be.kuleuven.cs.jli40d.server.application.service.async;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncGameMoveService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameMoveService.class );

    private int      serverID;
    private int      gameID;
    private GameMove gameMove;

    private DatabaseGameHandler gameHandler;

    public AsyncGameMoveService( int serverID, int gameID, GameMove gameMove, DatabaseGameHandler gameHandler )
    {
        this.serverID = serverID;
        this.gameID = gameID;
        this.gameMove = gameMove;
        this.gameHandler = gameHandler;
    }


    /**
     * Sends an update to the database cluster.
     */
    @Override
    public void run()
    {
        LOGGER.debug( "Persisting game move {}", gameMove.getId() );

        try
        {
            gameHandler.addMove( serverID, gameID, gameMove );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game move to remote db cluster. {}", e.getMessage() );
        }
    }

}