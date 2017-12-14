package be.kuleuven.cs.jli40d.core.deployer;

import java.io.Serializable;

/**
 * Simple util method to provide a host and port.
 *
 * @author Pieter
 * @version 1.0
 */
public class Server implements Serializable
{
    private String host;
    private int port;

    public Server( String host, int port )
    {
        this.host = host;
        this.port = port;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
}
