package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Created by Anton D.
 */
public class Card implements Serializable
{
    private CardType type;
    private CardColour colour;

    public Card( CardType type, CardColour colour )
    {
        this.type = type;
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
