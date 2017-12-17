package be.kuleuven.cs.jli40d.client;/**
 * Created by Anton D. on 27/10/2017 using IntelliJ IDEA 14.0
 * Project: uno
 * Package: be.kuleuven.cs.jli40d.client
 */

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.ResourceHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherRunner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

public class GameClient extends Application
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClient.class);

    private Stage primaryStage;
    private Scene loginScene, lobbyScene, spectateScene, gameScene, leaderboardScene;
    private LobbySceneHandler lobbySceneHandler;
    private GameSceneHandler gameSceneHandler;
    private SpectateSceneHandler spectateSceneHandler;
    private LeaderboardSceneHandler leaderboardSceneHandler;

    // TODO: find a better way to store these..
    private String token;
    private String username;
    private String uuid;

    public static void main( String[] args )
    {
        launch( args );
    }

    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle( "Uno" );
        this.primaryStage.getIcons().add(new Image("/icon.png")); //TODO: make this shit working
        this.primaryStage.setResizable( false );

        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/menu.fxml" ) );

            Pane pane = loader.load();

            loginScene = new Scene( pane );
            primaryStage.setScene( loginScene );
            primaryStage.show();

            Registry                  dispatcherRegistry  = LocateRegistry.getRegistry( DispatcherRunner.DISPATCHER.getHost(), DispatcherRunner.DISPATCHER.getPort() );
            ServerRegistrationHandler registrationHandler = (ServerRegistrationHandler)dispatcherRegistry.lookup( ServerRegistrationHandler.class.getName() );

            uuid = UUID.randomUUID().toString();

            Server appServer = registrationHandler.registerGameClient( uuid );

            Registry myRegistry = LocateRegistry.getRegistry( appServer.getHost(), appServer.getPort() );
            LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            UserHandler  userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );
            GameHandler  gameHandler  = ( GameHandler )myRegistry.lookup( GameHandler.class.getName() );
            ResourceHandler resourceHandler = (ResourceHandler) myRegistry.lookup( ResourceHandler.class.getName() );

            StartSceneController startSceneController = loader.getController();
            startSceneController.init( this, userManager );

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/lobby.fxml" ) );
            Pane lobbyPane = loader.load();

            LobbySceneHandler lobbySceneHandler = loader.getController();
            lobbySceneHandler.init( this, lobbyHandler, registrationHandler );
            this.lobbySceneHandler = lobbySceneHandler;

            lobbyScene = new Scene( lobbyPane );

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/game.fxml" ) );
            Pane gamePane = loader.load();

            GameSceneHandler gameSceneHandler = loader.getController();
            gameSceneHandler.init( this, lobbyHandler, gameHandler, registrationHandler, resourceHandler );
            this.gameSceneHandler = gameSceneHandler;

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/spectate.fxml" ) );
            Pane spectatePane = loader.load();

            SpectateSceneHandler spectateSceneHandler = loader.getController();
            spectateSceneHandler.init( this, lobbyHandler, gameHandler, registrationHandler );
            this.spectateSceneHandler = spectateSceneHandler;

            spectateScene = new Scene( spectatePane );

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/leaderboard.fxml" ) );
            Pane leaderboardPane = loader.load();

            leaderboardScene = new Scene( leaderboardPane );

            LeaderboardSceneHandler leaderboardSceneHandler = loader.getController();
            leaderboardSceneHandler.init( this, userManager );
            this.leaderboardSceneHandler = leaderboardSceneHandler;

            gameScene = new Scene( gamePane );

            loadImages( resourceHandler );
        }
        catch ( IOException e )
        {
            Utils.createPopup( "Failed to connect to the server" );
            LOGGER.debug( "Failed to load resource: {}", e.getMessage() );
        }
        catch ( NotBoundException e )
        {
            Utils.createPopup( "An unexpected error occurred" );
            LOGGER.debug( "Tried to load a non existing registry item: {}", e.getMessage() );
        }

        primaryStage.setOnCloseRequest( e ->
        {
            Platform.exit();
            System.exit( 0 );
        } );
    }

    private void loadImages( ResourceHandler resourceHandler )
    {
        try
        {
            ImageLoader.loadImages( resourceHandler );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }

    public void resetConnection( Server server )
    {
        try
        {
            Registry registry = LocateRegistry.getRegistry( server.getHost(), server.getPort() );

            LobbyHandler lobbyHandler = ( LobbyHandler )registry.lookup( LobbyHandler.class.getName() );
            UserHandler  userManager  = ( UserHandler )registry.lookup( UserHandler.class.getName() );
            GameHandler  gameHandler  = ( GameHandler )registry.lookup( GameHandler.class.getName() );

            lobbySceneHandler.setLobbyHandler( lobbyHandler );

            gameSceneHandler.setGameHandler( gameHandler );
            gameSceneHandler.setLobbyHandler( lobbyHandler );

            spectateSceneHandler.setGameHandler( gameHandler );
            spectateSceneHandler.setLobbyHandler( lobbyHandler );

            leaderboardSceneHandler.setUserHandler( userManager );
        }
        catch ( RemoteException e )
        {
            e.printStackTrace();
        }
        catch ( NotBoundException e )
        {
            e.printStackTrace();
        }

    }

    public void setStartScene()
    {
        this.primaryStage.setScene( loginScene );
    }

    public void setLobbyScene()
    {
        lobbySceneHandler.refresh();
        this.primaryStage.setScene( lobbyScene );
    }

    public void setGameScene( GameSummary game )
    {
        gameSceneHandler.setGameSummary( game );
        this.primaryStage.setScene( gameScene );
        gameSceneHandler.run();
    }

    public void setSpectatingScene( GameSummary game )
    {
        spectateSceneHandler.setGameSummary( game );
        this.primaryStage.setScene( spectateScene );
        spectateSceneHandler.run();
    }

    public void setLeaderboardScene()
    {
        leaderboardSceneHandler.refresh();
        this.primaryStage.setScene( leaderboardScene );
    }

    public String getToken()
    {
        return token;
    }

    public void setToken( String token )
    {
        this.token = token;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public String getUuid()
    {
        return uuid;
    }
}
