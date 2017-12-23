package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.deployer.Server;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerManagementHandler extends Remote, Serializable
{
    /**
     *  Asks the server to load games from another server & register them
     */
    void loadFromServer( Server server, List<String> gameIDS ) throws RemoteException;

    /**
     *  Indicates that the server should stop accepting requests for new games
     *  or gamemoves. Clients will connect to a different server. The server
     *  will not shutdown but keep running until {@see shutDown} is called.
     */
    void prepareShutdown() throws RemoteException;

    /**
     *  This will completly shut down a server. {@see prepareShutdown} must be called if
     *  to properly transfer games to another server.
     * */
    void shutDown() throws RemoteException;

    void updateCurrentResourcepack( String respack ) throws RemoteException;
}
