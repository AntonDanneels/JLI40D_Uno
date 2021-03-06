package be.kuleuven.cs.jli40d.core.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * In-between object because 'hibernate can map almost anything', notice the 'almost'.
 *
 * @author Pieter
 * @version 1.0
 */
@Entity
public class PlayerHand implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany( cascade = CascadeType.MERGE )
    @LazyCollection( LazyCollectionOption.FALSE )
    private List<Card> playerHands;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public List<Card> getPlayerHands()
    {
        return playerHands;
    }

    public PlayerHand()
    {
        this.playerHands = new ArrayList<>();
    }

    public void setPlayerHands( List<Card> playerHands )
    {
        this.playerHands = playerHands;
    }
}
