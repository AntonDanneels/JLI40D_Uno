package be.kuleuven.cs.jli40d.server.db.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class AppConfig
{
    @Bean
    public DataSource dataSource()
    {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName( "org.sqlite.JDBC" );
        dataSourceBuilder.url( "jdbc:sqlite:your.db" );
        return dataSourceBuilder.build();
    }
}
