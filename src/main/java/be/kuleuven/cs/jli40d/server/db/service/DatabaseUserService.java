package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.NotPreparedException;
import be.kuleuven.cs.jli40d.server.db.repository.TokenRepository;
import be.kuleuven.cs.jli40d.server.db.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    private ClusterService clusterService;

    //user commit params
    private AtomicBoolean lock = new AtomicBoolean( false );

    @Autowired
    public DatabaseUserService( UserRepository userRepository,
                                TokenRepository tokenRepository,
                                ClusterService clusterService ) throws
            RemoteException
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.clusterService = clusterService;
    }

    protected DatabaseUserService() throws RemoteException
    {
    }

    /**
     * This function registers a user using a two-phase commit process.
     *
     * @param user The user object to persist to the cluster.
     * @throws RemoteException
     * @throws AccountAlreadyExistsException
     */
    @Override
    public synchronized void registerUser( User user ) throws RemoteException, AccountAlreadyExistsException
    {
        while ( lock.get() )
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }

        //step one: obtain locks (first own lock, afterwards remote locks)
        Set <UserCommitHandler> successfulHandlers = new HashSet <>();

        LOGGER.info( "Obtaining lock on databases." );
        boolean successful = obtainResponse( user.getUsername(), this, successfulHandlers );

        Iterator <UserCommitHandler> iterator = clusterService.getUserCommitHandlers().iterator();

        while ( iterator.hasNext() && successful )
        {
            successful = obtainResponse( user.getUsername(), iterator.next(), successfulHandlers );
        }

        //step two: commit
        if ( successful )
        {
            LOGGER.info( "Obtained lock on all databases. Now committing." );

            for ( UserCommitHandler handler : successfulHandlers )
            {
                try
                {
                    handler.commit( user );
                }
                catch ( NotPreparedException e )
                {
                    LOGGER.error( "{} was not prepared. {}", handler, e.getMessage() );
                }
            }
        }

        //step three: cry when it goes wrong
        if ( !successful )
            throw new AccountAlreadyExistsException();
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

        for ( UserCommitHandler handler : clusterService.getUserCommitHandlers() )
        {
           handler.commit( token );
        }

    }

    /**
     * This method is blocking, meaning if a update to the database is happening and
     * a user is not found, this function will wait.
     * @param username
     * @return
     * @throws RemoteException
     */
    @Override
    public synchronized User findUserByName( String username ) throws RemoteException
    {
        User user = userRepository.findUserByUsernameIgnoreCase( username );

        if (user == null )
        {
            while ( lock.get() )
            {
                try
                {
                    wait();
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }

            user = userRepository.findUserByUsernameIgnoreCase( username );
        }

        return user ;
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
        if ( userRepository.findUserByUsernameIgnoreCase( username ) != null )
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

        lock.set( false );

        notifyAll();

    }

    /**
     * Single phase commit for tokens.
     *
     * @param token The {@link Token} object to persist.
     * @throws RemoteException
     */
    @Override
    public void commit( Token token ) throws RemoteException
    {
        tokenRepository.save( token );
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

    /**
     * Obtain the response of a {@link #prepare(String)} call. If successful, it will be added to the
     * list of successful calls. Otherwise,
     *
     * @param userCommitHandler
     * @param successfulHandlers
     */
    public boolean obtainResponse( String username, UserCommitHandler userCommitHandler, Set <UserCommitHandler> successfulHandlers ) throws RemoteException
    {
        PrepareResponse response = userCommitHandler.prepare( username );


        if ( response == PrepareResponse.PREPARED )
            successfulHandlers.add( userCommitHandler );
        else
        {
            LOGGER.warn( "Failed to obtain lock on all databases." );

            for ( UserCommitHandler handler : successfulHandlers )
            {
                try
                {
                    handler.forget();
                }
                catch ( NotPreparedException e )
                {
                    LOGGER.error( "Unprepared handler creeped in successful handler list. Shit's fucked up man." );
                }
            }
        }

        return response == PrepareResponse.PREPARED;
    }
}
