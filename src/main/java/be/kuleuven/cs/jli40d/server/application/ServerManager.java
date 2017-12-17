package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.ServerManagementHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServerManager  extends UnicastRemoteObject implements ServerManagementHandler, Serializable
{
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
    public void loadFromServer( Server server, List<String> gameIDS ) throws RemoteException
    {
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
        ApplicationMain.IS_RUNNING = false;
    }

    /**
     *  This will completly shut down a server. {@see prepareShutdown} must be called if
     *  to properly transfer games to another server.
     * */
    public void shutDown() throws RemoteException
    {
        ApplicationMain.IS_SHUTTING_DOWN = true;
        notifyAll();
    }
}
