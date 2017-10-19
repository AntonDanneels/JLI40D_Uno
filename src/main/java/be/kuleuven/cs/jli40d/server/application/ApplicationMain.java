package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.LobbyHandler;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * The class with the main method needed to launch the application server.
 *
 * @author Pieter
 * @version 1.0
 */
public class ApplicationMain
{
    public static void main( String[] args )
    {
        try
        {
            // create on port 1099
            Registry registry = LocateRegistry.createRegistry( 1099 );
            // create a new service named CounterService
            registry.rebind( LobbyHandler.class.getName(), new Lobby() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        System.out.println( "system is ready" );
    }

}
