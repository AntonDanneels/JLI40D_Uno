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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Pieter
 * @version 1.0
 */
@Service
public class DatabaseUserService extends UnicastRemoteObject implements DatabaseUserHandler, UserCommitHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DatabaseUserHandler.class );

    private UserRepository  userRepository;
    private TokenRepository tokenRepository;

    //user commit params
    private AtomicBoolean lock = new AtomicBoolean( false );

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
    public List <User> getUsersSortedByScore() throws RemoteException
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

    /**
     * Prepares the remote database for a commit of a {@link User}.
     * <p>
     * This takes a lock on the database, preventing other operations to read by blocking
     * them or write (by returning {@link PrepareResponse#ABORT}).
     * <p>
     * If {@link PrepareResponse#ABORT} is returned, this means no lock could be obtained.
     * Therefore, it is also unnecessary to call {@link #forget()} later on.
     *
     * @param username The username of the user to insert, as a String.
     * @return {@link PrepareResponse#PREPARED} if the user can be inserted.
     * @throws RemoteException
     */
    @Override
    public synchronized PrepareResponse prepare( String username ) throws RemoteException
    {
        if (userRepository.findUserByUsernameIgnoreCase( username ) != null)
            return PrepareResponse.ABORT;

        if ( lock.compareAndSet( false, true ) )
            return PrepareResponse.PREPARED;

        return PrepareResponse.ABORT;
    }

    /**
     * If and only if a {@link PrepareResponse#PREPARED} is returned by {@link #prepare(String)},
     * a {@link User} object can be committed. This will automatically remove the lock.
     * <p>
     * After this function call ends, the object is guaranteed to be persisted.
     *
     * @param user The {@link User} object to commit.
     * @throws RemoteException
     * @throws NotPreparedException When there is no lock, likely because {@link #prepare(String)} is
     *                              not called or returned {@link PrepareResponse#ABORT}.
     */
    @Override
    public synchronized void commit( User user ) throws RemoteException, NotPreparedException
    {
        //verify the lock
        if ( !lock.get() )
            throw new NotPreparedException( "There is no lock, prepare the cluster before calling this function." );

        userRepository.save( user );

    }

    /**
     * This manually removes the lock.
     * <p>
     * Note it's not needed to call this after {@link #commit(User)}.
     *
     * @throws RemoteException
     * @throws NotPreparedException When there is no lock, likely because {@link #prepare(String)} is
     *                              not called or returned {@link PrepareResponse#ABORT}.
     */
    @Override
    public synchronized void forget() throws RemoteException, NotPreparedException
    {
        if ( lock.compareAndSet( true, false ) )
            throw new NotPreparedException( "There is no lock." );
    }
}
