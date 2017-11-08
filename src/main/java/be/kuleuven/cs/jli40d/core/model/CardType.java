package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Created by Anton D.
 */
public enum CardType implements Serializable
{
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), REVERSE(20), SKIP(20), PLUS2(20), PLUS4(50), OTHER_COLOUR(50);

    private int value;

    private CardType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue( int value )
    {
        this.value = value;
    }
}
