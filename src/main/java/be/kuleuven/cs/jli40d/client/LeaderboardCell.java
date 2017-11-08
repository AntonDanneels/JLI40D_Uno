package be.kuleuven.cs.jli40d.client;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Anton D.
 */
public class LeaderboardCell extends ListCell<Pair<String, Long>>
{
    private HBox  hBox          = new HBox();
    private Pane  pane          = new Pane();
    private Label usernameLabel = new Label( "" );
    private Label scoreLabel    = new Label( "" );

    public LeaderboardCell( )
    {
        super();
        hBox.getChildren().addAll( usernameLabel, pane, scoreLabel );
        HBox.setHgrow( pane, Priority.ALWAYS );
    }

    protected void updateItem( Pair<String, Long> entry, boolean empty )
    {
        super.updateItem( entry, empty );
        setText( null );
        if ( empty )
        {
            setGraphic( null );
        }
        else
        {
            usernameLabel.setText( entry.getKey() );
            scoreLabel.setText( "" + entry.getValue() );
            setGraphic( hBox );
        }
    }
}
