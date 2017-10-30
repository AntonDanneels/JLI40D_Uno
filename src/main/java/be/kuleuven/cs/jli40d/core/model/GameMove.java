package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Anton D.
 */
@Entity
public class GameMove implements Serializable
{
    @Id
    private long    id;
    private Player  player;
    private Card    playedCard;
    private boolean cardDrawn;

    private boolean activated;

    public GameMove()
    {
    }

    public GameMove( int id, Player player, Card playedCard, boolean cardDrawn )
    {
        this.id = id;
        this.player = player;
        this.playedCard = playedCard;
        this.cardDrawn = cardDrawn;

        this.activated = false;
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

    public long getId()
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

    public void setId( long id )
    {
        this.id = id;
    }

    public void setPlayer( Player player )
    {
        this.player = player;
    }

    public void setCardDrawn( boolean cardDrawn )
    {
        this.cardDrawn = cardDrawn;
    }
}
