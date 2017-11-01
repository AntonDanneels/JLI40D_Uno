package be.kuleuven.cs.jli40d.server.service;

import be.kuleuven.cs.jli40d.core.DatabaseHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.User;
import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Pieter
 * @version 1.0
 */
public class LocalPersistenceService implements DatabaseHandler
{
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
}
