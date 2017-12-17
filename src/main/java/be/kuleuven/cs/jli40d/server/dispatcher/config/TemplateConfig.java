package be.kuleuven.cs.jli40d.server.dispatcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

/**
 * Configuration for templates. Uses Thymeleaf template engine.
 *
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class TemplateConfig
{
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(templateResolver());

        return springTemplateEngine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        final ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setOrder(1);
        viewResolver.setCache(false);
        return viewResolver;
    }
}