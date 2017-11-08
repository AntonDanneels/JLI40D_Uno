package be.kuleuven.cs.jli40d.core.model.exception;

/**
 * Created by Anton D.
 */
public class GameEndedException extends Exception
{
    public GameEndedException()
    {
        super( "Game has ended" );
    }

    public GameEndedException( String msg )
    {
        super( msg );
    }
}
