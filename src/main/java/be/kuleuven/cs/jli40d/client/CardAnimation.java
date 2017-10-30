package be.kuleuven.cs.jli40d.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Pair;

/**
 * Created by Anton D.
 */
public class CardAnimation
{
    private static final int ANIMATION_TIME   = 300;
    private static final int MS_PER_CYCLE     = 16;
    private static final int PIXELS_PER_CYCLE = ANIMATION_TIME / MS_PER_CYCLE;

    private double startX, startY;
    private double endX, endY;
    private double deltaX, deltaY;
    private double currentX, currentY;
    private Image image;
    private boolean isAlive;

    private int steps;

    public CardAnimation( Pair<Integer,Integer> startPoint, Pair<Integer,Integer> endPoint, Image img )
    {
        this.steps = 0;
        this.image = img;
        isAlive = true;

        startX = currentX = startPoint.getKey();
        startY = currentY = startPoint.getValue();
        endX = endPoint.getKey();
        endY = endPoint.getValue();

        deltaX = (endX - startX) / PIXELS_PER_CYCLE;
        deltaY = (endY - startY) / PIXELS_PER_CYCLE;
    }

    public void update()
    {
        steps++;
        currentX += deltaX;
        currentY += deltaY;

        if ( Math.abs(endX - currentX) <= Math.abs(deltaX) && Math.abs(endY - currentY) <= Math.abs(deltaY) )
            isAlive = false;
    }

    public void render( GraphicsContext gc )
    {
        gc.setGlobalAlpha( 1.0 / steps );
        gc.drawImage( image, currentX, currentY, GameSceneHandler.CARD_WIDTH, GameSceneHandler.CARD_HEIGHT );
        gc.setGlobalAlpha( 1.0 );
    }

    public boolean isAlive()
    {
        return isAlive;
    }
}
