package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidGameMoveException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

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

    private List<CardButton> cardButtons;

    @FXML
    private Canvas gameCanvas;
    private GraphicsContext gc;

    private Player me;

    public GameSceneHandler()
    {
        cardButtons = new ArrayList<>();
        gameMoves = new ConcurrentLinkedDeque<>();
    }

    public void init( GameClient client, LobbyHandler lobbyHandler, GameHandler gameHandler )
    {
        this.client = client;
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;

        gameCanvas.setOnMousePressed( e -> { mouseDown = true; } );
        gameCanvas.setOnMouseReleased( e -> { mouseDown = false; } );
        gameCanvas.setOnMouseMoved( e -> { mousePosX = e.getX(); mousePosY = e.getY(); } );
        gameCanvas.setOnMouseDragged( e -> {  mousePosX = e.getX(); mousePosY = e.getY(); } );

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

    private synchronized void enterGameLoop()
    {
        listenerService = new ListenerService( gameHandler, client.getToken(), game, gameMoves );

        for( Player p : game.getPlayers() )
        {
            if(p.getUsername().equals( client.getUsername() ))
                me = p;
        }

        int x = 50;
        int y = 75;
        List<Card> cards = game.getCardsPerPlayer().get( client.getUsername() );
        for( Card c : cards )
        {
            cardButtons.add( new CardButton( x, y, 150, 20, c ) );
            y += 25;
        }

        new Thread( listenerService ).start();
        this.start();
    }

    private CardButton selectedCardButton = null;

    public synchronized void handle( long now )
    {
        gc.clearRect( 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight() );
        gc.fillText( "Mouse " + mouseDown + " , " + mousePosX + " , " + mousePosY, 10, 10 );

        try
        {
            while( gameMoves.peek() != null )
            {
                GameMove move = gameMoves.poll();
                GameLogic.applyMove( game, move );
                game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
                layoutCards();

                // TODO create animation
            }

            gc.strokeRect( 600, 50, 200, 50 );
            gc.fillText( "Draw card", 605, 512 );

            if( gameHandler.myTurn( client.getToken(), game.getGameID() ) )
            {
                gc.fillText( "It is my turn", 50, 50 );
                if ( mouseDown )
                {
                    if( Utils.intersects( (int) mousePosX, (int) mousePosY, 1,1, 600, 50, 200, 50 ) )
                    {
                        GameMove move = new GameMove( game.getCurrentGameMoveID(), me, null, true );

                        if( GameLogic.testMove( game, move ) )
                        {
                            LOGGER.debug( "Sending gamemove to draw card" );
                            gameHandler.sendMove( client.getToken(), game.getGameID(), move );
                        }
                    }
                    else if( selectedCardButton == null )
                    {
                        for ( CardButton b : cardButtons )
                        {
                            if ( b.isIn( mousePosX, mousePosY ) )
                            {
                                selectedCardButton = b;
                                break;
                            }
                        }
                    }
                    else
                    {
                        selectedCardButton.setX( (int)mousePosX - selectedCardButton.getW() / 2 );
                        selectedCardButton.setY( (int)mousePosY - selectedCardButton.getH() / 2 );
                    }
                }
                else
                {
                    if ( selectedCardButton != null )
                    {
                        if ( Utils.intersects( selectedCardButton.getX(), selectedCardButton.getY(), selectedCardButton.getW(), selectedCardButton.getH(), 400, 200, 200, 50 ) )
                        {
                            GameMove move = new GameMove( game.getCurrentGameMoveID(), me, selectedCardButton.getC(), false );

                            if( GameLogic.testMove( game, move ) )
                            {
                                LOGGER.debug( "Sending gamemove with played card {}:{}", selectedCardButton.getC().getType(), selectedCardButton.getC().getColour() );
                                gameHandler.sendMove( client.getToken(), game.getGameID(), move );
                            }
                        }
                    }

                    selectedCardButton = null;
                    layoutCards();
                }
            }
            else
            {
                gc.fillText( "Waiting for the other players", 50,50 );
            }

            // TODO proper drop area
            gc.strokeRect( 400, 200, 200, 50 );
            Card c = game.getTopCard();
            gc.fillText( c.getType() + ":" + c.getColour(), 405, 212 );

            for ( CardButton b : cardButtons )
            {
                b.update( mousePosX, mousePosY );
                b.render( gc );
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
        catch ( InvalidGameMoveException e )
        {
            LOGGER.debug( "Invalid game move!" );
        }
    }

    public void layoutCards()
    {
        cardButtons.clear();
        int x = 50;
        int y = 75;
        List<Card> cards = game.getCardsPerPlayer().get( client.getUsername() );
        for( Card c : cards )
        {
            cardButtons.add( new CardButton( x, y, 150, 20, c ) );
            y += 25;
        }
    }

    public void setGame( Game game )
    {
        this.game = game;
    }
}
