package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.UserHandler;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple user manager that implements {@link UserHandler}. It's called simple because
 * tokens and passwords are not persisted but kept in maps.
 *
 * @author Pieter
 * @version 0.1
 */
public class SimpleUserManager extends UnicastRemoteObject implements UserHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( SimpleUserManager.class );

    private Map <String, String> tokens    = new HashMap <>();
    private Map <String, String> passwords = new HashMap <>();

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
    protected SimpleUserManager() throws RemoteException
    {
    }

    /**
     * Provide verification method to check a username and password against a persistence context.
     *
     * @param username The username as a string.
     * @param password The password as a string.
     * @return A token to use in further operations.
     * @throws InvalidUsernameOrPasswordException Thrown if either password or username don't match/exist.
     */
    public String login( String username, String password ) throws InvalidUsernameOrPasswordException
    {
        if ( passwords.containsKey( username ) && BCrypt.checkpw( password, passwords.get( username ) ) )
        {
            String token = generateRandomToken();

            tokens.put( token, username );

            return token;
        }

        return null;
    }

    /**
     * Register a user account. This creates a hash
     * <p>
     * Note: Checking the password by asking it twice should happen client side.
     *
     * @param email    The email of the user.
     * @param username The desired username.
     * @param password The password chosen by the user.
     * @return
     * @throws AccountAlreadyExistsException
     */
    public String register( String email, String username, String password ) throws AccountAlreadyExistsException
    {
        if ( passwords.containsKey( username ) )
        {
            throw new AccountAlreadyExistsException();
        }

        passwords.put( username, BCrypt.hashpw( password, BCrypt.gensalt() ) );

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
     */
    public void logout( String token )
    {

    }

    /**
     * Generate a Base64 string.
     *
     * @return
     */
    private String generateRandomToken()
    {
        SecureRandom random  = new SecureRandom();
        byte         bytes[] = new byte[ 24 ];

        random.nextBytes( bytes );

        return Base64.getEncoder().encodeToString( bytes );
    }
}
