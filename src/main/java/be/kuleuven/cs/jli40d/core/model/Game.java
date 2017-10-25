package be.kuleuven.cs.jli40d.core.model;

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
public class Game implements Serializable
{
    private int gameID;

    private List<Player>            players;
    private List<Card>              deck;
    private List<GameMove>          moves;
    private Map<Player, List<Card>> cardsPerPlayer;

    private int maximumNumberOfPlayers;

    private boolean ended;
    private int     currentPlayer;
    private Card    topCard;
    private int     currentGameMoveID;
    private boolean clockwise;

    public Game( int gameID, int maximumNumberOfPlayers )
    {
        this.gameID = gameID;

        this.maximumNumberOfPlayers = maximumNumberOfPlayers;

        this.players = new ArrayList<>();
        this.deck = new ArrayList<>();
        this.moves = new ArrayList<>();

        this.cardsPerPlayer = new HashMap<>();

        this.topCard = null;
        this.ended = false;
        this.currentPlayer = 0;
        this.currentGameMoveID = 0;
        this.clockwise = true;
    }

    public Map<Player, List<Card>> getCardsPerPlayer()
    {
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
            if ( player.getUsername().equals( username ) )
            {
                return true;
            }
        }

        return false;
    }
}
