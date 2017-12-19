package be.kuleuven.cs.jli40d.server.dispatcher;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.core.deployer.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import java.util.UUID;

/**
 * @author Pieter
 * @version 1.0
 */
@SpringBootApplication
@EntityScan( basePackages = { "be.kuleuven.cs.jli40d.core.model" } )
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class DispatcherMain
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherMain.class);

    public static Server DISPATCHER = new Server( "localhost", 1100, ServerType.DISPATCHER, UUID.randomUUID().toString() );

    public static void main( String[] args )
    {
        if (args.length != 0)
        {
            DISPATCHER = new Server( args[0], 1100, ServerType.DISPATCHER, UUID.randomUUID().toString() );
        } else {
            LOGGER.info( "Launching with default config ({}), use java -jar dispatcher.jar <ip> to change this", DISPATCHER );
        }

        SpringApplication.run( DispatcherMain.class, args );
    }
}