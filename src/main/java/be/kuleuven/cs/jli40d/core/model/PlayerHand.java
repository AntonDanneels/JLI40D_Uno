package be.kuleuven.cs.jli40d.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * In-between object because 'hibernate can map almost anything', notice the 'almost'.
 *
 * @author Pieter
 * @version 1.0
 */
@Entity
public class PlayerHand
{

    @Id
    private long id;

    @OneToMany
    @MapKey(name = "username")
    private List<Card> playerHands;

    public long getId()
    {
        return id;
    }

    public void setId( long id )
    {
        this.id = id;
    }

    public List<Card> getPlayerHands()
    {
        return playerHands;
    }

    public void setPlayerHands( List<Card> playerHands )
    {
        this.playerHands = playerHands;
    }
}
