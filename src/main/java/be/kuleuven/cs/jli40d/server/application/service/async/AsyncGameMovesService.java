package be.kuleuven.cs.jli40d.server.application.service.async;

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
public class AsyncGameMovesService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncGameMovesService.class );

    private int      serverID;
    private int      gameID;

    private List<GameMove> gameMoves;

    private DatabaseGameHandler gameHandler;

    public AsyncGameMovesService( int serverID, int gameID, List<GameMove> gameMoves, DatabaseGameHandler gameHandler )
    {
        this.serverID = serverID;
        this.gameID = gameID;
        this.gameMoves = gameMoves;
        this.gameHandler = gameHandler;
    }

    /**
     * Sends an update to the database cluster.
     */
    @Override
    public void run()
    {
        LOGGER.debug( "Persisting {} game moves.", gameMoves.size());

        try
        {
            gameHandler.addMoves( serverID, gameID, gameMoves );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the game move to remote db cluster. {}", e.getMessage() );
        }
    }

}