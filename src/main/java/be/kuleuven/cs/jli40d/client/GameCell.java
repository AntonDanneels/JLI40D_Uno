package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.model.Game;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Anton D.
 */
public class GameCell extends ListCell<Game>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCell.class);

    private HBox hBox = new HBox();
    private Pane pane = new Pane();
    private Pane pane1 = new Pane();
    private Label gameNameLabel = new Label( "" );
    private Label nrOfPlayersLabel = new Label( "" );
    private Button joinButton = new Button( "Join" );
    private Button viewButton = new Button( "View" );

    private Game game;

    public GameCell( GameClient client )
    {
        super();
        hBox.getChildren().addAll( gameNameLabel, pane, nrOfPlayersLabel, pane1, joinButton, viewButton );
        HBox.setHgrow( pane, Priority.ALWAYS );
        HBox.setHgrow( pane1, Priority.ALWAYS );
        joinButton.setOnAction( e ->
        {
            LOGGER.debug( "Joining game: {}", game.getGameID() );
            LOGGER.debug( "Switching to the game scene." );
            client.setGameScene( game );
        });
    }

    protected void updateItem( Game game, boolean empty )
    {
        super.updateItem( game, empty );
        setText( null );
        if ( empty )
        {
            this.game = null;
            setGraphic( null );
        }
        else
        {
            this.game = game;
            gameNameLabel.setText( "TODO: create actual fucking name attribute" );
            nrOfPlayersLabel.setText( "Players: " + game.getNumberOfJoinedPlayers() + "/" + game.getMaximumNumberOfPlayers() );
            setGraphic( hBox );
        }
    }
}
