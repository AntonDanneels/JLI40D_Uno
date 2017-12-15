package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;
import be.kuleuven.cs.jli40d.server.application.Lobby;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Anton D.
 */
public class LeaderboardSceneHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderboardSceneHandler.class);

    @FXML
    private ListView scoresList;

    private GameClient client;
    private UserHandler userHandler;

    public LeaderboardSceneHandler()
    {
    }

    public void init( GameClient client, UserHandler userHandler )
    {
        this.client = client;
        this.userHandler = userHandler;
    }

    public void refresh()
    {
        LOGGER.debug( "Switched to leaderboard scene" );
        LOGGER.debug( "Fetching latest scores" );

        try
        {
            List<Pair<String, Long>> list = userHandler.getUserScores();

            scoresList.getItems().clear();

            scoresList.setCellFactory( new Callback<ListView<Pair<String, Long>>, ListCell<Pair<String, Long>>>()
            {
                public ListCell<Pair<String, Long>> call( ListView<Pair<String, Long>> param )
                {
                    return new LeaderboardCell();
                }
            } );

            scoresList.getItems().addAll( list );

            LOGGER.debug( "Got latest scores, rendering." );
        }
        catch ( RemoteException e )
        {
            LOGGER.debug( "Failed to get latest user scores: {}", e.getMessage() );
            Utils.createPopup( "Failed to get the latest scores." );
        }
        catch ( WrongServerException e )
        {
            LOGGER.debug( "Changing server" );
            client.resetConnection( e );
        }
    }

    public void backToLobby()
    {
        client.setLobbyScene();
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }
}
