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
}
