package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;

/**
 * The UserHandler provides three functions: creating an account, logging in and logging out.
 */
public interface UserHandler
{

    /**
     * Provide verification method to check a username and password against a persistence context.
     *
     * @param username The username as a string.
     * @param password The password as a string.
     * @return A token to use in further operations.
     * @throws InvalidUsernameOrPasswordException Thrown if either password or username don't match/exist.
     */
    String login( String username, String password ) throws InvalidUsernameOrPasswordException;

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
     */
    String register( String email, String username, String password ) throws AccountAlreadyExistsException;

    /**
     * Invalidates the token.
     *
     * @param token The token to invalidate.
     */
    void logout( String token );
}
