package be.kuleuven.cs.jli40d.core.logic;

import be.kuleuven.cs.jli40d.core.model.*;

/**
 * Created by Anton D.
 */
public class GameLogic
{
    public static boolean testMove( Game game, GameMove move )
    {
        if( move.isCardDrawn() )
            return true;

        Card currentCard  = game.getTopCard();
        Card playedCard = move.getPlayedCard();

        if( playedCard.getColour() == CardColour.NO_COLOUR )
        {

        }

        if( currentCard.getColour() == playedCard.getColour() || currentCard.getType() == playedCard.getType() )
            return true;

        return true;
    }

    /**
     * Static method that applies the necessary state changes to a {@link Game} object,
     * given a {@link GameMove}.
     *
     * This also adds the {@link GameMove} to the list of moves in the {@link Game}.
     *
     * @param game
     * @param move
     */
    public static void applyMove( Game game, GameMove move )
    {
        if( move.isCardDrawn() )
        {

        }
        else
        {
            Card playedCard = move.getPlayedCard();
            game.setTopCard( playedCard );

            if( playedCard.getType() == CardType.REVERSE )
                game.setClockwise( !game.isClockwise() );

            if( playedCard.getType() == CardType.SKIP )
                game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );

            if( playedCard.getType() == CardType.PLUS2 )
            {
                int nextPlayer = wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() );
                Player target = game.getPlayers().get( nextPlayer );
                target.setNrOfCards( target.getNrOfCards() + 2 );
            }

            if( playedCard.getType() == CardType.PLUS4 )
            {
                int nextPlayer = wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() );
                Player target = game.getPlayers().get( nextPlayer );
                target.setNrOfCards( target.getNrOfCards() + 4 );
            }

            game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );
        }

        //finally add the move
        game.addLatestMove( move );
    }

    private static int wrap( int current, boolean clockwise, int max )
    {
        int step = (clockwise ? 1 : -1 );
        int result = current + step;

        if( result < 0 )
            result = max - 1;
        if( result == max )
            result = 0;

        return result;
    }
}
