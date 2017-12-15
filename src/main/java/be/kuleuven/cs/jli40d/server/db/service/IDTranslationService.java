package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.db.model.GameMapping;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @deprecated
 */
@Service
public class IDTranslationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( IDTranslationService.class );

    private Map<Integer, Map<Integer, GameMapping>> gameMappingsForServers = new HashMap<>();

    private Map<Integer, GameMapping> DBGameIDMappings = new HashMap<>();

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
    public int translateToGameID( int serverID, int gameID )
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getGameIDOnDB();
    }

    public int translateToGameMoveID( int serverID, int gameID, int gameMoveID )
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getGameMoveID( gameMoveID );
    }

    public int translateToPlayerID( int serverID, int gameID, int playerID )
    {
        return gameMappingsForServers.get( serverID ).get( gameID ).getPlayerID( playerID );
    }

    public boolean containsGameID( int serverID, int gameID )
    {
        return gameMappingsForServers.containsKey( serverID ) && gameMappingsForServers.get( serverID ).containsKey( gameID );
    }

    public void addGame( int serverID, int gameID, int gameIDOnDB )
    {
        //check is the server exists
        if ( !gameMappingsForServers.containsKey( serverID ) )
        {
            gameMappingsForServers.put( serverID, new HashMap<>() );
        }

        GameMapping gameMapping = new GameMapping( serverID, gameID, gameIDOnDB );

        gameMappingsForServers.get( serverID ).put( gameID, gameMapping );

        DBGameIDMappings.put( gameIDOnDB, gameMapping );

    }

    public void addGameMoveID( int serverID, int gameID, int gameMoveID, int gameMoveIDOnDB )
    {
        LOGGER.info( "Added gamemove id: server = {}, game = {}, gameMove = {} -> {}",
                serverID,
                gameID,
                gameMoveID,
                gameMoveIDOnDB );

        gameMappingsForServers.get( serverID ).get( gameID ).addGameMoveID( gameMoveID, gameMoveIDOnDB );
    }

    public synchronized void addPlayerID( int serverID, int gameID, int playerID, int playerIDOnDB )
    {
        LOGGER.info( "Added player id: server = {}, game = {}, player = {} -> {}",
                serverID,
                gameID,
                playerID,
                playerIDOnDB );

        gameMappingsForServers.get( serverID ).get( gameID ).addPlayerID( playerID, playerIDOnDB );

        notifyAll();
    }

    public int addServer()
    {
        int serverID = gameMappingsForServers.size();

        gameMappingsForServers.put( serverID, new HashMap<>() );

        return serverID;
    }

    /**
     * Returns a {@link Pair} pair with K (the first) as the server id and
     * V (second) as the game ID.
     *
     * @param gameIDOnDB
     * @return
     */
    public Pair<Integer, Integer> translateFromGameID( int gameIDOnDB )
    {
        GameMapping gameMapping = DBGameIDMappings.get( gameIDOnDB );

        LOGGER.info( "Translating game from id. {}", gameMapping );

        return new Pair<>( gameMapping.getServerID(), gameMapping.getGameIDOnServer() );

    }

}
