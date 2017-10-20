package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class Game implements Serializable
{
    private int gameID;

    private List <Player> players;
    private List <Card>   deck;

    private boolean ended;
    private int     currentPlayer;
    private Card    topCard;
    private int     currentGameMoveID;
    private boolean clockwise;

    public Game(int gameID)
    {
        this.gameID = gameID;

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
}
