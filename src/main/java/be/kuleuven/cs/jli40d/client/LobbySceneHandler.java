package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToCreateGameException;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Anton D.
 */
public class LobbySceneHandler
{
    private Logger LOGGER = LoggerFactory.getLogger( LobbySceneHandler.class );

    private GameClient   client;
    private LobbyHandler lobbyHandler;
    private ServerRegistrationHandler registrationHandler;

    @FXML
    private ListView<GameSummary> gamesList;

    public LobbySceneHandler()
    {
    }

    public void init( GameClient client, LobbyHandler lobbyHandler, ServerRegistrationHandler registrationHandler )
    {
        this.client = client;
        this.lobbyHandler = lobbyHandler;
        this.registrationHandler = registrationHandler;
    }

    public void createNewGame()
    {
        LOGGER.debug( "Showing dialog to create game" );

        Stage myDialog = new Stage();
        myDialog.initModality( Modality.WINDOW_MODAL );
        myDialog.setTitle( "Enter game info" );

        Button            okButton = new Button( "Create game" );
        TextField         gameName = new TextField( "Game name" );
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.getItems().addAll( 2, 3, 4 );
        comboBox.setValue( 4 );

        VBox dialogBox = new VBox( 20 );
        dialogBox.getChildren().add( gameName );
        dialogBox.getChildren().add( comboBox );
        dialogBox.getChildren().add( okButton );

        Scene myDialogScene = new Scene( dialogBox, 300, 200 );

        okButton.setOnAction( e ->
        {
            createNewGame( gameName.getText(), comboBox.getValue() );
            myDialog.close();
        } );

        myDialog.setScene( myDialogScene );
        myDialog.show();
    }

    private void createNewGame( String gameName, int nrOfPlayers )
    {
        LOGGER.debug( "Creating game with name {} and {} players ", gameName, nrOfPlayers );
        try
        {
            String uuid = lobbyHandler.makeGame( client.getToken(), gameName, nrOfPlayers );
            LOGGER.debug( "Succesfully created a game with ID {}", uuid );
            refresh();
        }
        catch ( RemoteException e )
        {
            Utils.createPopup( "An unexpected error occurred" );
            LOGGER.debug( "Remote exception: {}", e.getMessage() );

            try
            {
                client.resetConnection( registrationHandler.registerGameClient( client.getUuid() ) );
                this.createNewGame( gameName, nrOfPlayers);
            }
            catch ( RemoteException e1 )
            {
                e1.printStackTrace();
            }

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
        catch ( WrongServerException e )
        {
            LOGGER.debug( "Changing server" );

            try
            {
                registrationHandler.unregisterGameClient( client.getServer(), client.getServer().getUuid() );
                Server s = registrationHandler.registerGameClient( client.getUuid() );
                client.resetConnection( s );
                createNewGame( gameName, nrOfPlayers );
            }
            catch ( RemoteException e1 )
            {
                e1.printStackTrace();
            }
        }
    }

    public void showLeaderboard()
    {
        client.setLeaderboardScene();
    }

    public void refresh()
    {
        try
        {
            List<GameSummary> games = lobbyHandler.currentGames( client.getToken() );
            gamesList.getItems().clear();

            gamesList.setCellFactory( new Callback<ListView<GameSummary>, ListCell<GameSummary>>()
            {
                public ListCell<GameSummary> call( ListView<GameSummary> param )
                {
                    return new GameCell( client );
                }
            } );

            for ( GameSummary game : games )
                gamesList.getItems().add( game );
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
        catch ( WrongServerException e )
        {
            LOGGER.debug( "Changing server" );
        }
    }

    public void setLobbyHandler( LobbyHandler lobbyHandler )
    {
        this.lobbyHandler = lobbyHandler;
    }
}
