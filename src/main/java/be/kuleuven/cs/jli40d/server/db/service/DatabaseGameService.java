package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.GameSummary;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.model.exception.GameNotFoundException;
import be.kuleuven.cs.jli40d.server.db.repository.GameMoveRepository;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import be.kuleuven.cs.jli40d.server.db.repository.PlayerRepository;
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

    private GameRepository     gameRepository;
    private PlayerRepository   playerRepository;
    private GameMoveRepository gameMoveRepository;
    private ClusterService     clusterService;

    @Autowired
    public DatabaseGameService( GameRepository gameRepository,
                                PlayerRepository playerRepository,
                                GameMoveRepository gameMoveRepository,
                                ClusterService clusterService ) throws RemoteException
    {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gameMoveRepository = gameMoveRepository;
        this.clusterService = clusterService;
    }


    protected DatabaseGameService() throws RemoteException
    {
    }

    @Override
    public synchronized List <GameSummary> getGames() throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), true )
                .map( g -> new GameSummary(
                        g.getUuid(),
                        g.getName(),
                        g.getNumberOfJoinedPlayers(),
                        g.getMaximumNumberOfPlayers(),
                        g.isStarted() ) )
                .collect( Collectors.toList() );
    }


    @Override
    public Game getGame( int serverID, String gameUuid ) throws RemoteException, GameNotFoundException
    {

        return gameRepository.findOneByUuid( gameUuid );
    }

    @Override
    public synchronized void saveGame( int serverID, Game game ) throws RemoteException
    {

        //remove gameID
        int originalGameID = game.getGameID();
        game.setGameID( 0 );

        //remove player ids
        for ( Player p : game.getPlayers() )
        {
            playerRepository.findOneByUuid( p.getUuid() );

            p.setId( p.getId() );
        }

        //remove gamemove ids
        /*for ( GameMove gm : game.getMoves() )
        {
            gm.setId( translationService.translateToGameMoveID( serverID, originalGameID, gm.getId() ) );
        }*/

        LOGGER.info( "Saving game with id {}", originalGameID );

        Game g = gameRepository.findOneByUuid( game.getUuid() );
        if ( g != null )
        {
            game.setGameID( g.getGameID() );
            gameRepository.save( game );

        }
        else
        {
            gameRepository.save( game );

            int dbID = game.getGameID();

            LOGGER.info( "Game not found, persisting as new entity with db id = {} and propagating to cluster.", dbID );
            clusterService.addGame( game );
        }

    }

    @Override
    public synchronized void addMove( int serverID, String gameUuid, GameMove gameMove ) throws RemoteException
    {
        LOGGER.info( "Adding move {}", gameMove.getId() );

        //clearing ids
        int originalMoveID = gameMove.getId();
        gameMove.setId( 0 );

        //remove player id
        gameMove.setPlayer( playerRepository.findOneByUuid( gameMove.getPlayer().getUuid() ) );

        //step 1. save game move
        int dbID = gameMoveRepository.save( gameMove ).getId();

        //step 2. save game as well
        Game g = gameRepository.findOneByUuid( gameUuid );
        g.getMoves().add( gameMove );

        gameRepository.save( g );
    }

    /**
     * Add a list {@link GameMove}  objects to a {@link Game} object, specified by both
     * the serverID and the gameID.
     *
     * @param serverID  The id provided by the server as an int.
     * @param gameUuid  The id of the {@link Game} as seen by the application server.
     * @param gameMoves A list of {@link GameMove} objects.
     * @throws RemoteException
     */
    @Override
    public synchronized void addMoves( int serverID, String gameUuid, List <GameMove> gameMoves ) throws RemoteException
    {
        for ( GameMove gameMove : gameMoves )
        {
            addMove( serverID, gameUuid, gameMove );
        }
    }

    @Override
    public synchronized void addPlayer( int serverID, String gameUuid, Player player ) throws RemoteException
    {
        int originalPlayerID = player.getId();
        player.setId( 0 );

        LOGGER.info( "Saving player {} with original id = {} from server {}", player.getUsername(), originalPlayerID, serverID );

        Game g = gameRepository.findOneByUuid( gameUuid );

        //step 1. save player
        int dbID = playerRepository.save( player ).getId();

        g.getPlayers().add( player );
        gameRepository.save( g );

    }
}
