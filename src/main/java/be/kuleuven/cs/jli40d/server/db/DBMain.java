package be.kuleuven.cs.jli40d.server.db;

import be.kuleuven.cs.jli40d.core.model.Game;
import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author Pieter
 * @version 1.0
 */
@SpringBootApplication
@EntityScan( basePackages = {"be.kuleuven.cs.jli40d.core.model"} )
public class DBMain
{

    public static void main( String[] args )
    {
        SpringApplication.run( DBMain.class, args );
    }

    @Autowired
    private GameRepository gameRepository;

    private void start( String[] args )
    {
        gameRepository.save( new Game( 1, 2 ) );
    }
}