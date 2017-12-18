package be.kuleuven.cs.jli40d.server.dispatcher.controller;

import be.kuleuven.cs.jli40d.core.deployer.Server;

/**
 * @author Pieter
 * @version 1.0
 */
public class AppServerWrapper
{
    private Server server;

    private int numberOfGames;
    private int numberOfClients;
    private Server database;

    public AppServerWrapper( Server server )
    {
        this.server = server;
    }

    public Server getServer()
    {
        return server;
    }

    public void setServer( Server server )
    {
        this.server = server;
    }

    public int getNumberOfGames()
    {
        return numberOfGames;
    }

    public void setNumberOfGames( int numberOfGames )
    {
        this.numberOfGames = numberOfGames;
    }

    public int getNumberOfClients()
    {
        return numberOfClients;
    }

    public void setNumberOfClients( int numberOfClients )
    {
        this.numberOfClients = numberOfClients;
    }

    public Server getDatabase()
    {
        return database;
    }

    public void setDatabase( Server database )
    {
        this.database = database;
    }
}
