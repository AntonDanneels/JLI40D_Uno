package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.db.model.GameMapping;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * To provide a brigde between gameserved id space and db cluster id space,
 * this class provides a set transformation functions to transform {@link Game},
 * {@link Player} and {@link GameMove} id's.
 *
 * @author Pieter
 * @version 1.0
 */
@Service
public class IDTranslationService
{
    private Map<Integer, Map<Integer, GameMapping>> gameMappingsForServers = new HashMap<>();

    public IDTranslationService()
    {
    }

    /**
     * Returns a game id for the db space, based on the server id and game id in game server space.
     *
     * @param serverID
     * @param gameID
     * @return
     * @throws GameNotFoundException
     */
    public int translateToGameID( int serverID, int gameID ) throws GameNotFoundException
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getGameIDOnDB();
    }

    public int translateToGameMoveID( int serverID, int gameID, int gameMoveID ) throws GameNotFoundException
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getGameMoveID( gameMoveID );
    }

    public int translateToPlayerID( int serverID, int gameID, int playerID ) throws GameNotFoundException
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getPlayerID( playerID );
    }

    public void addGame( int serverID, int gameID, int gameIDOnDB )
    {
        //check is the server exists
        if ( !gameMappingsForServers.containsKey( serverID ) )
        {
            gameMappingsForServers.put( serverID, new HashMap<>() );
        }

        gameMappingsForServers.get( serverID ).put( gameID, new GameMapping( serverID, gameID, gameIDOnDB ) );
    }

    public void addGameMoveID( int serverID, int gameID, int gameMoveID, int gameMoveIDOnDB )
    {
        gameMappingsForServers.get( serverID ).get( gameID ).addGameMoveID( gameMoveID, gameMoveIDOnDB );
    }

    public void addPlayerID( int serverID, int gameID, int playerID, int playerIDOnDB )
    {
        gameMappingsForServers.get( serverID ).get( gameID ).addGameMoveID( playerID, playerIDOnDB );
    }

}
