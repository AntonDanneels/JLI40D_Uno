package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.model.User;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public interface UserCommitHandler extends Remote, Serializable
{

    /**
     * Prepares the remote database for a commit of a {@link User}.
     * <p>
     * This takes a lock on the database, preventing other operations to read by blocking
     * them or write (by returning {@link PrepareResponse#ABORT}).
     *
     * If {@link PrepareResponse#ABORT} is returned, this means no lock could be obtained.
     * Therefore, it is also unnecessary to call {@link #forget()} later on.
     *
     * @param username The username of the user to insert, as a String.
     * @return {@link PrepareResponse#PREPARED} if the user can be inserted.
     * @throws RemoteException
     */
    PrepareResponse prepare( String username ) throws RemoteException;

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
    void commit( User user ) throws RemoteException, NotPreparedException;

    /**
     * This manually removes the lock.
     * <p>
     * Note it's not needed to call this after {@link #commit(User)}.
     *
     * @throws RemoteException
     * @throws NotPreparedException When there is no lock, likely because {@link #prepare(String)} is
     *                              not called or returned {@link PrepareResponse#ABORT}.
     */
    void forget() throws RemoteException, NotPreparedException;
}
