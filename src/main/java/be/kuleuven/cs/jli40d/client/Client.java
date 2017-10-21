package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton D. on 19/10/2017 using IntelliJ IDEA 14.0
 */
public class Client
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Client.class );

    private Game game;
    private int myID = 0;

    //Token received by the server
    private String token;

    private Client()
    {
        game = new Game(0);
    }

    public void run()
    {
        GameHandler gameHandler = null;

        while ( !game.isEnded() )
        {
            GameMove move;
            if ( gameHandler.myTurn() )
            {
                // Construct my GameMove & send it
                move = new GameMove( game.getCurrentGameMoveID(), game.getPlayers().get( myID ), null, true );
            }
            else
            {
                move = gameHandler.getNextMove( game.getCurrentGameMoveID() );
            }

            // apply the game move to the game
            GameLogic gameLogic = new GameLogic();

            gameLogic.applyMove( game, move );

            game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
        }
    }

    public void connectToLobby( String host, int port )
    {
        try
        {
            Registry myRegistry = LocateRegistry.getRegistry( host, port );

            LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            UserHandler  userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );


            try
            {
                token = userManager.register( "test@test", "test", "test" );
                LOGGER.info( "Connected to server and received token {} after creating account.", token );

            } catch ( AccountAlreadyExistsException e) {
                LOGGER.warn( "Account already exists." );

                token = userManager.login( "test", "test" );
                LOGGER.info( "Connected to server and received token {} after logging in.", token );

            }



            LOGGER.info( "Requesting current games." );

            for ( Game game : lobbyHandler.currentGames( token ) ) {
                LOGGER.info("Game {}", game );
            }

            LOGGER.info( "Creating new game." );

            game.setGameID( lobbyHandler.makeGame( token, "Bob's game", 4));

            LOGGER.info( "Requesting current games." );

            for ( Game game : lobbyHandler.currentGames( token ) ) {
                LOGGER.info("Game {}", game );
            }

        }
        catch ( Exception e )
        {
            LOGGER.error( "Error: {}", e.getMessage() );
        }
    }

    public static void main( String args[] )
    {
        Client client = new Client();
        client.connectToLobby( "localhost", 2222 );
        //client.run();

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( Exception e )
        {
        }

        final JFrame frame = new JFrame( "Uno" );
        frame.setSize( 900, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setResizable( false );

        final JPanel loginPanel = new JPanel();

        JTextField usernameField = new JTextField( "Username" );
        JPasswordField passwordField = new JPasswordField( "password" );
        JButton login = new JButton( "Login" );
        JButton register = new JButton( "Register" );

        register.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                JPanel lobbyListPanel = new JPanel();

                // fetch list here
                List<Game> games = new ArrayList<Game>();
                games.add( new Game(0) );

                lobbyListPanel.setLayout( new GridLayout( games.size(), 1 ) );

                for( int i = 0; i < games.size(); i++ )
                {
                    Game game = games.get( i );
                    JPanel panel = new JPanel();
                    panel.add( new JLabel( "Game " + i ) );
                    panel.add( new JLabel( "Players: " + game.getPlayers().size()  ) );

                    JButton join = new JButton( "Join" );
                    JButton spectate = new JButton( "View" );

                    join.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            // TODO Join game here
                        }
                    } );

                    spectate.addActionListener( new ActionListener()
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            // TODO Spectate game here
                        }
                    } );

                    panel.add( join );
                    panel.add( spectate );

                    lobbyListPanel.add( panel );
                }

                frame.remove( loginPanel );
                frame.add( lobbyListPanel );
                frame.validate();
            }
        } );

        loginPanel.add( usernameField );
        loginPanel.add( passwordField );
        loginPanel.add( register );
        loginPanel.add( login );

        frame.add( loginPanel );
        frame.setVisible( true );
    }

}
