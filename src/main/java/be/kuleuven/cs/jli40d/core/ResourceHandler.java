package be.kuleuven.cs.jli40d.core;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  The resourcehandler exposes paths on the application server to handle the loading of resource packs.
 *  Resource packs contain:
 *  - Card images
 *  - Various scene images
 *  And can be extended to serve more resources.
 */
public interface ResourceHandler extends Remote, Serializable
{
    /**
     *  Returns the name of the current resource pack. If the client does not have it,
     *  it should download the images and store them locally.
     */
    String getCurrentResourcePackName() throws RemoteException;

    /**
     *  Returns a byte array representing the image.
     *  @param resourcePackName The resource pack that the image belongs to
     *  @param imageName The requested image
     */
    byte [] getImage( String resourcePackName, String imageName ) throws IOException;
}
