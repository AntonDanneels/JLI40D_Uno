package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Created by Anton D.
 */
public class GameMove implements Serializable
{
    private int id;
    private Player player;
    private Card playedCard;
    private boolean cardDrawn;

    public GameMove( int id, Player player, Card playedCard, boolean cardDrawn )
    {
        this.id = id;
        this.player = player;
        this.playedCard = playedCard;
        this.cardDrawn = cardDrawn;
    }

    public void setPlayedCard( Card playedCard )
    {
        this.playedCard = playedCard;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Card getPlayedCard()
    {
        return playedCard;
    }

    public boolean isCardDrawn()
    {
        return cardDrawn;
    }

    public int getId()
    {
        return id;
    }
}
