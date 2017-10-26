package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Queue;

/**
 * This service is tasked with fetching all the latest {@link GameMove} objects.
 *
 * @author Pieter
 * @version 1.0
 */
public class ListenerService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ListenerService.class );

    private GameHandler gameHandler;

    private String token;
    private int    gameID;
    private int    nextGameMoveID;

    private boolean active;

    private Queue<GameMove> unhandledGameMoves;

    public ListenerService( GameHandler gameHandler, String token, int gameID, Queue<GameMove> unhandledGameMoves )
    {
        this.gameHandler = gameHandler;
        this.token = token;
        this.gameID = gameID;
        this.unhandledGameMoves = unhandledGameMoves;

        this.nextGameMoveID = 0; //TODO make dynamic
        this.active = true;
    }

    @Override
    public void run()
    {
        while ( active )
        {
            try
            {
                GameMove move = gameHandler.getNextMove( token, gameID, nextGameMoveID );
            }
            catch ( InvalidTokenException | RemoteException | GameNotFoundException e )
            {
                LOGGER.error( "Error while fetching next move. {}", e.getMessage() );
                active = false;
            }

            nextGameMoveID++;
        }
    }
}
