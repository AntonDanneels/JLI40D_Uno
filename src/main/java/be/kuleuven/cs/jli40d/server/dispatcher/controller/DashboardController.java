package be.kuleuven.cs.jli40d.server.dispatcher.controller;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.server.dispatcher.DispatcherMain;
import be.kuleuven.cs.jli40d.server.dispatcher.ServerRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pieter
 * @version 1.0
 */
@Controller
public class DashboardController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( DashboardController.class );

    @Autowired
    private ServerRegister serverRegister;

    /**
     * Returns homepage (index.html).
     *
     * @param modelMap The ModelMap object.
     * @return The string with the template to use.
     */
    @RequestMapping( "/" )
    public String dashboard( ModelMap modelMap )
    {
        List <AppServerWrapper> gamesForEachServer = new ArrayList <>();

        Map <Server, Server> dababasesForAppServers = new HashMap <>();

        for ( Map.Entry <Server, List <Server>> dbAppList : serverRegister.getServerMapping().entrySet() )
        {
            for ( Server app : dbAppList.getValue() )
            {
                dababasesForAppServers.put( app, dbAppList.getKey() );
            }
        }

        for ( Server s : serverRegister.getApplicationServers() )
        {
            AppServerWrapper wrapper = new AppServerWrapper( s );
            wrapper.setNumberOfClients( serverRegister.getClientMapping().get( s ).size() );
            wrapper.setNumberOfGames( serverRegister.getServerGameMapping().get( s.getUuid() ).size() );
            wrapper.setDatabase( dababasesForAppServers.get( s ) );
            gamesForEachServer.add( wrapper );
        }

        modelMap.put( "appservers", gamesForEachServer );
        modelMap.put( "databases", serverRegister.getServerMapping() );

        return "home";
    }

    @RequestMapping( "/shutdown/{serverUuid}" )
    public String listItems( ModelMap modelMap, @PathVariable String serverUuid )
    {
        try
        {
            serverRegister.shutdownServer( serverUuid );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Shit's fucked up. Anton fucked up." );
        }

        return "redirect:/";
    }

    @RequestMapping( "/start/app" )
    public String startAppServer( ModelMap modelMap )
    {
        try
        {
            Runtime.getRuntime().exec( new String[]{ "java", "-jar", "app.jar", DispatcherMain.DISPATCHER.getHost(), DispatcherMain.DISPATCHER.getHost()  } );
        }
        catch ( IOException e )
        {
            modelMap.put( "error", "Failed to launch app server." );
        }

        return "redirect:/";
    }


}
