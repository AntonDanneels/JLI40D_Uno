package be.kuleuven.cs.jli40d.core.model.exception;

public class WrongServerException extends Exception
{
    private String server;
    private int port;

    public WrongServerException( String server, int port )
    {
        super("Game is hosted on another server: " + server + ":" + port );
        this.server = server;
        this.port = port;
    }

    public String getServer()
    {
        return server;
    }

    public int getPort()
    {
        return port;
    }
}
