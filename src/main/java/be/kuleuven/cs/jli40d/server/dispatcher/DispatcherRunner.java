package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
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
                String file =  new File(".").getAbsolutePath();
                file = file.substring( 0, file.length() - 1 ) + "db.jar";

                LOGGER.info( "Launching {}", file );

                Runtime.getRuntime().exec( new String[]{ "java", "-jar", file} );
            }
            catch ( IOException e )
            {
                LOGGER.error( "Failed to start db jar. filename should be {}", "db.jar" );
            }
        }
    }
}
