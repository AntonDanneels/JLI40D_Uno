package be.kuleuven.cs.jli40d.core.logic;

import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.CardColour;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;

/**
 * Created by Anton D.
 */
public class GameLogic
{
    public boolean testMove( Game game, GameMove move )
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

    public void applyMove( Game game, GameMove move )
    {

    }
}
