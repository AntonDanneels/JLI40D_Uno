package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * @author Pieter
 * @version 1.0
 */
@Component
public class DispatcherRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DispatcherRunner.class );

    public static final String JAR_NAME = "target/uno-1.0-SNAPSHOT-jar-with-dependencies.jar";

    private final ServerRegister serverRegister;

    @Autowired
    DispatcherRunner( ServerRegister serverRegister )
    {
        try
        {
            Registry server = LocateRegistry.createRegistry( DispatcherMain.DISPATCHER.getPort() );
            server.rebind( ServerRegistrationHandler.class.getName(), serverRegister );

            LOGGER.info( "Dispatcher server started with following bindings: {} ", Arrays.toString( server.list() ) );

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
        this.serverRegister = serverRegister;

        for (int i = 0; i < ServerRegister.DATABASE_SERVER; i++)
        {
            try
            {
                Runtime.getRuntime().exec( new String[]{ "java", "-cp", JAR_NAME, "be.kuleuven.cs.jli40d.server.db.DBMain" } );
            }
            catch ( IOException e )
            {
                LOGGER.error( "Failed to start db jar. filename should be {}", JAR_NAME );
            }
        }
    }
}
