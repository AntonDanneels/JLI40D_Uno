package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.DatabaseHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import be.kuleuven.cs.jli40d.server.db.repository.TokenRepository;
import be.kuleuven.cs.jli40d.server.db.repository.UserRepository;
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
public class LocalPersistenceService extends UnicastRemoteObject implements DatabaseHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( LocalPersistenceService.class );

    private UserRepository  userRepository;
    private TokenRepository tokenRepository;
    private GameRepository  gameRepository;

    @Autowired
    public LocalPersistenceService( UserRepository userRepository,
                                    TokenRepository tokenRepository,
                                    GameRepository gameRepository ) throws
            RemoteException
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.gameRepository = gameRepository;
    }

    protected LocalPersistenceService() throws RemoteException
    {
    }

    @Override
    public List<Game> getGames() throws RemoteException
    {
        return StreamSupport.stream( gameRepository.findAll().spliterator(), false )
                .collect( Collectors.toList() );
    }

    @Override
    public Game getGame( long id ) throws RemoteException
    {
        return gameRepository.findOne( id );
    }

    @Override
    public void addMove( long gameID, GameMove gameMove ) throws RemoteException
    {

    }

    @Override
    public void registerUser( User user ) throws RemoteException, AccountAlreadyExistsException
    {
        if ( userRepository.findUserByUsernameIgnoreCase( user.getUsername() ) != null )
        {
            LOGGER.warn( "User {} is already registered.", user.getUsername() );
            throw new AccountAlreadyExistsException( "User already found in database cluster" );
        }

        userRepository.save( user );
    }

    @Override
    public String getUsernameForToken( String token ) throws RemoteException
    {
        Token t = tokenRepository.findTokenByToken( token );

        if ( t == null )
        {
            return null;
        }

        return t.getUser().getUsername();
    }

    @Override
    public void registerToken( Token token ) throws RemoteException
    {
        tokenRepository.save( token );
    }

    @Override
    public User findUserByName( String username ) throws RemoteException
    {
        return userRepository.findUserByUsernameIgnoreCase( username );
    }

    @Override
    public List<User> getUsersSortedByScore() throws RemoteException
    {
        return userRepository.findAllByOrderByScoreDesc();
    }

    @Override
    public int registerGame( Game game ) throws RemoteException
    {
        gameRepository.save( game );

        return (int) game.getGameID();
    }
}
