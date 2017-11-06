package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Anton D.
 */
@Entity
public class Player implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int nrOfCards;

    private String username;

    public Player()
    {
    }

    public Player( String username )
    {
        this.username = username;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getNrOfCards()
    {
        return nrOfCards;
    }

    public void setNrOfCards( int nrOfCards )
    {
        this.nrOfCards = nrOfCards;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }
}
