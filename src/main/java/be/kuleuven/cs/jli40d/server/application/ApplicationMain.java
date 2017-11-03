package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
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
            //remote db
            Registry            myRegistry          = LocateRegistry.getRegistry( "localhost", 1100 );
            DatabaseUserHandler databaseUserHandler = ( DatabaseUserHandler )myRegistry.lookup( DatabaseUserHandler.class.getName() );
            DatabaseGameHandler databaseGameHandler = ( DatabaseGameHandler )myRegistry.lookup( DatabaseGameHandler.class.getName() );


            //services
            RemoteUserManager userManager = new RemoteUserManager( databaseUserHandler );
            RemoteGameService gameService = new RemoteGameService( databaseGameHandler );

            GameManager       gameManager = new GameManager( userManager, gameService );
            LobbyHandler      lobby       = new Lobby( userManager, gameService );

            // create on port 1099
            Registry server = LocateRegistry.createRegistry( 1099 );
            // create a new service named CounterService
            server.rebind( LobbyHandler.class.getName(), lobby );
            server.rebind( UserHandler.class.getName(), userManager );
            server.rebind( GameHandler.class.getName(), gameManager );

            LOGGER.info( "Application server started with following bindings: {} ", Arrays.toString( server.list() ) );

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
    }

}
