package be.kuleuven.cs.jli40d.core.model.exception;

public class WrongServerException extends Exception
{
    public WrongServerException()
    {
        super("Game is hosted on another server");
    }
}
