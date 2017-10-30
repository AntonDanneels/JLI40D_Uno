package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public interface GameListHandler extends Serializable
{
    void add( Game game );

    int nextID();

    Game getGameByID( long id ) throws GameNotFoundException;

    List<Game> getAllGames();
}
