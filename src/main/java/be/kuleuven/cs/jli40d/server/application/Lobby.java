package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.LobbyHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Implementation of a {@link LobbyHandler} interface.
 * <p>
 * Validation of tokens happens in a implementation of the {@link UserTokenHandler}.
 * <p>
 * This is a local implementation, meaning every application server has its own list
 * of games in memory. It's not persisted in any way.
 *
 * @author Pieter
 * @version 1.0
 */
public class Lobby extends UnicastRemoteObject implements LobbyHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Lobby.class );

    private UserTokenHandler userManager;

    private GameListHandler games;

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     * <p>
     * The object is exported with a server socket
     * created using the {@link RMISocketFactory} class.
     *
     * @param userManager A {@link UserTokenHandler} implementation that keeps track of all tokens.
     * @param games       A {@link GameListHandler} implementation.
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    Lobby( UserTokenHandler userManager, GameListHandler games ) throws RemoteException
    {
        super();
        this.userManager = userManager;
        this.games = games;

    }

    /**
     * Request a list with all games that are currently ongoing.
     *
     * @param token Token received by the {@link UserHandler}.
     * @return A list of all Game objects.
     * @throws InvalidTokenException When the token is invalid (expired or not found).
     */
    public List<Game> currentGames( String token ) throws InvalidTokenException
    {
        LOGGER.debug( "Requested list of games by {}", token );

        //Check authentication, throws error if token is invalid
        userManager.findUserByToken( token );

        return games.getAllGames();
    }

    /**
     * @param token           Token received by the {@link UserHandler}.
     * @param gameName        The desired name of the game, this is displayed in the lobby.
     * @param numberOfPlayers The number of games the game should have.
     * @return
     * @throws InvalidTokenException       When the token is invalid (expired or not found).
     * @throws UnableToCreateGameException When the game cannot be created for some reason (like exceeded limits).
     */
    public int makeGame( String token, String gameName, int numberOfPlayers ) throws InvalidTokenException, UnableToCreateGameException
    {
        //initial check for token
        userManager.findUserByToken( token );

        Game game = new Game( games.nextID(), numberOfPlayers );

        games.add( game );

        return game.getGameID();
    }

    /**
     * Join a game with an id, either provided by the {@link #makeGame} or {@link #currentGames} method.
     * <p>
     * If the game is full, a {@link GameFullException} is thrown. In other cases, like when the player should
     * already have joined, the more general {@link UnableToJoinGameException} is thrown.
     * <p>
     * This method is blocking. This means that if no exception is thrown, the method will return a {@link Game}
     * object only when all players have joined.
     *
     * @param token  Token received by the {@link UserHandler}.
     * @param gameID The id of the game to join.
     * @return A Game object.
     * @throws UnableToJoinGameException When the user cannot join the game for various reasons.
     * @throws InvalidTokenException     When the token is invalid (expired or not found).
     */
    public synchronized Game joinGame( String token, int gameID ) throws UnableToJoinGameException, InvalidTokenException
    {
        //initial check for token and find username
        String username = userManager.findUserByToken( token );

        //throws an error if the game is not in the list
        Game requestedGame;
        try
        {
            requestedGame = games.getGameByID( gameID );
        }
        catch ( GameNotFoundException e )
        {
            LOGGER.info( "User {} tried to join a non-existing game with id = {}.", username, gameID );

            throw new UnableToJoinGameException( "Game not found" );
        }

        //check if the game is not full or has ended
        if ( requestedGame.getNumberOfJoinedPlayers() >= requestedGame.getMaximumNumberOfPlayers() )
        {
            LOGGER.info( "{} tried to join a full game ( {} ).", username, gameID );

            throw new UnableToJoinGameException( "Game full." );
        }
        else if ( requestedGame.isEnded() )
        {
            LOGGER.info( "{} tried to join a game ( {} ) that has ended.", username, gameID );

            throw new UnableToJoinGameException( "Game has ended." );
        }

        //create a new player with the next id of the list
        Player player = new Player( requestedGame.getNumberOfJoinedPlayers(), username );

        requestedGame.getPlayers().add( player );

        LOGGER.info( "Player {} added to game {}.", username, gameID );

        //blocking until all players joined
        while ( requestedGame.getNumberOfJoinedPlayers() < requestedGame.getMaximumNumberOfPlayers() ) {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                LOGGER.error( "Thread interrupted. SAD. {}", e.getMessage() );
            }
        }

        notifyAll();

        LOGGER.debug( "Returning joinGame method calls." );

        return requestedGame;
    }


    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;

        Lobby lobby = ( Lobby )o;

        if ( userManager != null ? !userManager.equals( lobby.userManager ) : lobby.userManager != null ) return false;
        return games != null ? games.equals( lobby.games ) : lobby.games == null;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + ( userManager != null ? userManager.hashCode() : 0 );
        result = 31 * result + ( games != null ? games.hashCode() : 0 );
        return result;
    }
}
