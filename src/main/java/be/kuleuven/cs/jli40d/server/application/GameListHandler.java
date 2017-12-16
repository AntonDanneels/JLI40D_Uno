package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public interface GameListHandler extends Serializable
{
    void add( Game game );

    Game getGameByUuid( String uuid ) throws GameNotFoundException, WrongServerException;

    List<GameSummary> getAllGames();
}
