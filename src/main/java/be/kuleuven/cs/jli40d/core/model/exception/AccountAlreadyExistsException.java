package be.kuleuven.cs.jli40d.core.model.exception;

/**
 * @author Pieter
 * @version 1.0
 */
public class AccountAlreadyExistsException extends Exception
{
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public AccountAlreadyExistsException()
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
    public AccountAlreadyExistsException( String message )
    {
        super( message );
    }
}
