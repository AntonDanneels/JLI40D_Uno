package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.PlayerHand;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class takes a game related objects and returns copies with stripped out info.
 * Created by Anton D.
 */
public class ObfuscatorService
{
    public Game changeGame( Game game, String username )
    {
        Game result = new Game( game.getGameID(), game.getPlayers(), game.getMoves(),
                                new ArrayList<Card>(), new HashMap<String, PlayerHand>(),
                                game.getName(), game.getMaximumNumberOfPlayers(),
                                game.getTopCard(), game.isStarted(), game.isEnded(),
                                game.getCurrentPlayer(), game.getCurrentGameMoveID(),
                                game.isClockwise() );
        result.getPlayerHands().put( username, game.getPlayerHands().get( username ) );

        return result;
    }
}
