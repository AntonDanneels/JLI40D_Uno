package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToCreateGameException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Anton D.
 */
public class LobbySceneHandler
{
    private Logger LOGGER = LoggerFactory.getLogger( LobbySceneHandler.class );

    @FXML
    private Button newGameButton;

    private GameClient       client;
    private LobbyHandler lobbyHandler;

    public LobbySceneHandler()
    {

    }

    public void init( GameClient client, LobbyHandler lobbyHandler )
    {
        this.client = client;
        this.lobbyHandler = lobbyHandler;
    }

    public void createNewGame()
    {
        LOGGER.debug( "Creating game" );

        try
        {
            // TODO: create popup asking for info
            lobbyHandler.makeGame( client.getToken(), "", 4 );
        }
        catch ( RemoteException e )
        {
            Utils.createPopup( "An unexpected error occurred" );
            LOGGER.debug( "Remote exception: {}", e.getMessage() );
        }
        catch ( InvalidTokenException e )
        {
            Utils.createPopup( "Invalid login credentials, please login again" );
            client.setStartScene();
            LOGGER.debug( "User has an invalid token: {}", e.getMessage() );
        }
        catch ( UnableToCreateGameException e )
        {
            Utils.createPopup( "An error occurred when trying to create your game, please try again." );
            LOGGER.debug( "Error while creating game: {}", e.getMessage() );
        }
    }
}
