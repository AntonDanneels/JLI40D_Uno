package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Game is a collection of {@link Player} objects, specially created for
 * this game by their username. The game also has a set of cards and a few
 * status variables.
 *
 * When a Game is created, the creator specifies the number of players.
 *
 * @author Pieter
 * @version 1.0
 */
public class Game implements Serializable
{
    private int gameID;

    private List <Player> players;
    private List <Card>   deck;

    private int maximumNumberOfPlayers;

    private boolean ended;
    private int     currentPlayer;
    private Card    topCard;
    private int     currentGameMoveID;
    private boolean clockwise;

    public Game(int gameID, int maximumNumberOfPlayers)
    {
        this.gameID = gameID;

        this.maximumNumberOfPlayers = maximumNumberOfPlayers;

        this.players = new ArrayList <Player>();
        this.deck = new ArrayList <Card>();

        this.topCard = null;
        this.ended = false;
        this.currentPlayer = 0;
        this.currentGameMoveID = 0;
        this.clockwise = true;
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

    public void setPlayers( List <Player> players )
    {
        this.players = players;
    }

    public int getCurrentGameMoveID()
    {
        return currentGameMoveID;
    }

    public void setDeck( List <Card> deck )
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

    public List <Player> getPlayers()
    {
        return players;
    }

    public List <Card> getDeck()
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
}
