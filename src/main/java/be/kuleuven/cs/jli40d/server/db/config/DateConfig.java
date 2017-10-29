package be.kuleuven.cs.jli40d.server.db.config;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class DateConfig
{
    private final GameRepository gameRepository;

    @Autowired
    public DateConfig( GameRepository gameRepository )
    {
        this.gameRepository = gameRepository;
        start();
    }

    private void start()
    {
        gameRepository.save( new Game( 1, 2 ) );
        gameRepository.save( new Game( 2, 3 ) );
        gameRepository.save( new Game( 3, 2 ) );
        gameRepository.save( new Game( 4, 5 ) );
        gameRepository.save( new Game( 5, 2 ) );

        gameRepository.findAll().forEach( game -> System.out.println( game.getMaximumNumberOfPlayers() ) );
    }
}
