package be.kuleuven.cs.jli40d.core.model;

/**
 * Created by Anton D.
 */
public class GameMove
{
    private Player player;
    private Card playedCard;
    private boolean cardDrawn;

    public GameMove( Player player, Card playedCard, boolean cardDrawn )
    {
        this.player = player;
        this.playedCard = playedCard;
        this.cardDrawn = cardDrawn;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Card getPlayedCard()
    {
        return playedCard;
    }

    public boolean isCardDrawn()
    {
        return cardDrawn;
    }
}
