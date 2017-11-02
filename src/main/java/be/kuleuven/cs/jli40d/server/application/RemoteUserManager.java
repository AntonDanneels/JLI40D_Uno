package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.DatabaseHandler;
import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidTokenException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import javafx.util.Pair;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author Pieter
 * @version 1.0
 */
public class RemoteUserManager extends UnicastRemoteObject implements UserHandler, UserTokenHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( RemoteUserManager.class );

    private DatabaseHandler databaseHandler;

    /**
     * Creates and exports a new UnicastRemoteObject object using an
     * anonymous port.
     * <p>
     * <p>The object is exported with a server socket
     * created using the {@link RMISocketFactory} class.
     *
     * @throws RemoteException if failed to export object
     * @since JDK1.1
     */
    public RemoteUserManager( DatabaseHandler databaseHandler ) throws RemoteException
    {
        this.databaseHandler = databaseHandler;
    }

    /**
     * Obtains the username using the token.
     *
     * @param token The provided token.
     * @return A username as a string.
     * @throws InvalidTokenException Thrown if the token doesn't match.
     */
    @Override
    public String findUserByToken( String token ) throws InvalidTokenException
    {
        String username = null;
        try
        {
            username = databaseHandler.getUsernameForToken( token );
        }
        catch ( RemoteException e )
        {
            LOGGER.error( "Error while fetching username from remote database. {}", e.getMessage() );
        }

        if ( username == null )
        {
            LOGGER.warn( "Token {} requested, but not found.", token );
            throw new InvalidTokenException( "Token not found." );
        }

        return username;
    }

    /**
     * Provide verification method to check a username and password against a persistence context.
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
        User user = databaseHandler.findUserByName( username );

        if ( user != null && BCrypt.checkpw( password, user.getPassword() ) )
        {
            String token = generateRandomToken();

            databaseHandler.registerToken( new Token(token, generateDateForOver( 7 ) , user) );

            LOGGER.info( "Logging user {} in and activating token {}", username, token );

            return token;
        }

        LOGGER.info( "Failed to authenticate user {}", username );

        throw new InvalidUsernameOrPasswordException();
    }

    /**
     * Register a user account.
     * <p>
     * Note: Checking the password by asking it twice should happen client side.
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
        //database handler will throw an error if the account already exists on the db cluster
        databaseHandler.registerUser(
                new User( username, 0, BCrypt.hashpw( password, BCrypt.gensalt() ) ) );

        LOGGER.info( "Created account for {} with username {}", email, username );

        try
        {
            return login( username, password );
        }
        catch ( InvalidUsernameOrPasswordException e )
        {
            LOGGER.error( "This should never be thrown, since the method just created the account." );
        }

        return null;
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

    }

    @Override
    public List<Pair<String, Long>> getUserScores() throws RemoteException
    {
        List<User> users = databaseHandler.getUsersSortedByScore();

        List<Pair<String, Long>> result = new ArrayList<>();

        for ( User u : users )
            result.add( new Pair<>( u.getUsername(), u.getScore() ) );

        return result;
    }

    /**
     * Generate a Base64 string.
     *
     * @return
     */
    private static String generateRandomToken()
    {
        SecureRandom random = new SecureRandom();
        byte[]       bytes  = new byte[ 24 ];

        random.nextBytes( bytes );

        return Base64.getEncoder().encodeToString( bytes );
    }

    private static Date generateDateForOver(int days)
    {
        //Set the deactivation date for the token to the next week
        Calendar deactivationDate = new GregorianCalendar();
        deactivationDate.setTime(new Date());
        deactivationDate.add(Calendar.DATE, days);

        return deactivationDate.getTime();
    }
}
