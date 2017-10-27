package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Anton D.
 */
@Entity
public class Player implements Serializable
{
    @Id
    private int ID;
    private int nrOfCards;

    private String username;

    public Player( int ID, String username )
    {
        this.ID = ID;
        this.username = username;
    }

    public int getID()
    {
        return ID;
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
}
