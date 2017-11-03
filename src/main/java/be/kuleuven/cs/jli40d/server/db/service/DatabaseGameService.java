package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
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

    @Autowired
    public DatabaseGameService( GameRepository gameRepository ) throws RemoteException
    {
        this.gameRepository = gameRepository;
    }

    protected DatabaseGameService() throws RemoteException
    {
    }

    @Override
    public List<GameSummary> getGames() throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), true )
                .map( g -> new GameSummary(
                        ( int ) g.getGameID(),
                        g.getName(),
                        g.getNumberOfJoinedPlayers(),
                        g.getMaximumNumberOfPlayers(),
                        g.isStarted() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public Game getGame( int id ) throws RemoteException
    {
        return gameRepository.findOne( id );
    }

    @Override
    public Game saveGame( Game game ) throws RemoteException
    {
        return gameRepository.save( game );
    }

    @Override
    public int addMove( int gameID, GameMove gameMove ) throws RemoteException
    {
        return 0;
    }
}
