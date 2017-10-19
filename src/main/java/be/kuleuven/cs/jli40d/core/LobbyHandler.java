package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Game;

import java.util.List;

/**
 * The lobby has three main functions:
 * <ul>
 *     <li>Viewing a list of all games.</li>
 *     <li>Making a new game.</li>
 *     <li>Joining a existing game (either as spectator or player).</li>
 * </ul>
 */
public interface LobbyHandler
{
    List<Game> currentGames();

    int makeGame();

    Game joinGame();
}
