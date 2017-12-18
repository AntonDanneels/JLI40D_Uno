package be.kuleuven.cs.jli40d.server.dispatcher.controller;

import be.kuleuven.cs.jli40d.core.deployer.Server;
import be.kuleuven.cs.jli40d.server.dispatcher.ServerRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pieter
 * @version 1.0
 */
@Controller
public class DashboardController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private ServerRegister serverRegister;

    /**
     * Returns homepage (index.html).
     *
     * @param modelMap The ModelMap object.
     * @return The string with the template to use.
     */
    @RequestMapping("/")
    public String dashboard(ModelMap modelMap)
    {
        Map<Server, Integer> gamesForEachServer = new HashMap <>(  );

        for (Server s : serverRegister.getApplicationServers())
        {
            gamesForEachServer.put( s, serverRegister.getServerGameMapping().get( s.getUuid() ).size() );
        }

        modelMap.put("appservers", gamesForEachServer);
        modelMap.put("databases", serverRegister.getServerMapping());

        return "home";
    }

    @RequestMapping("/shutdown/{serverUuid}")
    public String listItems(ModelMap modelMap, @PathVariable String serverUuid)
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
}
