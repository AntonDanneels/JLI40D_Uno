package be.kuleuven.cs.jli40d.core.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Game is a collection of {@link Player} objects, specially created for
 * this game by their username. The game also has a set of cards and a few
 * status variables.
 * <p>
 * When a Game is created, the creator specifies the number of players.
 *
 * @author Pieter
 * @version 1.0
 */
@Entity
public class Game implements Serializable
{
    @Transient
    private static int currentGameID = 0;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    protected int gameID;

    private String uuid;

    @OneToMany
    @Cascade( org.hibernate.annotations.CascadeType.SAVE_UPDATE )
    @LazyCollection( LazyCollectionOption.FALSE )
    private List<Player> players;

    @OneToMany( cascade = CascadeType.MERGE )
    @LazyCollection( LazyCollectionOption.FALSE )
    private List<GameMove> moves;

    @OneToMany( cascade = CascadeType.MERGE )
    @LazyCollection( LazyCollectionOption.FALSE )
    private List<Card> deck;

    @Transient
    private Map<String, PlayerHand> playerHands;

    private String name;

    private int maximumNumberOfPlayers;

    @ManyToOne
    private Card topCard;

    private boolean started;
    private boolean ended;
    private int     currentPlayer;
    private int     currentGameMoveID;
    private boolean clockwise;

    public Game()
    {
    }

    public Game( int maximumNumberOfPlayers )
    {
        this.gameID = currentGameID++;
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;

        this.players = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.moves = new ArrayList<>();

        this.playerHands = new HashMap<>();

        this.topCard = null;
        this.started = false;
        this.ended = false;
        this.currentPlayer = 0;
        this.currentGameMoveID = -1;
        this.clockwise = true;
    }

    public Game( int gameID, List<Player> players, List<GameMove> moves, List<Card> deck,
                 Map<String, PlayerHand> playerHands, String name, int maximumNumberOfPlayers,
                 Card topCard, boolean started, boolean ended, int currentPlayer,
                 int currentGameMoveID, boolean clockwise )
    {
        this.gameID = gameID;
        this.players = players;
        this.moves = moves;
        this.deck = deck;
        this.playerHands = playerHands;
        this.name = name;
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
        this.topCard = topCard;
        this.started = started;
        this.ended = ended;
        this.currentPlayer = currentPlayer;
        this.currentGameMoveID = currentGameMoveID;
        this.clockwise = clockwise;
    }

    public Game( String name, int maximumNumberOfPlayers )
    {
        this( maximumNumberOfPlayers );
        this.name = name;
    }

    public Map<String, PlayerHand> getPlayerHands()
    {
        return playerHands;
    }

    public void setPlayerHands( Map<String, PlayerHand> playerHands )
    {
        this.playerHands = playerHands;
    }

    /**
     * Legacy function that creates a <code> Map<String, List<Card>> </code> object.
     *
     * @return A mapping with all the cards for each player
     */
    public Map<String, List<Card>> getCardsPerPlayer()
    {
        Map<String, List<Card>> cardsPerPlayer = new HashMap<>();

        for ( String player : playerHands.keySet() )
        {
            cardsPerPlayer.put( player, playerHands.get( player ).getPlayerHands() );
        }

        return cardsPerPlayer;
    }

    public void setCurrentGameMoveID( int currentGameMoveID )
    {
        this.currentGameMoveID = currentGameMoveID;
    }

    public boolean isClockwise()
    {
        return clockwise;
    }

    public void setClockwise( boolean clockwise )
    {
        this.clockwise = clockwise;
    }

    public void setPlayers( List<Player> players )
    {
        this.players = players;
    }

    public int getCurrentGameMoveID()
    {
        return currentGameMoveID;
    }

    public void setDeck( List<Card> deck )
    {
        this.deck = deck;
    }

    public void setEnded( boolean ended )
    {
        this.ended = ended;
    }

    public void setCurrentPlayer( int currentPlayer )
    {
        this.currentPlayer = currentPlayer;
    }

    public void setTopCard( Card topCard )
    {
        this.topCard = topCard;
    }

    public List<Player> getPlayers()
    {
        return players;
    }

    public List<Card> getDeck()
    {
        return deck;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted( boolean started )
    {

        this.started = started;
    }

    public boolean isEnded()
    {
        return ended;
    }

    public int getCurrentPlayer()
    {
        return currentPlayer;
    }

    public Card getTopCard()
    {
        return topCard;
    }

    public void setGameID( int gameID )
    {
        this.gameID = gameID;
    }

    public int getGameID()
    {
        return gameID;
    }

    public int getMaximumNumberOfPlayers()
    {
        return maximumNumberOfPlayers;
    }

    public void setMaximumNumberOfPlayers( int maximumNumberOfPlayers )
    {
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
    }

    /**
     * Util function that tells us how many players are currently joined.
     *
     * @return An int with the size of the payer list.
     */
    public int getNumberOfJoinedPlayers()
    {
        return players.size();
    }

    /**
     * Util function that returns the username (instead of an int at {@link #getCurrentPlayer()}).
     *
     * @return The username of the current player.
     */
    public String getCurrentPlayerUsername()
    {
        return players.get( currentPlayer ).getUsername();
    }

    public List<GameMove> getMoves()
    {
        return moves;
    }

    public void setMoves( List<GameMove> moves )
    {
        this.moves = moves;
    }

    public void addLatestMove( GameMove gameMove )
    {
        this.moves.add( gameMove );
    }

    /**
     * Util function that returns true if a user, based on his username, has joined the game.
     *
     * @param username The username to check.
     * @return True if the player is in the game, false otherwise.
     */
    public boolean hasPlayer( String username )
    {
        for ( Player player : players )
        {
            if ( username.equals( player.getUsername() ) )
            {
                return true;
            }
        }

        return false;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid( String uuid )
    {
        this.uuid = uuid;
    }
}
