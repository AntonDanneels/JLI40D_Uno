package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Created by Anton D.
 */
public class Player implements Serializable
{
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
