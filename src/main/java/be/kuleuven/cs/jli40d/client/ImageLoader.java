package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.ResourceHandler;
import be.kuleuven.cs.jli40d.core.logic.GameLogic;
import be.kuleuven.cs.jli40d.core.model.Card;
import be.kuleuven.cs.jli40d.core.model.CardColour;
import be.kuleuven.cs.jli40d.core.model.CardType;
import be.kuleuven.cs.jli40d.core.model.Game;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

    public static void loadImages( ResourceHandler resourceHandler ) throws MalformedURLException
    {


        Task t = new Task()
        {
            protected Object call() throws Exception
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

                String texturepack = System.getProperty( "user.home" ) + "/uno/client_texturepacks/" + "default_texturepacks";

                try
                {
                    texturepack = System.getProperty( "user.home" ) + "/uno/client_texturepacks/" + resourceHandler.getCurrentResourcePackName();

                    File file = new File( texturepack );
                    if( !file.isDirectory() )
                    {
                        if( file.mkdir() )
                            LOGGER.info( "Created folder: {}", file.getAbsolutePath() );
                        else
                            LOGGER.info( "Failed to create folder." );

                        LOGGER.info( "Texture pack '{}' doesn't exist. Creating folder & downloading files.", texturepack );

                        int index = 0;
                        for ( Card c : game.getDeck() )
                        {
                            String path = c.getType() + "_" + c.getColour() + ".png";
                            LOGGER.debug( "Loading image from server: {}", path );

                            byte[] image = resourceHandler.getImage( resourceHandler.getCurrentResourcePackName(), path );

                            BufferedImage imag = ImageIO.read( new ByteArrayInputStream( image ) );
                            ImageIO.write( imag, "png", new File( texturepack, path ) );

                            index++;

                            updateProgress( (float)index, (float)game.getDeck().size() );
                        }

                        String path = "CARD_BACK.png";
                        LOGGER.debug( "Loading image from server: {}", path );

                        byte[] image = resourceHandler.getImage( resourceHandler.getCurrentResourcePackName(), path );

                        BufferedImage imag = ImageIO.read( new ByteArrayInputStream( image ) );
                        ImageIO.write( imag, "png", new File( texturepack, path ) );
                    }
                    else
                    {
                        LOGGER.info( "Texture pack exists, using cache." );
                    }
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }

                int index = 0;
                for ( Card c : game.getDeck() )
                {
                    String path = new File( texturepack + "/" + c.getType() + "_" + c.getColour() + ".png" ).toURI().toURL().toExternalForm();
                    LOGGER.debug( "Loading image: {}", path );
                    cardImages.put( c, new Image( path ) );
                    index++;
                    updateProgress( (float)index, (double)game.getDeck().size() );
                }


                LOGGER.debug( "Loaded {} images", cardImages.size() );

                String path =  "/uno-dark-background.png";
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.GAME_BACKGROUND, new Image( path ) );

                path = new File( texturepack + "/" + "CARD_BACK.png" ).toURI().toURL().toExternalForm();
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.CARD_BACK, new Image( path ) );

                path = "/uno-spectate.png";
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.SPECTATOR_BACKGROUND, new Image( path ) );

                path = "/current-player.png";
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.CURRENT_USER, new Image( path ) );

                path = "/player.png";
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.OTHER_USER, new Image( path ) );

                path = "/user.png";
                LOGGER.debug( "Loading image: {}", path );
                sceneImages.put( SceneImage.DEFAULT_AVATAR, new Image( path ) );

                return null;
            }
        };

        Stage dialogStage = new Stage();
        dialogStage.initStyle( StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // PROGRESS BAR
        final Label label = new Label();
        label.setText("alerto");

        ProgressBar pb  = new ProgressBar();
        pb.setProgress(-1F);

        final VBox hb = new VBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(new Text("Loading resources.."), pb);

        Scene scene = new Scene(hb);
        dialogStage.setScene(scene);

        pb.progressProperty().bind(t.progressProperty());
        dialogStage.show();

        t.setOnSucceeded(event -> {
            dialogStage.close();
        });

        dialogStage.show();

        Thread thread = new Thread(t);
        thread.start();
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
