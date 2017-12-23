package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.ServerManagementHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * @author Pieter
 * @version 1.0
 */
@Component
public class ServerRegister extends UnicastRemoteObject implements ServerRegistrationHandler, Serializable
{
    private final Logger LOGGER = LoggerFactory.getLogger( ServerRegister.class.getName() );

    private static final int MIN_PORT        = 1101;
    private static final int MAX_PORT        = 1200;
    static final int DATABASE_SERVER = 2;

    private Map <String, Integer> portsOnHosts;

    private Set <Server> applicationServers;
    private Set <Server> databaseServers;

    private Map <Server, List <Server>> serverMapping;
    private Map <Server, List <String>> clientMapping;
    private Map <String, String>        gameServerMapping;
    private Map <String, List <String>> serverGameMapping;

    public ServerRegister() throws RemoteException
    {
        this.portsOnHosts = new HashMap <>();
        this.applicationServers = new HashSet <>();
        this.databaseServers = new HashSet <>();
        this.serverMapping = new HashMap <>();
        this.clientMapping = new HashMap <>();
        this.gameServerMapping = new HashMap <>();
        this.serverGameMapping = new HashMap <>();
    }

    /**
     * This function allows a server to obtain a port for a certain hostname.
     * <p>
     * Note that hostname resolving is not implied in this contract. This means
     * that e.g. localhost and 127.0.0.1 might be equal, but this is not
     * guaranteed by the contract.
     * <p>
     * Note also that the deployer has no knowledge of available/non-blocked ports
     * on the host. This means it's up to the host to verify the functionality of
     * the given port.
     *
     * @param host       The hostname/ip address of the host requesting a port.
     * @param serverType The type of server as a {@link ServerType} enum.
     * @return A {@link Server} object with the provided hostname and generated port number.
     * @throws RemoteException
     */
    @Override
    public synchronized Server obtainPort( String host, ServerType serverType ) throws RemoteException
    {
        LOGGER.info( "Server obtaining port: " + host + " for servertype: " + serverType );
        int port = MIN_PORT;

        if ( portsOnHosts.containsKey( host ) )
        {
            port = portsOnHosts.get( host ) + 1;
            portsOnHosts.put( host, port );
        }
        else
        {
            portsOnHosts.put( host, new Integer( port ) );
        }

        Server server = new Server( host, port, serverType, UUID.randomUUID().toString() );

        LOGGER.info( "Server {} registered.", server );

        return server;
    }

    /**
     * After the deployer is finished booting, has obtained a port and has registered
     * his RMI server, this function can be called.
     * <p>
     * It provides al list with all known other servers. This is blocking and
     * will only return when all database servers have also called this function and
     * are registered on the deployer.
     *
     * @param self The {@link Server} object given by {@link #obtainPort(String, ServerType)}.
     * @return A set of all {@link Server} objects, including the caller itself.
     * @throws RemoteException
     */
    @Override
    public synchronized Set <Server> registerDatabase( Server self ) throws RemoteException
    {
        LOGGER.info( "Registring database: " + self.getHost() + ":" + self.getPort() );

        databaseServers.add( self );
        serverMapping.put( self, new ArrayList <>() );

        notifyAll();

        while ( databaseServers.size() < DATABASE_SERVER )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        return databaseServers;
    }

    /**
     * Returns a database server for an application server. The Dispatcher has load balancing
     * and chooses the database with the least amount of load.
     * <p>
     * This method is blocking until all db's are registered.
     *
     * @param self The {@link Server} object given by {@link #obtainPort(String, ServerType)}.
     * @return A database {@link Server} for the application server to connect to.
     * @throws RemoteException
     */
    public synchronized Server registerAppServer( Server self ) throws RemoteException
    {
        LOGGER.info( "Registering application server: " + self.getHost() + ":" + self.getPort() );

        applicationServers.add( self );
        clientMapping.put( self, new ArrayList <>() );
        serverGameMapping.put( self.getUuid(), new ArrayList <>() );

        while ( databaseServers.size() < DATABASE_SERVER )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        Server lowestLoad = getLowest( serverMapping );

        serverMapping.get( lowestLoad ).add( self );

        return lowestLoad;
    }

    /**
     * Registers a game client and returns an application server with the least amount of load.
     * The game server will update the dispatcher if the client loses connection.
     *
     * @param uuid An {@link java.util.UUID } string to uniqly represent a client, note that this
     *             is separate from tokens: a client cannot pretend to be someone else.
     * @return An Application Server.
     * @throws RemoteException
     */
    public synchronized Server registerGameClient( String uuid ) throws RemoteException
    {
        while ( databaseServers.size() < DATABASE_SERVER && applicationServers.size() < 1 )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        Server result = getLowest( clientMapping );

        clientMapping.get( result ).add( uuid );

        return result;
    }

    public synchronized void unregisterGameClient( Server server, String clientUUID ) throws RemoteException
    {
        if ( clientMapping.containsKey( server ) )
            clientMapping.get( server ).remove( clientUUID );
    }

    private <T, Y> T getLowest( Map <T, List <Y>> mapping )
    {
        T   lowestLoad   = null;
        int lowestAmount = Integer.MAX_VALUE;

        for ( Map.Entry <T, List <Y>> entry : mapping.entrySet() )
        {
            if ( entry.getValue().size() < lowestAmount )
            {
                lowestLoad = entry.getKey();
                lowestAmount = mapping.get( lowestLoad ).size();
            }
        }

        return lowestLoad;
    }

    /**
     * Used by application servers to register a game on the dispatcher. Clients can contact
     * the dispatcher to request a host.
     */
    public void registerGame( String gameUUID, String serverUUID ) throws RemoteException
    {
        gameServerMapping.put( gameUUID, serverUUID );
        if ( !serverGameMapping.containsKey( serverUUID ) )
            serverGameMapping.put( serverUUID, new ArrayList <>() );

        serverGameMapping.get( serverUUID ).add( gameUUID );
    }

    /**
     * Returns the server where a game is hosted.
     */
    public Server getServer( String gameUUID ) throws RemoteException, GameNotFoundException
    {
        String serverUUID = gameServerMapping.get( gameUUID );

        if ( serverUUID == null )
            throw new GameNotFoundException();

        return applicationServers.stream()
                .filter( server -> server.getUuid().equals( serverUUID ) )
                .findFirst()
                .orElseThrow( () -> new GameNotFoundException() );
    }

    /**
     * 1. Remove server A from available servers
     * 2. Send stop signal to server A:
     * a. Server refuses to accept new gameMoves(set isMyMove to false)
     * 3. Send load signal to server B:
     * a. Server transfers games and registers them in the dispatcher
     * 4. Send shutdown signal to server A:
     * a. Server throws WrongServerException, client connects to dispatcher and
     * connects to correct server.
     */
    private synchronized void transferServers( Server from, Server to )
    {
        LOGGER.info( "Initiating game transfer" );
        applicationServers.remove( from );

        LOGGER.info( from.toString() );
        LOGGER.info( to.toString() );

        try
        {
            Registry                serverARegistry = LocateRegistry.getRegistry( from.getHost(), from.getPort() );
            ServerManagementHandler serverA         = ( ServerManagementHandler ) serverARegistry.lookup( ServerManagementHandler.class.getName() );

            Registry                serverBRegistry = LocateRegistry.getRegistry( to.getHost(), to.getPort() );
            ServerManagementHandler serverB         = ( ServerManagementHandler ) serverBRegistry.lookup( ServerManagementHandler.class.getName() );

            LOGGER.info( "Preparing server a for shutdown" );
            serverA.prepareShutdown();

            LOGGER.info( "Transfering games to the second server" );
            serverB.loadFromServer( from, serverGameMapping.get( from.getUuid() ) );

            for ( String s : serverGameMapping.get( from.getUuid() ) )
                gameServerMapping.put( s, to.getUuid() );

            if ( !serverGameMapping.containsKey( to.getUuid() ) )
                serverGameMapping.put( to.getUuid(), new ArrayList <>() );

            serverGameMapping.get( to.getUuid() ).addAll( serverGameMapping.get( from.getUuid() ) );
            serverGameMapping.get( from.getUuid() ).clear();

            LOGGER.info( "Shutting down server" );
            serverA.shutDown();

        }
        catch ( RemoteException e )
        {
            e.printStackTrace();
        }
        catch ( NotBoundException e )
        {
            e.printStackTrace();
        }
    }

    public void updateTexturepack( String respack )
    {
        for( Server s : applicationServers )
        {
            try
            {
                Registry                serverARegistry = LocateRegistry.getRegistry( s.getHost(), s.getPort() );
                ServerManagementHandler server         = ( ServerManagementHandler ) serverARegistry.lookup( ServerManagementHandler.class.getName() );
                server.updateCurrentResourcepack( respack );
            }
            catch ( RemoteException e )
            {
                e.printStackTrace();
            }
            catch ( NotBoundException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void shutdownServer( String serverUuid ) throws Exception
    {
        Server from = null;

        Server to = null;
        for( Server s : applicationServers )
        {
            if( !s.getUuid().equals( serverUuid ) )
                to = s;
            else
                from = s;
        }

        if( from == null || to == null )
            throw new Exception( "Servers not found" );

        transferServers( from, to );
    }

    public Map <String, Integer> getPortsOnHosts()
    {
        return portsOnHosts;
    }

    public Set <Server> getApplicationServers()
    {
        return applicationServers;
    }

    public Set <Server> getDatabaseServers()
    {
        return databaseServers;
    }

    public Map <Server, List <Server>> getServerMapping()
    {
        return serverMapping;
    }

    public Map <Server, List <String>> getClientMapping()
    {
        return clientMapping;
    }

    public Map <String, String> getGameServerMapping()
    {
        return gameServerMapping;
    }

    public Map <String, List <String>> getServerGameMapping()
    {
        return serverGameMapping;
    }
}
