package be.kuleuven.cs.jli40d.server.db;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

import javax.xml.bind.JAXBException;

/**
 * @author Pieter
 * @version 1.0
 */
@SpringBootApplication
@EntityScan( basePackages = { "be.kuleuven.cs.jli40d.core.model" } )
public class DBMain
{

    public static void main(String[] args) throws JAXBException
    {
        SpringApplication app = new SpringApplication(DBMain.class);
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
    }
}