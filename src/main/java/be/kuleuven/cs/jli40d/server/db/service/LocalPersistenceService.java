package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.DatabaseHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
@Service
public class LocalPersistenceService extends UnicastRemoteObject implements DatabaseHandler
{

    protected LocalPersistenceService() throws RemoteException
    {
    }

    @Override
    public List<Game> getGames() throws RemoteException
    {
        return null;
    }

    @Override
    public Game getGame( long id ) throws RemoteException
    {
        return null;
    }

    @Override
    public void addMove( long gameID, GameMove gameMove ) throws RemoteException
    {

    }

    @Override
    public void registerUser( User user ) throws RemoteException, AccountAlreadyExistsException
    {

    }

    @Override
    public String getValidTokenForUser( User user ) throws RemoteException
    {
        return null;
    }

    @Override
    public User findUserByName( String username ) throws RemoteException
    {
        return null;
    }

    @Override
    public List<User> getUsersSortedByScore() throws RemoteException
    {
        return null;
    }
}
