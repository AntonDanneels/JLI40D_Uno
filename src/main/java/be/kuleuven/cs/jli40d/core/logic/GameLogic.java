package be.kuleuven.cs.jli40d.core.logic;

import be.kuleuven.cs.jli40d.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Anton D.
 */
public class GameLogic
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GameLogic.class );

    public static void generateDeck( Game game )
    {
        game.getDeck().clear();

        CardType[]   types   = CardType.values();
        CardColour[] colours = CardColour.values();
        for ( int i = 0; i < types.length; i++ )
        {
            for ( int j = 0; j < colours.length; j++ )
            {
                if ( colours[ j ] != CardColour.NO_COLOUR && types[ i ] != CardType.OTHER_COLOUR && types[ i ] != CardType.PLUS4 )
                {
                    CardType   type   = types[ i ];
                    CardColour colour = colours[ j ];
                    Card       card   = new Card( type, colour );
                    game.getDeck().add( card );

                    if ( types[ i ] != CardType.ZERO )
                    {
                        Card extra = new Card( type, colour );
                        game.getDeck().add( extra );
                    }
                }
            }
        }

        for ( int i = 0; i < 4; i++ )
        {
            game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.NO_COLOUR ) );
            game.getDeck().add( new Card( CardType.PLUS4, CardColour.NO_COLOUR ) );
        }

        Collections.shuffle( game.getDeck() );
    }

    /**
     * Distribute the cards amongst the players in the list of players.
     *
     * @param game
     */
    public static void distributeCards( Game game )
    {
        int index = 0;

        Map<Player, List<Card>> cardsPerPlayer = game.getCardsPerPlayer();
        for ( int j = 0; j < game.getPlayers().size(); j++ )
            cardsPerPlayer.put( game.getPlayers().get( j ), new ArrayList<>() );
        for ( int i = 0; i < 7; i++ )
        {
            for ( Player player : game.getPlayers() )
                cardsPerPlayer.get( player ).add( game.getDeck().get( index++ ) );
        }
        //game.setDeck( game.getDeck().subList( 0, index ) );
        Iterator it      = game.getDeck().iterator();
        int      removed = 0;
        while ( removed < index )
        {
            it.next();
            it.remove();
            removed++;
        }

        game.setStarted( true );
    }

    /**
     * If the top card is not set, set the top card to a {@link Card}
     * of the deck and remove that card from the deck.
     *
     * @param game The subjected {@link Game} object.
     */
    public static void putInitialCardInTheMiddle( Game game )
    {
        if ( game.getTopCard() == null )
        {
            game.setTopCard( game.getDeck().get( 0 ) );
            game.getDeck().remove( 0 );

            if ( game.getTopCard().getColour() == CardColour.NO_COLOUR )
            {
                game.getDeck().add( game.getTopCard() );
                game.setTopCard( null );
                putInitialCardInTheMiddle( game );
            }

        }
    }

    public static boolean testMove( Game game, GameMove move )
    {
        if ( move.isCardDrawn() )
            return true;

        Card currentCard = game.getTopCard();
        Card playedCard  = move.getPlayedCard();

        if ( playedCard.getColour() == CardColour.NO_COLOUR )
        {

        }

        if ( currentCard.getColour() == playedCard.getColour() || currentCard.getType() == playedCard.getType() )
            return true;

        return false;
    }

    /**
     * Static method that applies the necessary state changes to a {@link Game} object,
     * given a {@link GameMove}.
     * <p>
     * This also adds the {@link GameMove} to the list of moves in the {@link Game}.
     *
     * @param game
     * @param move
     */
    public static void applyMove( Game game, GameMove move )
    {
        if ( move.isCardDrawn() )
        {
            //if a card is drawn, add it to the deck of the player (and generate one if needed)
            if ( move.getPlayedCard() == null )
            {
                Card c = game.getDeck().remove( 0 );
                move.setPlayedCard( c );
            }

            for ( Player p : game.getCardsPerPlayer().keySet() )
            {
                if ( p.getUsername().equals( move.getPlayer().getUsername() ) )
                    game.getCardsPerPlayer().get( p ).add( move.getPlayedCard() );
            }

            LOGGER.debug( "card drawn move applied: {}:{} to {}",
                    move.getPlayedCard().getColour(),
                    move.getPlayedCard().getType(),
                    move.getPlayer().getUsername() );
        }
        else
        {
            Card playedCard = move.getPlayedCard();
            game.getDeck().add( game.getTopCard() );
            Collections.shuffle( game.getDeck() );
            game.setTopCard( playedCard );

            // Note: game.getCardsPerPlayer().get( move.getPlayer() ) does not works bc of
            // a serialization issue. Isn't RMI the most wonderful technology in existence?
            for ( Player p : game.getCardsPerPlayer().keySet() )
            {
                if ( p.getUsername().equals( move.getPlayer().getUsername() ) )
                {
                    Iterator<Card> it = game.getCardsPerPlayer().get( p ).iterator();
                    while ( it.hasNext() )
                    {
                        Card c = it.next();
                        if ( c.getColour() == move.getPlayedCard().getColour() && c.getType() == move.getPlayedCard().getType() )
                            it.remove();
                    }
                }
            }

            if ( playedCard.getType() == CardType.REVERSE )
                game.setClockwise( !game.isClockwise() );

            if ( playedCard.getType() == CardType.SKIP )
                game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );

            if ( playedCard.getType() == CardType.PLUS2 )
            {
                int    nextPlayer = wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() );
                Player target     = game.getPlayers().get( nextPlayer );
                target.setNrOfCards( target.getNrOfCards() + 2 );

                move = new GameMove( game.getCurrentGameMoveID(), target, null, true );
                GameLogic.applyMove( game, move );
                move = new GameMove( game.getCurrentGameMoveID(), target, null, true );
                GameLogic.applyMove( game, move );
            }

            if ( playedCard.getType() == CardType.PLUS4 )
            {
                int    nextPlayer = wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() );
                Player target     = game.getPlayers().get( nextPlayer );
                target.setNrOfCards( target.getNrOfCards() + 4 );
            }

            game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );
        }

        //finally add the move
        game.addLatestMove( move );
    }

    private static int wrap( int current, boolean clockwise, int max )
    {
        int step   = ( clockwise ? 1 : -1 );
        int result = current + step;

        if ( result < 0 )
            result = max - 1;
        if ( result == max )
            result = 0;

        return result;
    }
}
