package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.exception.*;
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

        game = new Game(0, 4);

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
                token = userManager.register( "test@test", usernameField.getText(), passwordField.getPassword().toString() );
            else
                token = userManager.login( usernameField.getText(), passwordField.getPassword().toString() );
        }
        catch ( RemoteException | InvalidUsernameOrPasswordException | AccountAlreadyExistsException e1 )
        {
            e1.printStackTrace();
            return;
        }

        try
        {
            updateLobbyList();
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

    private void updateLobbyList() throws RemoteException, InvalidTokenException
    {
        JPanel lobbyListPanel = new JPanel();

        List<Game> games = lobbyHandler.currentGames( token );

        lobbyListPanel.setLayout( new BorderLayout() );

        JPanel buttonPanel = new JPanel();
        JButton refresh = new JButton( "Refresh" );
        JButton newGame = new JButton( "New game" );

        refresh.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                try
                {
                    updateLobbyList();
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
        } );

        final JFrame frame = this;

        newGame.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                JTextField gameNameField = new JTextField();
                JTextField nrOfPlayersField = new JTextField();
                Object[] message = {
                        "Game name:", gameNameField,
                        "Nr of players:", nrOfPlayersField,
                };
                int option = JOptionPane.showConfirmDialog(frame, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION)
                {
                    String gameName = gameNameField.getText();
                    String value2 = nrOfPlayersField.getText();
                    try
                    {
                        int nrOfPlayers = Integer.valueOf( value2 );
                        int id = lobbyHandler.makeGame( token, gameName, nrOfPlayers  );
                        updateLobbyList();
                    }
                    catch ( RemoteException e1 )
                    {
                        e1.printStackTrace();
                    }
                    catch ( InvalidTokenException e1 )
                    {
                        e1.printStackTrace();
                    }
                    catch ( UnableToCreateGameException e1 )
                    {
                        e1.printStackTrace();
                    }
                }
            }
        } );

        buttonPanel.add( refresh );
        buttonPanel.add( newGame );
        lobbyListPanel.add( buttonPanel, BorderLayout.NORTH );

        JPanel listPanel = new JPanel();
        listPanel.setLayout( new BoxLayout( listPanel, BoxLayout.PAGE_AXIS) );

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
                    joinGame( game.getGameID() );
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

            listPanel.add( panel );
        }

        lobbyListPanel.add( listPanel, BorderLayout.CENTER );

        remove( lobbyListPanel );
        remove( loginPanel );
        add( lobbyListPanel );
        validate();
    }

    private void joinGame( int id )
    {
        try
        {
            game = lobbyHandler.joinGame( token, id );
        }
        catch ( RemoteException e )
        {
            e.printStackTrace();
        }
        catch ( UnableToJoinGameException e )
        {
            e.printStackTrace();
        }
        catch ( InvalidTokenException e )
        {
            e.printStackTrace();
        }
    }

    public void run() throws InvalidTokenException, RemoteException
    {
        GameHandler gameHandler = null;

        while ( !game.isEnded() )
        {
            GameMove move;
            if ( gameHandler.myTurn(token, game.getGameID()) )
            {
                // Construct my GameMove & send it
                move = new GameMove( game.getCurrentGameMoveID(), game.getPlayers().get( myID ), null, true );
            }
            else
            {
                move = gameHandler.getNextMove(token, game.getGameID(), game.getCurrentGameMoveID() );
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
