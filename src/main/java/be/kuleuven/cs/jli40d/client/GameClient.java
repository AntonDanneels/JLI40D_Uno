package be.kuleuven.cs.jli40d.client;/**
 * Created by Anton D. on 27/10/2017 using IntelliJ IDEA 14.0
 * Project: uno
 * Package: be.kuleuven.cs.jli40d.client
 */

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

public class GameClient extends Application
{
    private Logger LOGGER = LoggerFactory.getLogger( GameClient.class );
    private Stage primaryStage;
    private Scene loginScene, lobbyScene, gameScene;

    private String token;

    public static void main( String[] args )
    {
        launch( args );
    }

    @Override
    public void start( Stage primaryStage )
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle( "Uno" );

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

            lobbyScene = new Scene( lobbyPane );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            Utils.createPopup( "An unexpected error occurred" );
            LOGGER.debug( "Failed to load resource {}", e.getMessage() );
        }
        catch ( NotBoundException e )
        {
            e.printStackTrace();
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
        this.primaryStage.setScene( lobbyScene );
    }

    public String getToken()
    {
        return token;
    }

    public void setToken( String token )
    {
        this.token = token;
    }
}
