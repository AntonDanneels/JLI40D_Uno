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

    private GameRepository       gameRepository;
    private PlayerRepository     playerRepository;
    private GameMoveRepository   gameMoveRepository;
    private IDTranslationService translationService;

    @Autowired
    public DatabaseGameService( GameRepository gameRepository,
                                PlayerRepository playerRepository,
                                GameMoveRepository gameMoveRepository,
                                IDTranslationService translationService ) throws
            RemoteException
    {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gameMoveRepository = gameMoveRepository;
        this.translationService = translationService;
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
        int nextServerID = translationService.addServer();

        LOGGER.info( "Registering application server with id {}", nextServerID );

        return nextServerID;
    }

    @Override
    public synchronized List<GameSummary> getGames() throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), true )
                .map( g -> new GameSummary(
                        translationService.translateFromGameID( g.getGameID() ).getValue(),
                        g.getName(),
                        g.getNumberOfJoinedPlayers(),
                        g.getMaximumNumberOfPlayers(),
                        g.isStarted() ) )
                .collect( Collectors.toList() );
    }

    /**
     * Like {@link #getGames()}, this returns a list with {@link GameSummary} objects,
     * but removes the games hosted by the provided server.
     *
     * @param serverID The id provided by {@link #registerServer()} as an int.
     * @return A {@link List} with {@link GameSummary} objects, filtered to remove those hosted by one server.
     * @throws RemoteException
     */
    @Override
    public List<GameSummary> getGames( int serverID ) throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), true )
                .filter( g -> serverID != translationService.translateFromGameID( g.getGameID() ).getKey() )
                .map( g -> new GameSummary(
                        translationService.translateFromGameID( g.getGameID() ).getValue(),
                        g.getName(),
                        g.getNumberOfJoinedPlayers(),
                        g.getMaximumNumberOfPlayers(),
                        g.isStarted() ) )
                .collect( Collectors.toList() );
    }

    @Override
    public Game getGame( int serverID, int gameID ) throws RemoteException, GameNotFoundException
    {
        int realGameID = translationService.translateToGameID( serverID, gameID );

        return gameRepository.findOne( realGameID );
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
            p.setId( translationService.translateToPlayerID( serverID, originalGameID, p.getId() ) );
        }

        //remove gamemove ids
        for ( GameMove gm : game.getMoves() )
        {
            gm.setId( translationService.translateToGameMoveID( serverID, originalGameID, gm.getId() ) );
        }

        LOGGER.info( "Saving game with id {}", originalGameID );

        if ( translationService.containsGameID( serverID, originalGameID ) )
        {
            game.setGameID( translationService.translateToGameID( serverID, originalGameID ) );
            gameRepository.save( game );

        }
        else
        {
            gameRepository.save( game );

            int dbID = game.getGameID();
            translationService.addGame( serverID, originalGameID, dbID );

            LOGGER.info( "Game not found, persisting as new entity with db id = {}.", dbID );
        }

    }

    @Override
    public synchronized void addMove( int serverID, int gameID, GameMove gameMove ) throws RemoteException
    {
        //clearing ids
        int originalMoveID = gameMove.getId();
        gameMove.setId( 0 );

        //remove player id
        gameMove.getPlayer().setId( translationService.translateToPlayerID( serverID, gameID, gameMove.getPlayer().getId() ) );

        //step 1. save game move
        int dbID = gameMoveRepository.save( gameMove ).getId();

        //step 2. save game as well
        Game g = gameRepository.findOne( translationService.translateToGameID( serverID, gameID ) );
        g.getMoves().add( gameMove );

        gameRepository.save( g );

        translationService.addGameMoveID( serverID, gameID, originalMoveID, dbID );
    }

    /**
     * Add a list {@link GameMove}  objects to a {@link Game} object, specified by both
     * the serverID and the gameID.
     *
     * @param serverID  The id provided by {@link #registerServer()} as an int.
     * @param gameID    The id of the {@link Game} as seen by the application server.
     * @param gameMoves A list of {@link GameMove} objects.
     * @throws RemoteException
     */
    @Override
    public synchronized void addMoves( int serverID, int gameID, List<GameMove> gameMoves ) throws RemoteException
    {
        for ( GameMove gameMove : gameMoves )
        {
            addMove( serverID, gameID, gameMove );
        }
    }

    @Override
    public synchronized void addPlayer( int serverID, int gameID, Player player ) throws RemoteException
    {
        int originalPlayerID = player.getId();
        player.setId( 0 );

        Game g = gameRepository.findOne( translationService.translateToGameID( serverID, gameID ) );
        g.getPlayers().add( player );

        gameRepository.save( g );

        translationService.addPlayerID( serverID, gameID, originalPlayerID, player.getId() );
    }
}
