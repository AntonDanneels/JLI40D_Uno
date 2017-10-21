package be.kuleuven.cs.jli40d.core.model;

/**
 * Created by Anton D.
 */
public class GameMove
{
    private int id;
    private Player player;
    private Card playedCard;
    private boolean cardDrawn;

    public GameMove( int id, Player player, Card playedCard, boolean cardDrawn )
    {
        this.id = id;
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

    public int getId()
    {
        return id;
    }
}
