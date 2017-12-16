package be.kuleuven.cs.jli40d.server.db.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Random;

/**
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class AppConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public DataSource dataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName( "org.sqlite.JDBC" );
        dataSourceBuilder.url( "jdbc:sqlite:uno_" + new Random().nextInt(1000) + ".db" );
        return dataSourceBuilder.build();
    }
}
