package be.kuleuven.cs.jli40d.core.model;

/**
 * Created by Anton D.
 */
public class Player
{
    private int ID;
    private int nrOfCards;

    public Player( int ID )
    {
        this.ID = ID;
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
}
