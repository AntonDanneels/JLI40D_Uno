package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.ResourceHandler;
import be.kuleuven.cs.jli40d.core.ServerManagementHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.PlayerHand;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidGameMoveException;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerManager extends UnicastRemoteObject implements ServerManagementHandler, Serializable
{
    Logger LOGGER = LoggerFactory.getLogger( ServerManager.class.getName() );

    private RemoteGameService remoteGameService;
    private DatabaseGameHandler gameHandler;

    public ServerManager( RemoteGameService remoteGameService, DatabaseGameHandler databaseGameHandler ) throws RemoteException
    {
        this.remoteGameService = remoteGameService;
        this.gameHandler = databaseGameHandler;
    }

    /**
     *  Asks the server to load games from another server & register them
     */
    public synchronized void loadFromServer( Server server, List<String> gameIDS ) throws RemoteException
    {
        LOGGER.info( "Getting games from other server" );

        for( String s : gameIDS )
        {
            try
            {
                Game game = gameHandler.getGame( server.getID(), s );

                game.setPlayerHands( new HashMap <>() );
                Map<String, PlayerHand> cardsPerPlayer = game.getPlayerHands();

                for ( int j = 0; j < game.getPlayers().size(); j++ )
                    cardsPerPlayer.put( game.getPlayers().get( j ).getUsername(), new PlayerHand() );

                List<GameMove> oldMoves = new ArrayList <>();
                oldMoves.addAll( game.getMoves() );

                game.getMoves().clear();

                for( GameMove m : oldMoves )
                {
                    try
                    {
                        GameLogic.applyMove( game, m );
                    }
                    catch ( InvalidGameMoveException e )
                    {
                        e.printStackTrace();
                    }
                }

                game.setStarted( true );
                game.setCurrentGameMoveID( game.getMoves().size() );

                remoteGameService.addGame( game.getUuid(), game );

                LOGGER.debug( "Transferred game {}", game.getUuid() );
            }
            catch ( GameNotFoundException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void updateCurrentResourcepack( String respack ) throws RemoteException
    {
        ResourceManager.CURRENT_RESOURCEPACK = respack;
    }

    /**
     *  Indicates that the server should stop accepting requests for new games
     *  or gamemoves. Clients will connect to a different server. The server
     *  will not shutdown but keep running until {@see shutDown} is called.
     */
    public synchronized void prepareShutdown() throws RemoteException
    {
        LOGGER.info( "Preparing for shutdown" );

        ApplicationMain.IS_RUNNING = false;
    }

    /**
     *  This will completely shut down a server. {@see prepareShutdown} must be called if
     *  to properly transfer games to another server.
     * */
    public synchronized void shutDown() throws RemoteException
    {
        LOGGER.info( "Shutting down" );

        System.exit( 0 );
    }
}
