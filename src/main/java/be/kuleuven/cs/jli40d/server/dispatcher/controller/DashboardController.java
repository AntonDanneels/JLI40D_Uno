package be.kuleuven.cs.jli40d.server.dispatcher.controller;

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
    /**
     * Returns homepage (index.html).
     *
     * @param modelMap The ModelMap object.
     * @return The string with the template to use.
     */
    @RequestMapping("/")
    public String listItems(ModelMap modelMap)
    {
        //modelMap.put("products", productService.getAllItems());

        return "home";
    }
}
