package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by Anton D.
 */
@Entity
public class Card implements Serializable
{
    @Id
    private long id;

    private CardType type;
    private CardColour colour;

    public Card()
    {
    }

    public Card( CardType type, CardColour colour )
    {
        this.type = type;
        this.colour = colour;
    }

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public void setType( CardType type )
    {
        this.type = type;
    }

    public void setColour( CardColour colour )
    {
        this.colour = colour;
    }

    public CardType getType()
    {
        return type;
    }

    public CardColour getColour()
    {
        return colour;
    }
}
