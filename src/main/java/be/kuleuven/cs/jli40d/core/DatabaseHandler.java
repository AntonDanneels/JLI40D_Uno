package be.kuleuven.cs.jli40d.core;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Token;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * This class provides an endpoint for application servers to connect to
 * database servers.
 *
 * @author Pieter
 * @version 1.0
 */
public interface DatabaseHandler extends Remote, Serializable
{
    List<Game> getGames() throws RemoteException;

    Game getGame( long id ) throws RemoteException;

    void addMove( long gameID, GameMove gameMove) throws RemoteException;

    void registerUser( User user ) throws RemoteException, AccountAlreadyExistsException;

    String getUsernameForToken(String token) throws RemoteException;

    void registerToken(Token token) throws RemoteException;

    User findUserByName(String username) throws RemoteException;

    List<User> getUsersSortedByScore() throws RemoteException;

}
