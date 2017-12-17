package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Pieter
 * @version 1.0
 */
public class DispatcherRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherRunner.class);

    public static final Server DISPATCHER = new Server( "localhost", 1100, ServerType.DISPATCHER, UUID.randomUUID().toString() );

    DispatcherRunner()
    {

        try
        {
            ServerRegister serverRegister = new ServerRegister();

            Registry server = LocateRegistry.createRegistry( DISPATCHER.getPort() );
            server.rebind( ServerRegistrationHandler.class.getName(), serverRegister );

            LOGGER.info( "Dispatcher server started with following bindings: {} ", Arrays.toString( server.list() ) );

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
    }
}
