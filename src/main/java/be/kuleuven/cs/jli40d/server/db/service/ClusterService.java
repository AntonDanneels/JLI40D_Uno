package be.kuleuven.cs.jli40d.server.db.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;
import be.kuleuven.cs.jli40d.core.service.TaskQueueService;
import be.kuleuven.cs.jli40d.core.service.task.*;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author Pieter
 * @version 1.0
 */
@Component
public class ClusterService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ClusterService.class );

    //cluster async publishers
    private Map <Server, TaskQueueService> clusterServices;

    private Set <Deque <AsyncTask>> clusterQueues;

    private int serverID = UUID.randomUUID().hashCode();

    @Autowired
    public ClusterService()
    {
        clusterQueues = new HashSet <>();
        clusterServices = new HashMap <>();
    }

    /**
     * This method initializes a server connections through RMI and registers a {@link AsyncTask} queue
     * to publish those tasks to other databases.
     *
     * To prevent threads from running without use, close the connection with {@link #closeConnection(Server)}.
     *
     * @param server The {@link Server} object of the remote database server.
     */
    public void addServer( Server server )
    {
        if ( clusterServices.containsKey( server ) )
            LOGGER.warn( "This server ({}) is already registered. To prevent performance leaks, unregister it first." );

        LOGGER.info( "opening connection to remote game handler for server {}.", server );
        try
        {
            //remote db
            Registry myRegistry = LocateRegistry.getRegistry( server.getHost(), server.getPort() );

            DatabaseGameHandler remoteGameHandler = ( DatabaseGameHandler ) myRegistry.lookup( DatabaseGameHandler.class.getName() );

            //start new thread and create a queue
            Deque <AsyncTask> tasks            = new ConcurrentLinkedDeque <>();
            TaskQueueService  taskQueueService = new TaskQueueService( tasks, remoteGameHandler );
            new Thread( taskQueueService ).start();
            LOGGER.info( "Started a remote publishing service [ {} ].", taskQueueService.getClass().getSimpleName() );

            //finaly add the queue and service to the managing set and map
            clusterServices.put( server, taskQueueService );
            clusterQueues.add( tasks );

        }
        catch ( RemoteException | NotBoundException e )
        {
            LOGGER.error( "Failed to connect to dispatcher {}. Check these settings.", DispatcherMain.DISPATCHER );
        }
    }

    /**
     * This method closes the rmi connection
     *
     * @param server The {@link Server} object of the remote database server.
     */
    public void closeConnection(Server server)
    {
        clusterServices.get( server ).setActive( false );

        clusterQueues.remove( clusterServices.get( server ).getTasks() );

        clusterServices.remove( server );

        //TODO actually close the connection, hope GC does it for now.
    }

    public synchronized void addGame( Game game )
    {
        LOGGER.debug( "Added game to persist to other cluster members." );

        for ( Deque <AsyncTask> tasks : clusterQueues )
            tasks.add( new AsyncGameTask( serverID, game ) );

        notifyAll();
    }

    public synchronized void addMove( String gameUuid, GameMove move )
    {
        LOGGER.debug( "Added move to persist to other cluster members." );

        for ( Deque <AsyncTask> tasks : clusterQueues )
            tasks.add( new AsyncGameMoveTask( serverID, gameUuid, move ) );

        notifyAll();
    }

    public synchronized void addMoves( String gameUuid, List <GameMove> moves )
    {
        LOGGER.debug( "Added moves to persist to other cluster members." );

        for ( Deque <AsyncTask> tasks : clusterQueues )
            tasks.add( new AsyncGameMovesTask( serverID, gameUuid, moves ) );

        notifyAll();
    }

    public synchronized void addPlayer( String gameUuid, Player player )
    {
        LOGGER.debug( "Added player to persist to other cluster members." );

        for ( Deque <AsyncTask> tasks : clusterQueues )
            tasks.add( new AsyncPlayerTask( serverID, gameUuid, player ) );

        notifyAll();
    }
}
