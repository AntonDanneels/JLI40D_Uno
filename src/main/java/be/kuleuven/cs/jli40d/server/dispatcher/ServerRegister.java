package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Pieter
 * @version 1.0
 */
public class ServerRegister extends UnicastRemoteObject implements ServerRegistrationHandler, Serializable
{
    private final Logger LOGGER = LoggerFactory.getLogger( ServerRegister.class.getName() );

    private static final int MIN_PORT = 1101;
    private static final int MAX_PORT = 1200;
    private static final int DATABASE_SERVER = 3;

    private Map<String, AtomicInteger> portsOnHosts;

    private Set<Server> applicationServers;
    private Set<Server> databaseServers;

    // Contains the mapping between application and database servers
    private Map<Server, List<Server>> serverMapping;

    public ServerRegister() throws RemoteException
    {
        this.portsOnHosts = new HashMap<>();
        this.applicationServers = new HashSet <>();
        this.databaseServers = new HashSet <>();
        this.serverMapping = new HashMap <>();
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
    public Server obtainPort( String host, ServerType serverType ) throws RemoteException
    {
        LOGGER.info( "Server obtaining port: " + host + " for servertype: " + serverType );
        int port = MIN_PORT;

        if ( portsOnHosts.containsKey( host ) )
        {
            port = portsOnHosts.get( host ).getAndAdd( 1 );
        }
        else
        {
            portsOnHosts.put( host, new AtomicInteger( port ) );
        }

        Server server = new Server( host, port, serverType );

        applicationServers.add( server );

        return server;
    }

    /**
     * After the deployer is finished booting, has obtained a port and has registered
     * his RMI server, this function can be called.
     * <p>
     * It provides al list with all known other servers. This is blocking and
     * will only return when all expected servers have also called this function and
     * are registered on the deployer.
     *
     * @param self The {@link Server} object given by {@link #obtainPort(String, ServerType)}.
     * @return A set of all {@link Server} objects, including the caller itself.
     * @throws RemoteException
     */
    @Override
    public synchronized Set<Server> registerDatabase( Server self ) throws RemoteException
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
     * @param self The {@link Server} object given by {@link #obtainPort(String, ServerType)}.
     * @return A database {@link Server} for the application server to connect to.
     * @throws RemoteException
     */
    public synchronized Server registerAppServer( Server self ) throws RemoteException
    {
        LOGGER.info( "Registring application server: " + self.getHost() + ":" + self.getPort() );

        applicationServers.add( self );

        while (databaseServers.size() < 1)
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

        Server lowestLoad = null;
        int lowestAmount = Integer.MAX_VALUE;
        for( Object key : serverMapping.keySet() )
        {
            if( serverMapping.get( (Server) key ).size() < lowestAmount )
            {
                lowestLoad = (Server)key;
                lowestAmount = serverMapping.get( lowestLoad ).size();
            }
        }

        serverMapping.get( lowestLoad ).add( self );

        return lowestLoad;
    }
}
