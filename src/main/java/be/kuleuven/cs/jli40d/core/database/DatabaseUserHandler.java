package be.kuleuven.cs.jli40d.core.database;

import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class provides an endpoint for application servers to connect to
 * database servers and manage user functions.
 *
 * @author Pieter
 * @version 1.0
 */
public interface DatabaseUserHandler extends Remote, Serializable
{
    void registerUser( User user ) throws RemoteException, AccountAlreadyExistsException;

    String getUsernameForToken(String token) throws RemoteException;

    void registerToken(Token token) throws RemoteException;

    User findUserByName(String username) throws RemoteException;

    List<User> getUsersSortedByScore() throws RemoteException;

    void updateScore( String username, int score ) throws RemoteException;
}
