package be.kuleuven.cs.jli40d.core.model;

import java.io.Serializable;

/**
 * Lightweight class that only contains some essential information about a game.
 *
 * @author Pieter
 * @version 1.0
 */
public class GameSummary implements Serializable
{
    private int gameID;

    private String name;
    private int numberOfJoinedPlayers;
    private int maximumNumberOfPlayers;
    private boolean started;

    public GameSummary( int gameID, String name, int numberOfJoinedPlayers, int maximumNumberOfPlayers, boolean started )
    {
        this.gameID = gameID;
        this.name = name;
        this.numberOfJoinedPlayers = numberOfJoinedPlayers;
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
        this.started = started;
    }

    public int getGameID()
    {
        return gameID;
    }

    public void setGameID( int gameID )
    {
        this.gameID = gameID;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getMaximumNumberOfPlayers()
    {
        return maximumNumberOfPlayers;
    }

    public void setMaximumNumberOfPlayers( int maximumNumberOfPlayers )
    {
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted( boolean started )
    {
        this.started = started;
    }

    public int getNumberOfJoinedPlayers()
    {
        return numberOfJoinedPlayers;
    }

    public void setNumberOfJoinedPlayers( int numberOfJoinedPlayers )
    {
        this.numberOfJoinedPlayers = numberOfJoinedPlayers;
    }
}
