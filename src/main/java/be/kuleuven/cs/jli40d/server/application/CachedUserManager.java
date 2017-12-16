package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.database.DatabaseUserHandler;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * The {@link CachedUserManager} is an abomination that should never have been. It combines
 * both a {@link RemoteUserManager} and a {@link SimpleUserManager} to provide a cache and
 * a more extensive user manager manager by a database cluster.
 *
 * @author Pieter
 * @version 1.0
 */
public class CachedUserManager extends UnicastRemoteObject implements UserHandler, UserTokenHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( CachedUserManager.class );

    private RemoteUserManager remoteUserManager;
    private SimpleUserManager simpleUserManager;

    public CachedUserManager( DatabaseUserHandler databaseUserHandler ) throws RemoteException
    {
        this.remoteUserManager = new RemoteUserManager( databaseUserHandler );
        this.simpleUserManager = new SimpleUserManager();
    }


    /**
     * Obtains the username using the token.
     * <p>
     * First a local cache is checked, if this doesn't give any hits the remote cluster is consulted.
     *
     * @param token The provided token.
     * @return A username as a string.
     * @throws InvalidTokenException Thrown if the token doesn't match.
     */
    @Override
    public String findUserByToken( String token ) throws InvalidTokenException
    {
        try
        {
            return simpleUserManager.findUserByToken( token );
        }
        catch ( InvalidTokenException e )
        {
            LOGGER.debug( "Not found locally, trying remote." );
            String username = remoteUserManager.findUserByToken( token );
            simpleUserManager.forceLogin( username, token );

            return username;
        }
    }

    /**
     * Provide verification method to check a username and password against a persistence context.
     * <p>
     * First a local cache is checked, if this doesn't give any hits the remote cluster is consulted.
     *
     * @param username The username as a string.
     * @param password The password as a string.
     * @return A token to use in further operations.
     * @throws InvalidUsernameOrPasswordException Thrown if either password or username don't match/exist.
     * @throws RemoteException
     */
    @Override
    public String login( String username, String password ) throws RemoteException, InvalidUsernameOrPasswordException
    {
        String token = remoteUserManager.login( username, password );

        simpleUserManager.forceLogin( username, password );

        return token;
    }

    /**
     * Register a user account.
     * <p>
     * Note: Checking the password by asking it twice should happen client side.
     * <p>
     * First a local cache is checked, if this doesn't give any hits the remote cluster is consulted.
     *
     * @param email    The email of the user.
     * @param username The desired username.
     * @param password The password chosen by the user.
     * @return
     * @throws AccountAlreadyExistsException
     * @throws RemoteException
     */
    @Override
    public String register( String email, String username, String password ) throws
            RemoteException,
            AccountAlreadyExistsException
    {
        String token = remoteUserManager.register( email, username, password );

        simpleUserManager.forceRegistration( email, username, password, token );

        return token;

    }

    /**
     * Invalidates the token.
     *
     * @param token The token to invalidate.
     * @throws RemoteException
     */
    @Override
    public void logout( String token ) throws RemoteException
    {
        remoteUserManager.logout( token );
        simpleUserManager.logout( token );
    }

    /**
     * Returns a pair of usernames & scores.
     * <p>
     * This directly consults a remote db cluster.
     *
     * @throws RemoteException
     */
    @Override
    public List<Pair<String, Long>> getUserScores() throws RemoteException
    {
        return remoteUserManager.getUserScores();
    }

    @Override
    public void updateScore( String username, int score )
    {
        remoteUserManager.updateScore( username, score );
    }


}
