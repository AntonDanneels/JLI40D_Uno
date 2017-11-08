package be.kuleuven.cs.jli40d.core.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Anton D.
 */
@Entity
public class GameMove implements Serializable
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private int id;

    @ManyToOne
    @Cascade( CascadeType.SAVE_UPDATE )
    private Player player;

    @ManyToOne
    private Card playedCard;

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

    public void setId( int id )
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

    @Override
    public String toString()
    {
        return "GameMove{" +
                "id=" + id +
                ", player=" + player +
                ", playedCard=" + playedCard +
                ", cardDrawn=" + cardDrawn +
                ", activated=" + activated +
                '}';
    }
}
