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

    private String uuid;

    public Server( String host, int port, ServerType type, String uuid )
    {
        this.host = host;
        this.port = port;
        serverType = type;
        this.uuid = uuid;
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

    public String getUuid()
    {
        return uuid;
    }

    public int getID()
    {
        return uuid.hashCode();
    }

    @Override
    public String toString()
    {
        return host + ':' + port;
    }
}
