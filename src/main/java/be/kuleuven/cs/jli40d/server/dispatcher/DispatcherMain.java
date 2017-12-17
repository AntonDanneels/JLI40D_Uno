package be.kuleuven.cs.jli40d.server.dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @author Pieter
 * @version 1.0
 */
@SpringBootApplication
@EntityScan( basePackages = { "be.kuleuven.cs.jli40d.core.model" } )
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class DispatcherMain
{

    public static void main( String[] args )
    {
        SpringApplication.run( DispatcherMain.class, args );
    }
}