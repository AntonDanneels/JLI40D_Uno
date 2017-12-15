package be.kuleuven.cs.jli40d.server.db;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Set;

/**
 * @author Pieter
 * @version 1.0
 */
@Component
public class DatabaseRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DatabaseRunner.class );

    private DatabaseGameHandler gameHandler;
    private DatabaseUserHandler userHandler;

    @Autowired
    public DatabaseRunner( DatabaseGameHandler gameHandler, DatabaseUserHandler userHandler )
    {
        this.gameHandler = gameHandler;
        this.userHandler = userHandler;

        try
        {
            Registry                  dispatcherRegistry  = LocateRegistry.getRegistry( DispatcherMain.DISPATCHER.getHost(), DispatcherMain.DISPATCHER.getPort() );
            ServerRegistrationHandler registrationHandler = (ServerRegistrationHandler)dispatcherRegistry.lookup( ServerRegistrationHandler.class.getName() );

            LOGGER.info( "Obtaining port." );

            Server me = registrationHandler.obtainPort( "localhost", ServerType.DATABASE );

            LOGGER.info( "Obtained port {}, now registering database." );

            Set<Server> dbs = registrationHandler.registerDatabase( me );

            LOGGER.info( "Received {} databases. Now creating registry.", dbs.size() );

            Registry registry = LocateRegistry.createRegistry( me.getPort() );

            registry.rebind( DatabaseGameHandler.class.getName(), gameHandler );
            registry.rebind( DatabaseUserHandler.class.getName(), userHandler );


            LOGGER.info( "DB server started with following bindings: {} ", Arrays.toString( registry.list() ) );

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
    }

}
