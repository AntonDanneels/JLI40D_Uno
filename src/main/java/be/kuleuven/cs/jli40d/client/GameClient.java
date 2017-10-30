package be.kuleuven.cs.jli40d.client;/**
 * Created by Anton D. on 27/10/2017 using IntelliJ IDEA 14.0
 * Project: uno
 * Package: be.kuleuven.cs.jli40d.client
 */

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class GameClient extends Application
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GameClient.class);

    private Stage primaryStage;
    private Scene loginScene, lobbyScene, gameScene;
    private LobbySceneHandler lobbySceneHandler;
    private GameSceneHandler gameSceneHandler;

    // TODO: find a better way to store these..
    private String token;
    private String username;

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

        try
        {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/menu.fxml" ) );

            Pane pane = loader.load();

            loginScene = new Scene( pane );
            primaryStage.setScene( loginScene );
            primaryStage.show();

            String host = "localhost";
            int    port = 1099;

            Registry myRegistry = LocateRegistry.getRegistry( host, port );
            LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            UserHandler  userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );
            GameHandler  gameHandler  = ( GameHandler )myRegistry.lookup( GameHandler.class.getName() );

            StartSceneController startSceneController = loader.getController();
            startSceneController.init( this, userManager );

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/lobby.fxml" ) );
            Pane lobbyPane = loader.load();

            LobbySceneHandler lobbySceneHandler = loader.getController();
            lobbySceneHandler.init( this, lobbyHandler );
            this.lobbySceneHandler = lobbySceneHandler;

            lobbyScene = new Scene( lobbyPane );

            loader = new FXMLLoader();
            loader.setLocation( getClass().getResource( "/game.fxml" ) );
            Pane gamePane = loader.load();

            GameSceneHandler gameSceneHandler = loader.getController();
            gameSceneHandler.init( this, lobbyHandler, gameHandler );
            this.gameSceneHandler = gameSceneHandler;

            gameScene = new Scene( gamePane );

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

    public void setGameScene( Game game )
    {
        gameSceneHandler.setGame( game );
        this.primaryStage.setScene( gameScene );
        gameSceneHandler.run();
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
}
