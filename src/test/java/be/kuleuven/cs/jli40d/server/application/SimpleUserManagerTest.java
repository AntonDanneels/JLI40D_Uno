package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.model.exception.AccountAlreadyExistsException;
import be.kuleuven.cs.jli40d.core.model.exception.InvalidUsernameOrPasswordException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

/**
 * @author Pieter
 * @version 1.0
 */
public class SimpleUserManagerTest
{
    private SimpleUserManager simpleUserManager;

    @Before
    public void before() throws RemoteException
    {
        this.simpleUserManager = new SimpleUserManager();
    }

    @Test
    public void correctLogin() throws Exception
    {
        simpleUserManager.register( "test@test", "test-user", "test" );

        Assert.assertTrue( "Token should be more than a few characters.",
                10 < simpleUserManager.login( "test-user", "test" ).length() );

    }

    @Test( expected = InvalidUsernameOrPasswordException.class )
    public void fakeLogin() throws Exception
    {
        simpleUserManager.login( "test", "fake" );
    }

    @Test( expected = AccountAlreadyExistsException.class )
    public void registerExistingAccount() throws Exception
    {
        simpleUserManager.register( "test@test", "test-user", "test" );

        simpleUserManager.register( "test@test", "test-user", "test" );
    }

}