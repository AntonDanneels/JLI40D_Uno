package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
    private JPanel lobbyListPanel;
    private JPanel gamePanel;

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JButton        login;
    private JButton        register;

    private UserHandler  userManager;
    private LobbyHandler lobbyHandler;
    private GameHandler  gameHandler;

    private Client( UserHandler userManager, LobbyHandler lobbyHandler, GameHandler gameHandler )
    {
        this.userManager = userManager;
        this.lobbyHandler = lobbyHandler;
        this.gameHandler = gameHandler;

        game = new Game( 0, 4 );

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

        this.addMouseListener( new MouseEventListener() );
    }

    public void actionPerformed( ActionEvent e )
    {
        try
        {
            if ( e.getSource().equals( register ) )
                token = userManager.register( "test@test", usernameField.getText(), new String( passwordField.getPassword() ) );
            else
                token = userManager.login( usernameField.getText(), new String( passwordField.getPassword() ) );
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
        remove( loginPanel );
        if ( lobbyListPanel != null )
            remove( lobbyListPanel );

        lobbyListPanel = new JPanel();

        List<Game> games = lobbyHandler.currentGames( token );

        lobbyListPanel.setLayout( new BorderLayout() );

        JPanel  buttonPanel = new JPanel();
        JButton refresh     = new JButton( "Refresh" );
        JButton newGame     = new JButton( "New game" );

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
                JTextField gameNameField    = new JTextField();
                JTextField nrOfPlayersField = new JTextField();
                Object[] message = {
                        "Game name:", gameNameField,
                        "Nr of players:", nrOfPlayersField,
                };
                int option = JOptionPane.showConfirmDialog( frame, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION );

                if ( option == JOptionPane.OK_OPTION )
                {
                    String gameName = gameNameField.getText();
                    String value2   = nrOfPlayersField.getText();
                    try
                    {
                        int nrOfPlayers = Integer.valueOf( value2 );
                        int id          = lobbyHandler.makeGame( token, gameName, nrOfPlayers );
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
        listPanel.setLayout( new BoxLayout( listPanel, BoxLayout.PAGE_AXIS ) );

        for ( int i = 0; i < games.size(); i++ )
        {
            Game   game  = games.get( i );
            JPanel panel = new JPanel();
            panel.add( new JLabel( "Game " + game.getGameID() ) );
            panel.add( new JLabel( "Players: " + game.getNumberOfJoinedPlayers() ) );

            JButton join     = new JButton( "Join" );
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

        add( lobbyListPanel );
        revalidate();
        repaint();

    }

    private void joinGame( int id )
    {
        try
        {
            remove( loginPanel );
            if ( lobbyListPanel != null )
                remove( lobbyListPanel );

            gamePanel = new JPanel();

            add( gamePanel );
            revalidate();
            repaint();

            //Graphics g = gamePanel.getGraphics();
            //g.drawString( "Joining..", 50, 50 );

            game = lobbyHandler.joinGame( token, id );

            run();
        }
        catch ( RemoteException | InvalidTokenException | UnableToJoinGameException e )
        {
            e.printStackTrace();
            add( loginPanel );
            revalidate();
            repaint();
        }
        catch ( GameNotFoundException e )
        {
            add( lobbyListPanel );
            revalidate();
            repaint();
            e.printStackTrace();
        }
        catch ( InvalidGameMoveException e )
        {
            e.printStackTrace();
        }
    }

    private void setCard( int index, Player player, List<Card> cards )
    {
        GameMove move;
        if ( index < 0 )
            move = new GameMove( game.getCurrentGameMoveID(), player, null, true );
        else
            move = new GameMove( game.getCurrentGameMoveID(), player, cards.get( index ), false );

        if ( GameLogic.testMove( game, move ) )
        {
            try
            {
                GameMove result = gameHandler.sendMove( token, game.getGameID(), move );
                //GameLogic.applyMove( game, move );
                //game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
                run();
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
    }

    public void run() throws InvalidTokenException, RemoteException, GameNotFoundException, InvalidGameMoveException
    {
        GameMove move;
        if ( gameHandler.myTurn( token, game.getGameID() ) )
        {
            gamePanel.removeAll();
            // This is temporary until we've decided how to keep track of a players card.
            Player me = null;
            for ( int i = 0; i < game.getPlayers().size(); i++ )
            {
                if ( game.getPlayers().get( i ).getUsername().equals( usernameField.getText() ) )
                    me = game.getPlayers().get( i );
            }

            final Player     meP   = me;
            final List<Card> cards = game.getCardsPerPlayer().get( me );

            for ( int i = 0; i < cards.size(); i++ )
            {
                Card      c     = cards.get( i );
                final int index = i;
                //g.drawString( "" + c.getColour() + ":" + c.getType(), 250, 75 * ( i + 1 ) );
                JButton button = new JButton( c.getColour() + ":" + c.getType() );
                button.addActionListener( e ->
                {
                    setCard( index, meP, cards );
                } );

                gamePanel.add( button );
            }

            JButton drawCardButton = new JButton( "Draw card" );
            drawCardButton.addActionListener( e ->
            {
                setCard( -1, meP, cards );
            } );

            gamePanel.add( drawCardButton );

            Card topCard = game.getTopCard();
            gamePanel.add( new JLabel( "TopCard: " + topCard.getColour() + ":" + topCard.getType() ) );

            for ( int i = 0; i < game.getPlayers().size(); i++ )
            {
                gamePanel.add( new JLabel( "Player: " + game.getPlayers().get( i ).getUsername() ) );
                gamePanel.add( new JLabel( "Cards " + game.getPlayers().get( i ).getNrOfCards() ) );
            }

            gamePanel.add( new JLabel( "it is my turn" ) );
            gamePanel.revalidate();
            // Construct my GameMove & send it
            //move = new GameMove( game.getCurrentGameMoveID(), me, cards.get( currentCardIndex ), false );
            //gameHandler.sendMove( token, game.getGameID(), move );
            LOGGER.debug( "Waiting for input." );
        }
        else
        {

            LOGGER.debug( "Waiting for move {}", game.getCurrentGameMoveID() );

            gamePanel.removeAll();
            gamePanel.revalidate();
            gamePanel.repaint();

            Graphics g = gamePanel.getGraphics();
            g.clearRect( 0, 0, getWidth(), getHeight() );

            Card topCard = game.getTopCard();
            g.drawString( "" + topCard.getColour() + ":" + topCard.getType(), 450, 150 );

            for ( int i = 0; i < game.getPlayers().size(); i++ )
            {
                g.drawString( "Player " + game.getPlayers().get( i ).getUsername(), 50, 150 + 25 * i );
                g.drawString( "Cards " + game.getPlayers().get( i ).getNrOfCards(), 60, 160 + 25 * i );
            }

            g.drawString( "Waiting for the other players", 250, 150 );
        }

        move = gameHandler.getNextMove( token, game.getGameID(), game.getCurrentGameMoveID() );
        GameLogic.applyMove( game, move );
        game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );

        run();

    }

    public static void main( String args[] )
    {
        String host = "localhost";
        int    port = 1099;

        Registry myRegistry;

        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

            myRegistry = LocateRegistry.getRegistry( host, port );
            final LobbyHandler lobbyHandler = ( LobbyHandler )myRegistry.lookup( LobbyHandler.class.getName() );
            final UserHandler  userManager  = ( UserHandler )myRegistry.lookup( UserHandler.class.getName() );
            final GameHandler  gameHandler  = ( GameHandler )myRegistry.lookup( GameHandler.class.getName() );

            Client client = new Client( userManager, lobbyHandler, gameHandler );
        }
        catch ( Exception e )
        {
            // :no-words: :marvelous:
            e.printStackTrace();
        }
    }
}
