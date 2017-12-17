package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.*;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
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

    public static boolean IS_RUNNING = false;
    public static boolean IS_SHUTTING_DOWN = false;

    public static void main( String[] args )
    {
        try
        {
            ServerRegistrationHandler registrationHandler = null;

            try
            {
                Registry dispatcherRegistry = LocateRegistry.getRegistry( DispatcherMain.DISPATCHER.getHost(), DispatcherMain.DISPATCHER.getPort() );
                registrationHandler = ( ServerRegistrationHandler )dispatcherRegistry.lookup( ServerRegistrationHandler.class.getName() );
            }
            catch ( RemoteException e )
            {
                LOGGER.error( "Failed to connect to dispatcher {}. Check these settings.", DispatcherMain.DISPATCHER );
            }


            Server me = registrationHandler.obtainPort( "localhost", ServerType.APPLICATION );

            Server db = registrationHandler.registerAppServer( me );

            //remote db
            Registry            myRegistry          = LocateRegistry.getRegistry( db.getHost(), db.getPort() );
            DatabaseUserHandler databaseUserHandler = ( DatabaseUserHandler )myRegistry.lookup( DatabaseUserHandler.class.getName() );
            DatabaseGameHandler databaseGameHandler = ( DatabaseGameHandler )myRegistry.lookup( DatabaseGameHandler.class.getName() );

            //services
            CachedUserManager userManager = new CachedUserManager( databaseUserHandler );
            RemoteGameService gameService = new RemoteGameService( databaseGameHandler, me );

            GameManager  gameManager = new GameManager( userManager, gameService );
            LobbyHandler lobby       = new Lobby( userManager, gameService, registrationHandler, me );

            ResourceHandler resourceHandler = new ResourceManager();

            ServerManager serverManager = new ServerManager( gameService, databaseGameHandler );

            Registry server = LocateRegistry.createRegistry( me.getPort() );
            server.rebind( LobbyHandler.class.getName(), lobby );
            server.rebind( UserHandler.class.getName(), userManager );
            server.rebind( GameHandler.class.getName(), gameManager );
            server.rebind( ResourceHandler.class.getName(), resourceHandler );
            server.rebind( ServerManagementHandler.class.getName(), serverManager );

            IS_RUNNING = true;

            LOGGER.info( "Application server started with following bindings: {} ", Arrays.toString( server.list() ) );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            LOGGER.error( "Error while creating a registry. {}", e.getMessage() );
        }
    }

}
