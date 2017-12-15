package be.kuleuven.cs.jli40d.server.application.service.task;

import be.kuleuven.cs.jli40d.core.database.DatabaseGameHandler;

/**
 * This task represents a contract to publish a update regarding a game to a
 * remote {@link DatabaseGameHandler}.
 *
 * @author Pieter
 * @version 1.0
 */
public abstract class AsyncTask
{
    private int serverID;
    private String gameUuid;

    public AsyncTask( int serverID, String gameUuid )
    {
        this.serverID = serverID;
        this.gameUuid = gameUuid;
    }

    /**
     * The only method that defines the contract between the caller and callee, where the
     * function invoking this method can expect a <b>blocking</b> task to be executed.
     *
     * @param databaseGameHandler
     */
    public abstract void publish( DatabaseGameHandler databaseGameHandler );

    public int getServerID()
    {
        return serverID;
    }

    public void setServerID( int serverID )
    {
        this.serverID = serverID;
    }

    public String getGameUuid()
    {
        return gameUuid;
    }

    public void setGameUuid( String gameUuid )
    {
        this.gameUuid = gameUuid;
    }
}
