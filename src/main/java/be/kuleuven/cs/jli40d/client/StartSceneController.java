package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Anton D.
 */
public class StartSceneController
{
    private Logger LOGGER = LoggerFactory.getLogger( StartSceneController.class );

    private UserHandler userHandler;
    private GameClient client;

    public StartSceneController()
    {
    }

    public void init( GameClient client, UserHandler userHandler )
    {
        this.userHandler = userHandler;
        this.client = client;
    }

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    public void login()
    {
        LOGGER.debug( "Trying to login user" );
        try
        {
            String token = userHandler.login( usernameField.getText(), passwordField.getText() );

            LOGGER.debug( "Login succesful, token: {}", token );
            LOGGER.debug( "Switching to lobby scene" );
            client.setLobbyScene();
        }
        catch ( RemoteException e )
        {
            Utils.createPopup( "An unexpected error occured." );
            LOGGER.debug( "Unexpected remote exception: {}", e.getMessage() );
        }
        catch ( InvalidUsernameOrPasswordException e )
        {
            Utils.createPopup( "Invalid username or password." );
            LOGGER.debug( "User entered invalid username or password" );
        }
    }



    public void register()
    {
        LOGGER.debug( "Trying to register user" );
        try
        {
            String token = userHandler.register( "test@test.be", usernameField.getText(), passwordField.getText() );

            LOGGER.debug( "Register succesful, token: {}", token );
            LOGGER.debug( "Switching to lobby scene" );
            client.setLobbyScene();
        }
        catch ( RemoteException e )
        {
            Utils.createPopup( "An unexpected error occured." );
            LOGGER.debug( "Unexpected remote exception: {}", e.getMessage() );
        }

        catch ( AccountAlreadyExistsException e )
        {
            Utils.createPopup( "Account already exists." );
            LOGGER.debug( "User tried to register an already existing account" );
        }
    }
}
