package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
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
public class Client extends JFrame implements ActionListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Client.class );

    private Game game;
    private int myID = 0;

    //Token received by the server
    private String token;
    private JPanel loginPanel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton login;
    private JButton register;

    private UserHandler userManager;
    private LobbyHandler lobbyHandler;

    private Client( UserHandler userManager, LobbyHandler lobbyHandler )
    {
        this.userManager = userManager;
        this.lobbyHandler = lobbyHandler;

        game = new Game(0);

        setTitle( "Uno" );
        setSize( 900, 600 );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setResizable( false );

        loginPanel = new JPanel();

        usernameField = new JTextField( "Username" );
        passwordField = new JPasswordField( "password" );
        login = new JButton( "Login" );
        register = new JButton( "Register" );


        register.addActionListener( this );

        login.addActionListener( this );

        loginPanel.add( usernameField );
        loginPanel.add( passwordField );
        loginPanel.add( register );
        loginPanel.add( login );

        add( loginPanel );
        setVisible( true );
    }

    public void actionPerformed( ActionEvent e )
    {
        try
        {
            if( e.getSource().equals( register ) )
                token = userManager.register( "test@test", "test", "test" );
            else
                token = userManager.login( "test", "test" );
        }
        catch ( RemoteException | InvalidUsernameOrPasswordException | AccountAlreadyExistsException e1 )
        {
            e1.printStackTrace();
        }

        try
        {
            JPanel lobbyListPanel = new JPanel();

            // fetch list here
            List<Game> games = lobbyHandler.currentGames( token );
            games.add( new Game(0) );

            lobbyListPanel.setLayout( new GridLayout( games.size(), 1 ) );

            for( int i = 0; i < games.size(); i++ )
            {
                Game game = games.get( i );
                JPanel panel = new JPanel();
                panel.add( new JLabel( "Game " + game.getGameID() ) );
                panel.add( new JLabel( "Players: " + game.getNumberOfJoinedPlayers()  ) );

                JButton join = new JButton( "Join" );
                JButton spectate = new JButton( "View" );

                join.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent e )
                    {
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

            remove( loginPanel );
            add( lobbyListPanel );
            validate();
        }
        catch ( RemoteException e1 )
        {
            e1.printStackTrace();
        }
        catch ( InvalidTokenException e1 )
        {
            e1.printStackTrace();
        }
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

    public static void main( String args[] )
    {
        String host = "localhost";
        int port = 1099;

        Registry myRegistry;

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

            myRegistry = LocateRegistry.getRegistry( host, port );
            final LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            final UserHandler userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );

            Client client = new Client( userManager, lobbyHandler );
        }
        catch ( Exception e )
        {
            // :no-words:
            e.printStackTrace();
        }
    }
}
