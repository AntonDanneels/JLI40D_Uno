package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.*;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidGameMoveException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.UnableToJoinGameException;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Anton D.
 */
public class GameSceneHandler extends AnimationTimer
{
    private Logger LOGGER = LoggerFactory.getLogger( GameSceneHandler.class );

    private GameClient      client;
    private LobbyHandler    lobbyHandler;
    private GameHandler     gameHandler;
    private Game            game;
    private ListenerService listenerService;
    private Queue<GameMove> gameMoves;

    private boolean mouseDown = false;
    private double  mousePosX = 0.0;
    private double  mousePosY = 0.0;

    private       List<CardButton> cardButtons;
    public static Map<Card, Image> images;
    public static Image            background;

    @FXML
    private Canvas          gameCanvas;
    private GraphicsContext gc;

    private Player me;

    private CardButton selectedCardButton = null;
    private int        topCardX           = 0;
    private int        topCardY           = 0;

    public GameSceneHandler()
    {
        cardButtons = new ArrayList<>();
        gameMoves = new ConcurrentLinkedDeque<>();
        images = new HashMap<>();
    }

    public void init( GameClient client, LobbyHandler lobbyHandler, GameHandler gameHandler )
    {
        this.client = client;
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;

        gameCanvas.setOnMousePressed( e ->
        {
            mouseDown = true;
        } );
        gameCanvas.setOnMouseReleased( e ->
        {
            mouseDown = false;
        } );
        gameCanvas.setOnMouseMoved( e ->
        {
            mousePosX = e.getX();
            mousePosY = e.getY();
        } );
        gameCanvas.setOnMouseDragged( e ->
        {
            mousePosX = e.getX();
            mousePosY = e.getY();
        } );

        gc = gameCanvas.getGraphicsContext2D();

        Game game = new Game( 0, 4 );
        GameLogic.generateDeck( game );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.GREEN ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.RED ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.BLUE ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.YELLOW ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.GREEN ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.RED ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.BLUE ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.YELLOW ) );

        for ( Card c : game.getDeck() )
        {
            String path = "/cards_original/" + c.getType() + "_" + c.getColour() + ".png";
            LOGGER.debug( "Loading image: {}", path );
            images.put( c, new Image( path ) );
        }

        topCardX = ( int )gameCanvas.getWidth() / 2 - 74/2;
        topCardY = ( int )gameCanvas.getHeight() / 2 - 20;

        LOGGER.debug( "Loaded {} images", images.size() );

        String path = "/uno-dark-background.png";
        LOGGER.debug( "Loading image: {}", path );
        background = new Image( path );

        LOGGER.debug( "loaded background." );
    }

    public void run()
    {
        LOGGER.debug( "Entering gameloop" );

        final String msg  = "Joining the game";
        final Text   text = new Text( msg );
        text.setFont( gc.getFont() );
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

        for ( Player p : game.getPlayers() )
        {
            if ( p.getUsername().equals( client.getUsername() ) )
                me = p;
        }

        layoutCards();

        new Thread( listenerService ).start();
        this.start();
    }

    public synchronized void handle( long now )
    {
        gc.setFill( Color.BLACK );
        gc.clearRect( 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight() );

        //draw background
        gc.drawImage( background, 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.fillText( "Mouse " + mouseDown + " , " + mousePosX + " , " + mousePosY, 10, 10 );

        try
        {
            while ( gameMoves.peek() != null )
            {
                GameMove move = gameMoves.poll();
                GameLogic.applyMove( game, move );
                game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
                layoutCards();

                // TODO create animation
            }

            //if ( gameHandler.myTurn( client.getToken(), game.getGameID() ) )
            if( game.getCurrentPlayerUsername().equals( client.getUsername() ) )
            {
                gc.fillText( "It is my turn", 50, 50 );
                if ( mouseDown )
                {
                    if ( Utils.intersects( ( int )mousePosX, ( int )mousePosY, 1, 1, 526, 282, 74, 106) )
                    {
                        GameMove move = new GameMove( game.getCurrentGameMoveID(), me, null, true );

                        if ( GameLogic.testMove( game, move ) )
                        {
                            LOGGER.debug( "Sending gamemove to draw card" );
                            gameHandler.sendMove( client.getToken(), game.getGameID(), move );
                        }
                    }
                    else if ( selectedCardButton == null )
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
                        selectedCardButton.setX( ( int )mousePosX - selectedCardButton.getW() / 2 );
                        selectedCardButton.setY( ( int )mousePosY - selectedCardButton.getH() / 2 );
                    }
                }
                else
                {
                    if ( selectedCardButton != null )
                    {
                        if ( Utils.intersects( selectedCardButton.getX(), selectedCardButton.getY(), selectedCardButton.getW(), selectedCardButton.getH(), topCardX, topCardY, 50, 75 ) )
                        {
                            GameMove move = new GameMove( game.getCurrentGameMoveID(), me, selectedCardButton.getC(), false );

                            if ( GameLogic.testMove( game, move ) )
                            {
                                LOGGER.debug( "Sending gamemove with played card {}:{}", selectedCardButton.getC().getType(), selectedCardButton.getC().getColour() );
                                if ( move.getPlayedCard().getType() == CardType.OTHER_COLOUR || move.getPlayedCard().getType() == CardType.PLUS4 )
                                    createChooseColourPopup( move );
                                else
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
                gc.fillText( "Waiting for the other players", 50, 50 );
            }

            //gc.setFill( Color.TRANSPARENT );
            Card c = game.getTopCard();
            //gc.clearRect( topCardX, topCardY, 74, 108 );
            gc.drawImage( images.get( c ), topCardX, topCardY, 74, 108 );



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

    private void createChooseColourPopup( GameMove move )
    {
        LOGGER.debug( "Showing dialog to choose colour game" );

        Stage myDialog = new Stage();
        myDialog.initModality( Modality.WINDOW_MODAL );
        myDialog.setTitle( "Choose the colour" );

        Card c = move.getPlayedCard();

        c.setColour( CardColour.RED );
        ImageView redView = new ImageView( images.get( c ) );
        redView.setFitHeight( 75 );
        redView.setFitWidth( 50 );
        redView.setOnMouseClicked( event ->
        {
            move.getPlayedCard().setColour( CardColour.RED );
            sendMove( move );
            myDialog.close();
        } );

        c.setColour( CardColour.GREEN );
        ImageView greenView = new ImageView( images.get( c ) );
        greenView.setFitHeight( 75 );
        greenView.setFitWidth( 50 );
        greenView.setOnMouseClicked( event ->
        {
            move.getPlayedCard().setColour( CardColour.GREEN );
            sendMove( move );
            myDialog.close();
        } );

        c.setColour( CardColour.YELLOW );
        ImageView yellowView = new ImageView( images.get( c ) );
        yellowView.setFitHeight( 75 );
        yellowView.setFitWidth( 50 );
        yellowView.setOnMouseClicked( event ->
        {
            move.getPlayedCard().setColour( CardColour.YELLOW );
            sendMove( move );
            myDialog.close();
        } );

        c.setColour( CardColour.BLUE );
        ImageView blueView = new ImageView( images.get( c ) );
        blueView.setFitHeight( 75 );
        blueView.setFitWidth( 50 );
        blueView.setOnMouseClicked( event ->
        {
            move.getPlayedCard().setColour( CardColour.BLUE );
            sendMove( move );
            myDialog.close();
        } );

        c.setColour( CardColour.NO_COLOUR );

        HBox dialogBox = new HBox( 20 );
        dialogBox.getChildren().addAll( redView, greenView, blueView, yellowView );

        Scene myDialogScene = new Scene( dialogBox, 300, 100 );

        myDialog.setScene( myDialogScene );
        myDialog.show();
    }

    private void sendMove( GameMove move )
    {
        try
        {
            gameHandler.sendMove( client.getToken(), game.getGameID(), move );
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
            e.printStackTrace();
        }
    }

    public void layoutCards()
    {
        cardButtons.clear();
        List<Card> cards = game.getCardsPerPlayer().get( client.getUsername() );
        int        x     = ( int )gameCanvas.getWidth() / 2 - cards.size() * 80 / 2;
        int        y     = 475;
        for ( Card c : cards )
        {
            cardButtons.add( new CardButton( x, y, 74, 108, c ) );
            x += 80;
        }
    }

    public void setGame( Game game )
    {
        this.game = game;
    }
}
