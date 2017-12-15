package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Pieter
 * @version 1.0
 */
public class ServerRegister implements ServerRegistrationHandler
{
    private static final int MIN_PORT        = 1101;
    private static final int MAX_PORT        = 1200;
    private static final int DATABASE_SERVER = 3;

    private Map<String, AtomicInteger> portsOnHosts;

    private Set<Server> applicationServers;
    private Set<Server> databaseServers;

    public ServerRegister()
    {
        this.portsOnHosts = new HashMap<>();
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
    public synchronized Set<Server> register( Server self ) throws RemoteException
    {
        if ( self.getServerType() == ServerType.DATABASE )
        {
            databaseServers.add( self );
            notifyAll();
        } else if (self.getServerType() == ServerType.APPLICATION)
        {
            applicationServers.add( self );
        }

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
}
