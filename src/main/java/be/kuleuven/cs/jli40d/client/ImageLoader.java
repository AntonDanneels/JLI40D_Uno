package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.CardColour;
import be.kuleuven.cs.jli40d.core.model.CardType;
import be.kuleuven.cs.jli40d.core.model.Game;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Pieter
 * @version 1.0
 */
public class ImageLoader
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ImageLoader.class );

    private static HashMap<SceneImage, Image> sceneImages = new HashMap<>();
    private static Map<Card, Image>           cardImages  = new HashMap<>();

    public static void loadImages()
    {
        LOGGER.info( "Loading image pack." );

        Game game = new Game( 4 );
        GameLogic.generateDeck( game );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.GREEN ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.RED ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.BLUE ) );
        game.getDeck().add( new Card( CardType.PLUS4, CardColour.YELLOW ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.GREEN ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.RED ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.BLUE ) );
        game.getDeck().add( new Card( CardType.OTHER_COLOUR, CardColour.YELLOW ) );

        for ( Card c : game.getDeck() )
        {
            String path = "/cards_original/" + c.getType() + "_" + c.getColour() + ".png";
            LOGGER.debug( "Loading image: {}", path );
            cardImages.put( c, new Image( path ) );
        }


        LOGGER.debug( "Loaded {} images", cardImages.size() );

        String path = "/uno-dark-background.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.GAME_BACKGROUND, new Image( path ) );

        path = "/uno-spectate.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.SPECTATOR_BACKGROUND, new Image( path ) );

        path = "/cards_original/CARD_BACK.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.CARD_BACK, new Image( path ) );

        path = "/current-player.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.CURRENT_USER, new Image( path ) );

        path = "/player.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.OTHER_USER, new Image( path ) );

        path = "/user.png";
        LOGGER.debug( "Loading image: {}", path );
        sceneImages.put( SceneImage.DEFAULT_AVATAR, new Image( path ) );
    }

    public static Image getCardImage( Card card )
    {
        return cardImages.get( card );
    }

    public static Image getSceneImage( SceneImage sceneImage )
    {
        return sceneImages.get( sceneImage );
    }

}
