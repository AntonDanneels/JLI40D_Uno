package be.kuleuven.cs.jli40d.core.model.exception;

/**
 * @author Pieter
 * @version 1.0
 */
public class GameNotFoundException extends Exception
{
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public GameNotFoundException()
    {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public GameNotFoundException( String message )
    {
        super( message );
    }
}
