package be.kuleuven.cs.jli40d.server.application.service.async;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class AsyncPlayerService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AsyncPlayerService.class );

    private int    serverID;
    private int    gameID;
    private Player player;

    private DatabaseGameHandler gameHandler;

    public AsyncPlayerService( int serverID, int gameID, Player player, DatabaseGameHandler gameHandler )
    {
        this.serverID = serverID;
        this.gameID = gameID;
        this.player = player;
        this.gameHandler = gameHandler;
    }

    /**
     * Sends an update to the database cluster.
     */
    @Override
    public void run()
    {
        LOGGER.debug( "Persisting player {}:{}", player.getUsername(), player.getId() );

        try
        {
            gameHandler.addPlayer( serverID, gameID, player );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while saving the player for game {} to remote db cluster. {}", gameID, e.getMessage() );
        }
    }
}
