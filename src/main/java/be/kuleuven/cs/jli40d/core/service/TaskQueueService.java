package be.kuleuven.cs.jli40d.core.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.core.service.task.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;

/**
 * @author Pieter
 * @version 1.0
 */
public class TaskQueueService implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger( TaskQueueService.class );

    private boolean active = true;

    private Deque<AsyncTask>    tasks;
    private DatabaseGameHandler databaseGameHandler;

    public TaskQueueService( Deque<AsyncTask> tasks, DatabaseGameHandler databaseGameHandler )
    {
        this.tasks = tasks;
        this.databaseGameHandler = databaseGameHandler;
    }

    @Override
    public synchronized void run()
    {
        while ( active )
        {
            while ( tasks.peek() != null )
            {
                AsyncTask task = tasks.poll();
                LOGGER.debug( "Publishing task {} for game {} from server {}",
                        task.getClass().getSimpleName(),
                        task.getGameUuid(),
                        task.getServerID() );
                task.publish( databaseGameHandler );
            }
        }
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public Deque <AsyncTask> getTasks()
    {
        return tasks;
    }
}
