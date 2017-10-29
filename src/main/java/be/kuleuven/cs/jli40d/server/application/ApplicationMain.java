package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

/**
 * The class with the main method needed to launch the application server.
 *
 * @author Pieter
 * @version 1.0
 */
public class ApplicationMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ApplicationMain.class );

    public static void main( String[] args )
    {
        try
        {
            //services
            SimpleUserManager userManager = new SimpleUserManager();
            GameManager gameManager = new GameManager( userManager );
            LobbyHandler lobby = new Lobby( userManager, gameManager );

            // create on port 1099
            Registry registry = LocateRegistry.createRegistry( 1099 );
            // create a new service named CounterService
            registry.rebind( LobbyHandler.class.getName(), lobby );
            registry.rebind( UserHandler.class.getName(), userManager );
            registry.rebind( GameHandler.class.getName(), gameManager );

            LOGGER.info( "Application server started with following bindings: {} ", Arrays.toString( registry.list() ) );

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
    }

}
