package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToJoinGameException;
import com.sun.corba.se.pept.transport.ListenerThread;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Queue;

/**
 * Created by Anton D.
 */
public class GameSceneHandler extends AnimationTimer
{
    private Logger LOGGER = LoggerFactory.getLogger( GameSceneHandler.class );

    private GameClient client;
    private LobbyHandler lobbyHandler;
    private GameHandler gameHandler;
    private Game game;
    private ListenerService listenerService;
    private Queue<GameMove> gameMoves;

    private boolean mouseDown = false;
    private double mousePosX = 0.0;
    private double mousePosY = 0.0;

    @FXML
    private Canvas gameCanvas;
    private GraphicsContext gc;

    public GameSceneHandler()
    {
    }

    public void init( GameClient client, LobbyHandler lobbyHandler, GameHandler gameHandler )
    {
        this.client = client;
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;

        gameCanvas.setOnMouseDragEntered( e -> { mouseDown = true; } );
        gameCanvas.setOnMouseDragExited( e -> { mouseDown = false; } );
        gameCanvas.setOnMouseMoved( e -> { mousePosX = e.getX(); mousePosY = e.getY(); } );

        gc = gameCanvas.getGraphicsContext2D();
    }

    public void run()
    {
        LOGGER.debug( "Entering gameloop" );

        final String msg = "Joining the game";
        final Text text = new Text(msg);
        text.setFont(gc.getFont());
        final double width = text.getLayoutBounds().getWidth();
        gc.fillText( msg, gameCanvas.getWidth() / 2 - width / 2, 50 );

        // TODO add rotating card here

        new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    game = lobbyHandler.joinGame( client.getToken(), game.getGameID() );
                    enterGameLoop();
                }
                catch ( RemoteException e )
                {
                    Utils.createPopup( "An unexpected error occured." );
                    LOGGER.debug( "Unexpected remote exception: {}", e.getMessage() );
                }
                catch ( UnableToJoinGameException e )
                {
                    Utils.createPopup( "Unable to join game." );
                    LOGGER.debug( "Unable to join game with id {}: {}", game.getGameID(), e.getMessage() );
                    client.setLobbyScene();
                }
                catch ( InvalidTokenException e )
                {
                    Utils.createPopup( "Something went wrong, please login again." );
                    LOGGER.debug( "Invalid token: {}", e.getMessage() );
                    client.setStartScene();
                }
            }
        } ).start();
    }

    private void enterGameLoop()
    {
        listenerService = new ListenerService( gameHandler, client.getToken(), game, gameMoves );
        new Thread( listenerService ).start();
        this.start();
    }

    public synchronized void handle( long now )
    {
        gc.clearRect( 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight() );
        gc.fillText( "Game " + game.getGameID(), 10, 10 );

        try
        {
            if( gameHandler.myTurn( client.getToken(), game.getGameID() ) )
            {
                gc.fillText( "It is my turn", 50, 50 );
            }
            else
            {
                gc.fillText( "Waiting for the other players", 50,50 );
            }

            int x = 50;
            int y = 75;
            List<Card> cards = game.getCardsPerPlayer().get( client.getUsername() );
            for( Card c : cards )
            {
                drawCard( c, x, y, 10,10 );
                y += 25;
            }
        }
        catch ( InvalidTokenException e )
        {
            e.printStackTrace();
        }
        catch ( RemoteException e )
        {
            e.printStackTrace();
        }
        catch ( GameNotFoundException e )
        {
            e.printStackTrace();
        }
    }

    private void drawCard( Card c, int x, int y, int w, int h )
    {
        gc.fillText( c.getColour() + ":" + c.getType() , x, y );
    }

    public void setGame( Game game )
    {
        this.game = game;
    }
}
