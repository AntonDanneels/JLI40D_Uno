package be.kuleuven.cs.jli40d.server.application.service;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;
import be.kuleuven.cs.jli40d.server.application.service.task.AsyncTask;

import java.util.Queue;

/**
 * @author Pieter
 * @version 1.0
 */
public class TaskQueueService implements Runnable
{
    private boolean active = true;

    private Queue<AsyncTask> tasks;
    private DatabaseGameHandler databaseGameHandler;

    public TaskQueueService( Queue<AsyncTask> tasks, DatabaseGameHandler databaseGameHandler )
    {
        this.tasks = tasks;
        this.databaseGameHandler = databaseGameHandler;
    }

    @Override
    public synchronized void run()
    {
        while (active)
        {
               while(tasks.peek() != null)
               {
                   AsyncTask taks = tasks.poll();
                   taks.publish( databaseGameHandler );
               }
        }
    }
}
