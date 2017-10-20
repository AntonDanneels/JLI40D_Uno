package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.GameHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
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
                move = new GameMove( game.getCurrentGameMoveID(), game.getPlayers().get( myID ), null, true  );
            }
            else
            {
                move = gameHandler.getNextMove( game.getCurrentGameMoveID() );
            }

            // apply the game move to the game
            GameLogic gameLogic = new GameLogic();

            gameLogic.applyMove( game, move );

            game.setCurrentGameMoveID( game.getCurrentGameMoveID() + 1 );
        }
    }

    public static void main( String args[] )
    {
        Client client = new Client();
        client.run();
    }

}
