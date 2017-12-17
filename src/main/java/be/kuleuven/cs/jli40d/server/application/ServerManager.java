package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.ServerManagementHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

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
                remoteGameService.addGame( game.getUuid(), game );
            }
            catch ( GameNotFoundException e )
            {
                e.printStackTrace();
            }
        }
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
     *  This will completly shut down a server. {@see prepareShutdown} must be called if
     *  to properly transfer games to another server.
     * */
    public synchronized void shutDown() throws RemoteException
    {
        LOGGER.info( "Shutting down" );

        ApplicationMain.IS_SHUTTING_DOWN = true;
        notifyAll();
    }
}
