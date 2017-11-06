package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Pieter
 * @version 1.0
 */
@Service
public class DatabaseGameService extends UnicastRemoteObject implements DatabaseGameHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DatabaseUserHandler.class );

    private GameRepository gameRepository;

    private Map<Integer, Map<Integer, Integer>> gameMappingsForServers = new HashMap<>();

    private Map<Integer, Integer> playersForGames = new HashMap<>();
    private Map<Integer, Integer> gameMovesForGames = new HashMap<>();


    @Autowired
    public DatabaseGameService( GameRepository gameRepository ) throws RemoteException
    {
        this.gameRepository = gameRepository;
    }

    protected DatabaseGameService() throws RemoteException
    {
    }

    /**
     * Registers an application server to the database.
     *
     * @return An int with the application server id.
     * @throws RemoteException
     */
    @Override
    public synchronized int registerServer() throws RemoteException
    {
        int nextServerID = gameMappingsForServers.size();

        gameMappingsForServers.put( nextServerID, new HashMap<>() );

        LOGGER.info( "Registering application server with id {}", nextServerID );

        return nextServerID;
    }

    @Override
    public List<GameSummary> getGames() throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), true )
                .map( g -> new GameSummary(
                        g.getGameID(),
                        g.getName(),
                        g.getNumberOfJoinedPlayers(),
                        g.getMaximumNumberOfPlayers(),
                        g.isStarted() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public Game getGame( int serverID, int gameID ) throws RemoteException
    {
        int realGameID = gameMappingsForServers.get( serverID ).get( gameID );

        return gameRepository.findOne( realGameID );
    }

    @Override
    public void saveGame( int serverID, Game game ) throws RemoteException
    {
        gameRepository.save( game );
    }

    @Override
    public void addMove( int serverID, int gameID, GameMove gameMove ) throws RemoteException
    {
    }

    @Override
    public void addPlayer( int serverID, int gameID, Player player ) throws RemoteException
    {

    }

    @Override
    public Game getGame( int id ) throws RemoteException
    {
        return gameRepository.findOne( id );
    }

    @Override
    public Game saveGame( Game game ) throws RemoteException
    {
        LOGGER.debug( "saving game with players: {}", game.getPlayers() );
        return gameRepository.save( game );
    }

    @Override
    public int addMove( int gameID, GameMove gameMove ) throws RemoteException
    {
        Game game = gameRepository.findOne( gameID );

        game.addLatestMove( gameMove );

        gameRepository.save( game );

        return game.getMoves().size();
    }
}
