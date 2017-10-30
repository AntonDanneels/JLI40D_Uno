package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Created by Anton D.
 */
public class GameMove implements Serializable
{
    private int     id;
    private Player  player;
    private Card    playedCard;
    private boolean cardDrawn;

    private boolean activated;

    public GameMove( int id, Player player, Card playedCard, boolean cardDrawn )
    {
        this.id = id;
        this.player = player;
        this.playedCard = playedCard;
        this.cardDrawn = cardDrawn;

        this.activated = false;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Card getPlayedCard()
    {
        return playedCard;
    }

    public void setPlayedCard( Card playedCard )
    {
        this.playedCard = playedCard;
    }

    public boolean isCardDrawn()
    {
        return cardDrawn;
    }

    public int getId()
    {
        return id;
    }

    public boolean isActivated()
    {
        return activated;
    }

    public void setActivated( boolean activated )
    {
        this.activated = activated;
    }
}
