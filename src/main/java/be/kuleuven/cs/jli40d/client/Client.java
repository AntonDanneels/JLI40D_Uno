package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.core.model.GameMove;
import be.kuleuven.cs.jli40d.core.model.Player;

/**
 * Created by Anton D. on 19/10/2017 using IntelliJ IDEA 14.0
 */
public class Client
{
    private Game game;
    private int myID = 0;

    private Client()
    {
        game = new Game();
    }

    public void run()
    {
        GameHandler gameHandler = null;

        while ( !game.isEnded() )
        {
            GameMove move;
            if( gameHandler.myTurn() )
            {
                // Construct my GameMove & send it
            }
            else
            {
                move = gameHandler.getNextMove( game.getCurrentGameMoveID() );
            }

            // apply the game move to the game


        }
    }

    public static void main( String args[] )
    {
        Client client = new Client();
        client.run();
    }

}
