package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerRegistrationHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.*;
import be.kuleuven.cs.jli40d.core.model.exception.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by Anton D.
 */
public class SpectateSceneHandler extends AnimationTimer
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SpectateSceneHandler.class );

    private List <Pair <Integer, Integer>> positions;

    private Map <String, Pair <Integer, Integer>> positionsPerPlayer;

    public static final int CARD_WIDTH  = 74;
    public static final int CARD_HEIGHT = 108;

    private GameClient       client;
    private LobbyHandler     lobbyHandler;
    private GameHandler      gameHandler;
    private Game             game;
    private GameSummary      gameSummary;
    private ListenerService  listenerService;
    private Queue <GameMove> gameMoves;

    private ServerRegistrationHandler registrationHandler;

    private boolean mouseDown = false;
    private double  mousePosX = 0.0;
    private double  mousePosY = 0.0;

    private List <CardButton> cardButtons;

    private List <CardAnimation> animations;

    @FXML
    private Canvas gameCanvas;
    @FXML
    private Pane   gamePanel;

    private Button backToLobbyButton;

    private GraphicsContext gc;
    private Player          me;
    private CardButton selectedCardButton = null;

    private int topCardX    = 0;
    private int topCardY    = 0;
    private int cardOffsetX = 0;

    public SpectateSceneHandler()
    {
        animations = new ArrayList <>();
        cardButtons = new ArrayList <>();
        gameMoves = new ConcurrentLinkedDeque <>();

        positions = new LinkedList <>();

        positions.addAll( Arrays.asList( new Pair <>( 83, 83 ), new Pair <>( 709, 83 ), new Pair <>( 83, 423 ), new Pair <>( 709, 423 ), new Pair <>( 396, 9 ) ) );
    }

    public void init( GameClient client, LobbyHandler lobbyHandler, GameHandler gameHandler, ServerRegistrationHandler registrationHandler )
    {
        this.client = client;
        this.gameHandler = gameHandler;
        this.lobbyHandler = lobbyHandler;
        this.registrationHandler = registrationHandler;

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

        topCardX = ( int ) gameCanvas.getWidth() / 2 - 74 / 2;
        topCardY = ( int ) gameCanvas.getHeight() / 2 - 20;

        backToLobbyButton = new Button( "Go to lobby" );
        backToLobbyButton.setOnAction( event ->
        {
            client.setLobbyScene();
        } );

    }

    public void run()
    {
        gamePanel.getChildren().remove( backToLobbyButton );
        animations.clear();
        cardButtons.clear();
        gameMoves.clear();

        me = null;
        selectedCardButton = null;
        listenerService = null;

        cardOffsetX = 0;

        LOGGER.debug( "Entering gameloop" );

        gc.clearRect( 0, 0, gameCanvas.getWidth(), gameCanvas.getWidth() );
        final String msg = "Joining the game";
        gc.setTextAlign( TextAlignment.CENTER );
        gc.setFont( new Font( gc.getFont().getName(), 32 ) );
        gc.fillText( msg, gameCanvas.getWidth() / 2, 50 );
        gc.setFont( new Font( gc.getFont().getName(), 12 ) );

        // TODO add rotating card here

        new Thread( new Runnable()
        {
            public void run()
            {
                try
                {
                    game = lobbyHandler.spectateGame( client.getToken(), gameSummary.getUuid() );
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
                catch ( GameEndedException e )
                {
                    Utils.createPopup( "Game has ended." );
                    LOGGER.debug( "Tried to join ended game with id {}: {}", game.getGameID(), e.getMessage() );
                    client.setLobbyScene();
                }
                catch ( WrongServerException e )
                {
                    LOGGER.debug( "Changing server" );

                    try
                    {
                        Server newServer = registrationHandler.getServer( gameSummary.getUuid() );
                        client.resetConnection( newServer );
                        this.run();
                    }
                    catch ( RemoteException e1 )
                    {
                        e1.printStackTrace();
                    }
                    catch ( GameNotFoundException e1 )
                    {
                        e1.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private synchronized void enterGameLoop()
    {
        listenerService = new ListenerService( client, registrationHandler, gameHandler, client.getToken(), game, gameMoves );

        for ( Player p : game.getPlayers() )
        {
            if ( p.getUsername().equals( client.getUsername() ) )
                me = p;
        }

        initializePositions();

        new Thread( listenerService ).start();
        this.start();
    }

    public synchronized void handle( long now )
    {
        gc.setFill( Color.BLACK );
        gc.clearRect( 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight() );

        //draw background
        gc.drawImage( ImageLoader.getSceneImage( SceneImage.SPECTATOR_BACKGROUND ), 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight() );

        for ( Player player : game.getPlayers() )
        {
            String username = player.getUsername();

            if ( !username.equals( client.getUsername() ) )
            {
                int x = getPlayerPosition( player.getUsername() ).getKey();
                int y = getPlayerPosition( player.getUsername() ).getValue();

                if ( game.getCurrentPlayerUsername().equals( username ) )
                {
                    //player glow is 30x30 @2x
                    gc.drawImage( ImageLoader.getSceneImage( SceneImage.CURRENT_USER ), x - 14, y - 14, 132, 132 );
                }


                gc.drawImage( ImageLoader.getSceneImage( SceneImage.DEFAULT_AVATAR ), x, y, 104, 104 );

                gc.fillText(
                        username + " (" + game.getPlayerHands().get( username ).getPlayerHands().size() + ")",
                        getPlayerPosition( player.getUsername() ).getKey() + 52,
                        getPlayerPosition( player.getUsername() ).getValue() + 120 );
            }

        }

        try
        {
            while ( gameMoves.peek() != null )
            {
                GameMove move = gameMoves.poll();
                GameLogic.applyMove( game, move );
                game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );

                if ( move.isCardDrawn() )
                {
                    animations.add( new CardAnimation( new Pair <Integer, Integer>( 526, 282 ),
                            getPlayerPosition( move.getPlayer().getUsername() ),
                            ImageLoader.getSceneImage( SceneImage.CARD_BACK ) ) );
                }
                else
                {
                    animations.add( new CardAnimation( getPlayerPosition( move.getPlayer().getUsername() ),
                            new Pair <Integer, Integer>( topCardX, topCardY ),
                            ImageLoader.getCardImage( move.getPlayedCard() ) ) );
                }
            }

            //gc.setFill( Color.TRANSPARENT );
            Card c = game.getTopCard();
            //gc.clearRect( topCardX, topCardY, 74, 108 );
            gc.drawImage( ImageLoader.getCardImage( c ), topCardX, topCardY, CARD_WIDTH, CARD_HEIGHT );


            for ( CardButton b : cardButtons )
            {
                b.update( mousePosX, mousePosY );
                b.render( gc );
            }


            Iterator <CardAnimation> cardAnimationIterator = animations.iterator();
            while ( cardAnimationIterator.hasNext() )
            {
                CardAnimation animation = cardAnimationIterator.next();
                animation.update();
                animation.render( gc );
                if ( !animation.isAlive() )
                    cardAnimationIterator.remove();
            }

            testGameEnded();
        }
        catch ( InvalidGameMoveException e )
        {
            LOGGER.debug( "Invalid game move! {}", e );
        }
    }

    private void testGameEnded()
    {
        if ( GameLogic.hasGameEnded( game ) )
        {
            LOGGER.debug( "Game has ended" );

            Player winner = GameLogic.getWinner( game );
            int    score  = GameLogic.calculateScoreForPlayer( winner.getUsername(), game );

            gc.applyEffect( new GaussianBlur( 50 ) );

            gc.setFill( Color.WHITE );
            gc.setFont( Font.font( gc.getFont().getName(), FontWeight.BOLD, 48 ) );
            gc.fillText( "The game has ended", gameCanvas.getWidth() / 2, 300 );
            gc.setFont( Font.font( gc.getFont().getName(), FontWeight.NORMAL, 32 ) );
            gc.fillText( "" + winner.getUsername() + " has won with score: " + score, gameCanvas.getWidth() / 2, 350 );
            gc.setFill( Color.BLACK );

            backToLobbyButton.setTranslateY( 375 );
            backToLobbyButton.setTranslateX( gameCanvas.getWidth() / 2 - 40 );

            //listenerService.setActive( false );
            stop();

            gamePanel.getChildren().addAll( backToLobbyButton );
        }
    }


    /**
     * Returns the position of the other players.
     *
     * @param player The number of the other player.
     * @return
     */
    public Pair <Integer, Integer> getPlayerPosition( String player )
    {
        return positionsPerPlayer.get( player );
    }

    public void setGameSummary( GameSummary gameSummary )
    {
        this.gameSummary = gameSummary;
    }

    /**
     * Generate a list with player positions for easy access.
     */
    private void initializePositions()
    {
        positionsPerPlayer = new HashMap <>();

        for ( Player player : game.getPlayers() )
        {

            //fixed position for current player
            if ( game.getMaximumNumberOfPlayers() == 3 && positions.size() == 5)
            {
                positionsPerPlayer.put( player.getUsername(), positions.remove( 4 ) );
            }
            else
            {
                positionsPerPlayer.put( player.getUsername(), positions.remove( 0 ) );
            }
        }
    }

    public void setLobbyHandler( LobbyHandler lobbyHandler )
    {
        this.lobbyHandler = lobbyHandler;
    }

    public void setGameHandler( GameHandler gameHandler )
    {
        this.gameHandler = gameHandler;
        if( game != null )
        {
            this.listenerService.setGameHandler( gameHandler );
            new Thread( this.listenerService ).start();
        }
    }
}
