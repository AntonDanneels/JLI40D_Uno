package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Created by Anton D.
 */
public class GameSceneHandler
{
    private GameClient client;
    private GameHandler gameHandler;
    private Game game;

    @FXML
    private Canvas gameCanvas;

    public void init( GameClient client, GameHandler gameHandler )
    {
        this.client = client;
        this.gameHandler = gameHandler;
    }

    public void run()
    {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.fillText( "Game scene!", 50,50 );
    }

    public void setGame( Game game )
    {
        this.game = game;
    }
}
