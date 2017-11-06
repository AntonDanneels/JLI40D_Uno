package be.kuleuven.cs.jli40d.server.db.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameMapping
{
    private int serverID;
    private int gameIDOnServer;

    private int gameIDOnDB;

    private Map<Integer, Integer>  players;
    private Map<Integer, Integer> gameMoves;

    public GameMapping()
    {
    }

    public GameMapping( int serverID, int gameIDOnServer, int gameIDOnDB )
    {
        this.serverID = serverID;
        this.gameIDOnServer = gameIDOnServer;
        this.gameIDOnDB = gameIDOnDB;

        this.players = new HashMap<>( 4 );
        this.gameMoves = new HashMap<>();
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

    public Map<Integer, Integer> getPlayers()
    {
        return players;
    }

    public void setPlayers( Map<Integer, Integer> players )
    {
        this.players = players;
    }

    public Map<Integer, Integer> getGameMoves()
    {
        return gameMoves;
    }

    public void setGameMoves( Map<Integer, Integer> gameMoves )
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
        gameMoves.put( serverID, dbID );
    }

    public void addPlayerID( int serverID, int dbID )
    {
        players.put( serverID, dbID );
    }

}
