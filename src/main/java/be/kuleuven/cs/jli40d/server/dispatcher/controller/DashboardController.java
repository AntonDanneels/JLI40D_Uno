package be.kuleuven.cs.jli40d.server.dispatcher.controller;

import be.kuleuven.cs.jli40d.server.dispatcher.ServerRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Pieter
 * @version 1.0
 */
@Controller
public class DashboardController
{
    @Autowired
    private ServerRegister serverRegister;

    /**
     * Returns homepage (index.html).
     *
     * @param modelMap The ModelMap object.
     * @return The string with the template to use.
     */
    @RequestMapping("/")
    public String listItems(ModelMap modelMap)
    {
        modelMap.put("databases", serverRegister.getDatabaseServers());

        return "home";
    }
}
