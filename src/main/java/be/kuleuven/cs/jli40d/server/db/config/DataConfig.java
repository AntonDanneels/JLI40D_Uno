package be.kuleuven.cs.jli40d.server.db.config;

import be.kuleuven.cs.jli40d.server.db.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class DataConfig
{
    private final GameRepository gameRepository;

    @Autowired
    public DataConfig( GameRepository gameRepository )
    {
        this.gameRepository = gameRepository;
    }

}
