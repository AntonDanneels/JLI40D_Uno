package be.kuleuven.cs.jli40d.server.db.config;

import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.server.db.repository.CardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author Pieter
 * @version 1.0
 */
@Configuration
public class CardConfig
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CardConfig.class);

    private CardRepository cardRepository;

    @Autowired
    public CardConfig( CardRepository cardRepository )
    {
        this.cardRepository = cardRepository;

        generateCardsIfNeeded();
    }

    private void generateCardsIfNeeded()
    {
        if (cardRepository.findOne( 0 ) == null)
        {
            LOGGER.info( "Generating collection of cards for database." );

            cardRepository.save( GameLogic.generateCards() );
        }
    }
}
