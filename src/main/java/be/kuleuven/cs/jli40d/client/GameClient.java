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
    private Stage primaryStage;

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

            Scene scene = new Scene( pane );
            primaryStage.setScene( scene );
            primaryStage.show();

            String host = "localhost";
            int    port = 1099;

            Registry myRegistry;

            myRegistry = LocateRegistry.getRegistry( host, port );
            final LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            final UserHandler  userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );
            final GameHandler  gameHandler  = ( GameHandler )myRegistry.lookup( GameHandler.class.getName() );

            StartSceneController startSceneController = loader.getController();
            startSceneController.init( this, userManager );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        catch ( NotBoundException e )
        {
            e.printStackTrace();
        }

    }
}
