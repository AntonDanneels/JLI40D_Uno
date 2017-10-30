package be.kuleuven.cs.jli40d.server.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * @author Pieter
 * @version 1.0
 */
@SpringBootApplication
@EntityScan( basePackages = { "be.kuleuven.cs.jli40d.core.model" } )
public class DBMain
{

    public static void main( String[] args )
    {
        SpringApplication.run( DBMain.class, args );
    }
}