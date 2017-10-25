package be.kuleuven.cs.jli40d.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Pieter
 * @version 1.0
 */
public class MouseEventListener implements MouseListener
{

    private static final Logger LOGGER = LoggerFactory.getLogger(MouseEventListener.class);

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e
     */
    @Override
    public void mouseClicked( MouseEvent e )
    {
        LOGGER.debug( "Clicked on {}", e.getComponent().getClass().getName() );

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e
     */
    @Override
    public void mousePressed( MouseEvent e )
    {

    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e
     */
    @Override
    public void mouseReleased( MouseEvent e )
    {

    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e
     */
    @Override
    public void mouseEntered( MouseEvent e )
    {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e
     */
    @Override
    public void mouseExited( MouseEvent e )
    {

    }
}
