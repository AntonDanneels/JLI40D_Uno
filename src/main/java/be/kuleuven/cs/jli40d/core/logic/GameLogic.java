package be.kuleuven.cs.jli40d.core.logic;

import be.kuleuven.cs.jli40d.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Anton D.
 */
public class GameLogic
{
    private static final Logger LOGGER = LoggerFactory.getLogger( GameLogic.class );

    public static void generateDeck( Game game )
    {
        int counter = 0;

        game.getDeck().clear();

        for ( CardType type : CardType.values() )
        {
            if ( type != CardType.OTHER_COLOUR && type != CardType.PLUS4 )
                for ( CardColour colour : CardColour.values() )
                {
                    if ( colour != CardColour.NO_COLOUR )
                    {
                        Card card = new Card( counter++, type, colour );
                        game.getDeck().add( card );

                        if ( type != CardType.ZERO )
                        {
                            Card extra = new Card( counter++, type, colour );
                            game.getDeck().add( extra );
                        }
                    }

                }
        }

        for ( int i = 0; i < 4; i++ )
        {
            game.getDeck().add( new Card( counter++, CardType.OTHER_COLOUR, CardColour.NO_COLOUR ) );
            game.getDeck().add( new Card( counter++, CardType.PLUS4, CardColour.NO_COLOUR ) );
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

        Map<String, PlayerHand> cardsPerPlayer = game.getPlayerHands();
        for ( int j = 0; j < game.getPlayers().size(); j++ )
            cardsPerPlayer.put( game.getPlayers().get( j ).getUsername(), new PlayerHand() );

        for ( int i = 0; i < 7; i++ )
        {
            for ( Player player : game.getPlayers() )
            {
                //cardsPerPlayer.get( player.getUsername() ).getPlayerHands().add( game.getDeck().get( index++ ) );
                GameMove move = new GameMove( game.getCurrentGameMoveID(), player, null, true );
                game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
                game.addLatestMove( move );
                GameLogic.applyMove( game, move );
                player.setNrOfCards( player.getNrOfCards() + 1 );
            }
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

            if ( game.getTopCard().getColour() == CardColour.NO_COLOUR || game.getTopCard().getType() == CardType.PLUS2
                    || game.getTopCard().getType() == CardType.REVERSE || game.getTopCard().getType() == CardType.SKIP )
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

        if ( playedCard.getType() == CardType.OTHER_COLOUR || playedCard.getType() == CardType.PLUS4 )
        {
            return true;
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
            giveCardToPlayer( game, move );

            LOGGER.debug( "card drawn move applied: {}:{} to {}",
                    move.getPlayedCard().getColour(),
                    move.getPlayedCard().getType(),
                    move.getPlayer().getUsername() );

            if ( !move.isActivated() )
            {
                game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );
            }
        }
        else
        {
            Card playedCard = move.getPlayedCard();
            game.getDeck().add( game.getTopCard() );
            Collections.shuffle( game.getDeck() );
            game.setTopCard( playedCard );

            for ( Player p : game.getPlayers() )
            {
                if ( p.getUsername().equals( move.getPlayer().getUsername() ) )
                {
                    game.getPlayerHands().get( p.getUsername() ).getPlayerHands()
                            .removeIf( c -> c.getId() == move.getPlayedCard().getId() );
                }
            }

            //Add the game move
            game.addLatestMove( move );

            if ( playedCard.getType() == CardType.REVERSE )
                game.setClockwise( !game.isClockwise() );

            if ( playedCard.getType() == CardType.SKIP )
                game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );

            if ( ( playedCard.getType() == CardType.PLUS2 || playedCard.getType() == CardType.PLUS4 ) && !move.isActivated() )
            {
                int    nextPlayer = wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() );
                Player target     = game.getPlayers().get( nextPlayer );

                move.setActivated( true );

                for ( int i = 0; i < ( playedCard.getType() == CardType.PLUS2 ? 2 : 4 ); i++ )
                {
                    GameMove m = new GameMove( game.getCurrentGameMoveID(), target, null, true );
                    m.setActivated( true );
                    giveCardToPlayer( game, m );
                }
            }

            game.setCurrentPlayer( wrap( game.getCurrentPlayer(), game.isClockwise(), game.getPlayers().size() ) );
        }

    }

    public static int wrap( int current, boolean clockwise, int max )
    {
        int step   = ( clockwise ? 1 : -1 );
        int result = current + step;

        if ( result < 0 )
            result = max - 1;
        if ( result == max )
            result = 0;

        return result;
    }

    private static void giveCardToPlayer( Game game, GameMove move )
    {

        if ( game.getCardsPerPlayer().containsKey( move.getPlayer().getUsername() ) )
        {
            //take a card from the deck if there is no card given
            if ( move.getPlayedCard() == null )
            {
                Card c = game.getDeck().remove( 0 );
                move.setPlayedCard( c );
            }

            //and give it to the player
            Player target = move.getPlayer();

            game.getPlayerHands().get( target.getUsername() ).getPlayerHands().add( move.getPlayedCard() );
            target.setNrOfCards( target.getNrOfCards() + 1 );

            LOGGER.debug( "Given card {}:{} to {}",
                    move.getPlayedCard().getColour(),
                    move.getPlayedCard().getType(),
                    move.getPlayer().getUsername() );

            game.addLatestMove( move );

        }
    }

    public static boolean hasGameEnded( Game game )
    {
        for ( Player p : game.getPlayers() )
        {
            if ( game.getPlayerHands().get( p.getUsername() ).getPlayerHands().size() == 0 )
                return true;
        }

        return false;
    }

    public static Player getWinner( Game game )
    {
        for ( Player p : game.getPlayers() )
        {
            if( game.getPlayerHands().get( p.getUsername() ).getPlayerHands().size() == 0 )
                return p;
        }

        LOGGER.debug( "Asked for the winning player even thought the game hasn't ended yet." );
        return null;
    }

    public static int calculateScoreForPlayer( String username, Game game )
    {
        int score = 0;
        for ( Player p : game.getPlayers() )
        {
            if( !p.getUsername().equals( username ) )
            {
                for ( Card c : game.getPlayerHands().get( p.getUsername() ).getPlayerHands() )
                    score += c.getType().getValue();
            }
        }

        return score;
    }
}
