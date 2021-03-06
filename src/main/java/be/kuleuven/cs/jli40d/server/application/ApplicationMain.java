package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.*;
import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.CardColour;
import be.kuleuven.cs.jli40d.core.model.CardType;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.server.application.service.RemoteGameService;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public static boolean IS_RUNNING       = false;
    public static boolean IS_SHUTTING_DOWN = false;

    public static void main( String[] args )
    {

        if ( args.length == 0 )
        {
            LOGGER.error( "Launch with java -jar app.jar <ip> <ip-dispatcher>." );
            System.exit( 1 );
        }

        Server dispatcher = new Server( args[1], DispatcherMain.DISPATCHER.getPort(), DispatcherMain.DISPATCHER.getServerType(), DispatcherMain.DISPATCHER.getUuid() );

        LOGGER.info( "Launching to dispatcher {} with app server {}", dispatcher, args[0] );

        try
        {
            LOGGER.info( "Testing folders." );
            File unoDir = new File( System.getProperty( "user.home" ) + "/" + "uno" );
            if( !unoDir.exists() )
            {
                LOGGER.info( "Making uno folder." );
                unoDir.mkdirs();
            }

            File serverDir = new File( System.getProperty( "user.home" ) + "/" + "uno"  + "/" + "server_texturepacks" );
            if( !serverDir.exists() )
            {
                LOGGER.info( "Making server folder" );
                serverDir.mkdir();
            }

            Game game = new Game( 4 );
            GameLogic.generateDeck( game );
            game.getDeck().add( new Card( CardType.PLUS4, CardColour.GREEN ) );
            game.getDeck().add( new Card( CardType.PLUS4, CardColour.RED ) );
            game.getDeck().add( new Card( CardType.PLUS4, CardColour.BLUE ) );
            game.getDeck().add( new Card( CardType.PLUS4, CardColour.YELLOW ) );
            game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.GREEN ) );
            game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.RED ) );
            game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.BLUE ) );
            game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.YELLOW ) );

            for( Card c : game.getDeck() )
            {
                BufferedImage img = ImageIO.read( Thread.currentThread().getContextClassLoader().getResource( "default_texturepack/" + c.getType() + "_" + c.getColour() + ".png" ) );
                ImageIO.write( img, "png", new File( serverDir, c.getType() + "_" + c.getColour() + ".png" ) );
            }

            BufferedImage img = ImageIO.read( Thread.currentThread().getContextClassLoader().getResource( "default_texturepack/CARD_BACK.png" ) );
            ImageIO.write( img, "png", new File( serverDir, "CARD_BACK.png" ) );

            LOGGER.info( "Wrote files to serving folder" );

            ServerRegistrationHandler registrationHandler = null;

            try
            {
                Registry dispatcherRegistry = LocateRegistry.getRegistry( dispatcher.getHost(), dispatcher.getPort() );
                registrationHandler = ( ServerRegistrationHandler ) dispatcherRegistry.lookup( ServerRegistrationHandler.class.getName() );
            }
            catch ( RemoteException e )
            {
                LOGGER.error( "Failed to connect to dispatcher {}. Check these settings.", dispatcher );
            }


            Server me = registrationHandler.obtainPort( args[0], ServerType.APPLICATION );

            Server db = registrationHandler.registerAppServer( me );

            //remote db
            Registry            myRegistry          = LocateRegistry.getRegistry( db.getHost(), db.getPort() );
            DatabaseUserHandler databaseUserHandler = ( DatabaseUserHandler ) myRegistry.lookup( DatabaseUserHandler.class.getName() );
            DatabaseGameHandler databaseGameHandler = ( DatabaseGameHandler ) myRegistry.lookup( DatabaseGameHandler.class.getName() );

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
