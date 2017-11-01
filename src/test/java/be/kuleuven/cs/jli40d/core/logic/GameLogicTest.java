package be.kuleuven.cs.jli40d.core.logic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameLogicTest
{
    @Test
    public void clockwise() throws Exception
    {
        assertEquals( 1, GameLogic.wrap( 0, true, 4 ));
        assertEquals( 2, GameLogic.wrap( 1, true, 4 ));
        assertEquals( 3, GameLogic.wrap( 2, true, 4 ));
        assertEquals( 0, GameLogic.wrap( 3, true, 4 ));
    }

    @Test
    public void counterClockwise() throws Exception
    {
        assertEquals( 3, GameLogic.wrap( 0, false, 4 ));
        assertEquals( 0, GameLogic.wrap( 1, false, 4 ));
        assertEquals( 1, GameLogic.wrap( 2, false, 4 ));
        assertEquals( 2, GameLogic.wrap( 3, false, 4 ));
    }
}