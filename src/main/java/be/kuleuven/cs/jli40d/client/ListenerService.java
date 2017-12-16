package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.GameEndedException;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;
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
    private Game   game;
    private GameClient client;

    private volatile boolean active;
    private int     currentGameMoveID;

    private ServerRegistrationHandler registrationHandler;

    private Queue<GameMove> unhandledGameMoves;

    public ListenerService( GameClient client, ServerRegistrationHandler registrationHandler, GameHandler gameHandler, String token, Game game, Queue<GameMove> unhandledGameMoves )
    {
        this.client = client;
        this.game = game;
        this.gameHandler = gameHandler;
        this.token = token;
        this.unhandledGameMoves = unhandledGameMoves;
        this.registrationHandler = registrationHandler;

        this.active = true;

        this.currentGameMoveID = game.getMoves().size();
    }

    @Override
    public synchronized void run()
    {
        while ( active )
        {
            try
            {
                GameMove move = gameHandler.getNextMove( token, game.getUuid(), currentGameMoveID );
                unhandledGameMoves.add( move );
                LOGGER.debug( "Added move {}: {}:{}", currentGameMoveID,
                        move.getPlayedCard().getColour(),
                        move.getPlayedCard().getType() );
                currentGameMoveID++;

            }
            catch ( InvalidTokenException | RemoteException | GameNotFoundException | GameEndedException e )
            {
                LOGGER.error( "Error while fetching next move. {}", e.getMessage() );
                active = false;
            }
            catch ( WrongServerException e )
            {
                LOGGER.debug( "Changing server" );

                try
                {
                    Server newServer = registrationHandler.getServer( game.getUuid() );
                    client.resetConnection( newServer );
                    this.run();
                }
                catch ( RemoteException e1 )
                {
                    e1.printStackTrace();
                }
                catch ( GameNotFoundException e1 )
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void setGameHandler( GameHandler gameHandler )
    {
        this.gameHandler = gameHandler;
    }

    public synchronized void setActive( boolean active )
    {
        this.active = active;
    }
}
