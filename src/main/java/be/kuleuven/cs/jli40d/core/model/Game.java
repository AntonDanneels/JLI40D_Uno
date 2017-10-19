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
