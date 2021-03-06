package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import be.kuleuven.cs.jli40d.core.model.exception.WrongServerException;
import javafx.util.Pair;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * The UserHandler provides three functions: creating an account, logging in and logging out.
 */
public interface UserHandler extends Remote, Serializable
{

    /**
     * Provide verification method to check a username and password against a persistence context.
     *
     * @param username The username as a string.
     * @param password The password as a string.
     * @return A token to use in further operations.
     * @throws InvalidUsernameOrPasswordException Thrown if either password or username don't match/exist.
     * @throws RemoteException
     */
    String login( String username, String password ) throws RemoteException, InvalidUsernameOrPasswordException, WrongServerException;

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
    String register( String email, String username, String password ) throws RemoteException, AccountAlreadyExistsException, WrongServerException;

    /**
     * Invalidates the token.
     *
     * @param token The token to invalidate.
     * @throws RemoteException
     */
    void logout( String token ) throws RemoteException, WrongServerException;


    /**
     *  Returns a pair of usernames & scores.
     *  @throws RemoteException
     * */
    List<Pair<String, Long>> getUserScores() throws RemoteException, WrongServerException;

    void updateScore( String username, int score ) throws RemoteException, WrongServerException;
}
