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
    private ServerType serverType;

    public Server( String host, int port, ServerType type )
    {
        this.host = host;
        this.port = port;
        serverType = type;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public ServerType getServerType()
    {
        return serverType;
    }

    @Override
    public String toString()
    {
        return host + ':' + port;
    }
}
