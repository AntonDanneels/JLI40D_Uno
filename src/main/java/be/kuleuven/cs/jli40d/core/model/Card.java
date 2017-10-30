package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

    @Enumerated( EnumType.STRING)
    private CardType type;

    @Enumerated( EnumType.STRING)
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

    public void setColour( CardColour colour )
    {
        this.colour = colour;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Card card = ( Card ) o;

        if ( type != card.type ) return false;
        return colour == card.colour;

    }

    @Override
    public int hashCode()
    {
        int result = type.hashCode();
        result = 31 * result + colour.hashCode();
        return result;
    }
}
