package be.kuleuven.cs.jli40d.server.db.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameMapping
{
    private int serverID;
    private int gameIDOnServer;

    private int gameIDOnDB;

    private List<Integer> players;
    private List<Integer> gameMoves;

    public GameMapping()
    {
    }

    public GameMapping( int serverID, int gameIDOnServer, int gameIDOnDB )
    {
        this.serverID = serverID;
        this.gameIDOnServer = gameIDOnServer;
        this.gameIDOnDB = gameIDOnDB;

        this.players = new ArrayList<>( 4 );
        this.gameMoves = new ArrayList<>();
    }

    public int getServerID()
    {
        return serverID;
    }

    public void setServerID( int serverID )
    {
        this.serverID = serverID;
    }

    public int getGameIDOnServer()
    {
        return gameIDOnServer;
    }

    public void setGameIDOnServer( int gameIDOnServer )
    {
        this.gameIDOnServer = gameIDOnServer;
    }

    public int getGameIDOnDB()
    {
        return gameIDOnDB;
    }

    public void setGameIDOnDB( int gameIDOnDB )
    {
        this.gameIDOnDB = gameIDOnDB;
    }

    public List<Integer> getPlayers()
    {
        return players;
    }

    public void setPlayers( List<Integer> players )
    {
        this.players = players;
    }

    public List<Integer> getGameMoves()
    {
        return gameMoves;
    }

    public void setGameMoves( List<Integer> gameMoves )
    {
        this.gameMoves = gameMoves;
    }

    public int getGameMoveID( int gameMoveID )
    {
        return gameMoves.get( gameMoveID );
    }

    public int getPlayerID( int playerID )
    {
        return players.get( playerID );
    }

    public void addGameMoveID( int serverID, int dbID )
    {
        gameMoves.add( serverID, dbID );
    }

    public void addPlayerID( int serverID, int dbID )
    {
        players.add( serverID, dbID );
    }

}
