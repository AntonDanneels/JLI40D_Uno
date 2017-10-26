package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidGameMoveException;
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
    private Game    game;

    private boolean active;

    private Queue<GameMove> unhandledGameMoves;
    private Client client;

    public ListenerService( Client client, GameHandler gameHandler, String token, Game game, Queue<GameMove> unhandledGameMoves )
    {
        this.client = client;
        this.gameHandler = gameHandler;
        this.token = token;
        this.game = game;
        this.unhandledGameMoves = unhandledGameMoves;

        this.active = true;
    }

    @Override
    public void run()
    {
        while ( active )
        {
            try
            {
                GameMove move = gameHandler.getNextMove( token, game.getGameID(), game.getCurrentGameMoveID() + 1 );
                unhandledGameMoves.add( move );
                client.run();
            }
            catch ( InvalidTokenException | RemoteException | GameNotFoundException e )
            {
                LOGGER.error( "Error while fetching next move. {}", e.getMessage() );
                active = false;
            }
            catch ( InvalidGameMoveException e )
            {
                LOGGER.warn( "client.run returned an invalidGameMoveException. {}", e.getMessage() );
            }
        }
    }
}
