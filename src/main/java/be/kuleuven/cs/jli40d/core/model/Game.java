package be.kuleuven.cs.jli40d.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class Game
{
    private List <Player> players;
    private List <Card>   deck;

    private boolean ended;
    private int     currentPlayer;
    private Card    topCard;

    public Game()
    {
        players = new ArrayList <Player>();
        deck = new ArrayList <Card>();

        topCard = null;
        ended = false;
        currentPlayer = 0;
    }

    public void setPlayers( List <Player> players )
    {
        this.players = players;
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
}
