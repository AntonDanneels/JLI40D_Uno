package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.server.db.repository.TokenRepository;
import be.kuleuven.cs.jli40d.server.db.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
@Service
public class DatabaseUserService extends UnicastRemoteObject implements DatabaseUserHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DatabaseUserHandler.class );

    private UserRepository  userRepository;
    private TokenRepository tokenRepository;

    @Autowired
    public DatabaseUserService( UserRepository userRepository,
                                TokenRepository tokenRepository ) throws
            RemoteException
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    protected DatabaseUserService() throws RemoteException
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
    public void updateScore( String username, int score ) throws RemoteException
    {
        User u = userRepository.findUserByUsernameIgnoreCase( username );
        u.setScore( u.getScore() + score );
        userRepository.save( u );
    }
}
