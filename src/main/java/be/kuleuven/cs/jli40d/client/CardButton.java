package be.kuleuven.cs.jli40d.client;

import be.kuleuven.cs.jli40d.core.model.Card;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Created by Anton D.
 */
public class CardButton
{
    private int x, y, w, h;
    private Card    c;
    private boolean hovered;

    public CardButton( int x, int y, int w, int h, Card c )
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.c = c;
        this.hovered = false;
    }

    public void update( double mousePosX, double mousePosY )
    {
        if ( isIn( mousePosX, mousePosY ) )
            hovered = true;
        else
            hovered = false;
    }

    public boolean isIn( double mousePosX, double mousePosY )
    {
        return mousePosX > x && mousePosX < x + w && mousePosY > y && mousePosY < y + h;
    }

    public void render( GraphicsContext gc )
    {
        if ( hovered )
            gc.drawImage( ImageLoader.getCardImage( c ), x - 5, y - 5, w + 10, h + 10 );
        else
            gc.drawImage( ImageLoader.getCardImage( c ), x, y, w, h );
    }

    public int getX()
    {
        return x;
    }

    public void setX( int x )
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY( int y )
    {
        this.y = y;
    }

    public int getW()
    {
        return w;
    }

    public int getH()
    {
        return h;
    }

    public Card getC()
    {
        return c;
    }
}
