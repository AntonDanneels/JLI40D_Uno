package be.kuleuven.cs.jli40d.server.application;

import be.kuleuven.cs.jli40d.core.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ResourceManager extends UnicastRemoteObject implements ResourceHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ResourceManager.class.getName() );

    private String currentResourcePack;

    public ResourceManager() throws RemoteException
    {
        currentResourcePack = "default_texturepack";
    }

    public String getCurrentResourcePackName() throws RemoteException
    {
        return currentResourcePack;
    }

    public byte[] getImage( String resourcePackName, String imageName ) throws IOException
    {
        LOGGER.info( "Received image request for {}/{}", resourcePackName, imageName );

        // "src/main/resources/"
        String                dirName = System.getProperty( "user.home" ) + "/uno/server_texturepacks/" + resourcePackName;
        ByteArrayOutputStream baos    = new ByteArrayOutputStream(1000);

        System.out.println( new File(dirName,imageName).getAbsolutePath() );

        BufferedImage         img     = ImageIO.read(new File(dirName,imageName));

        ImageIO.write(img, "png", baos);
        baos.flush();

        byte [] image = baos.toByteArray();

        baos.close();

        return image;
    }
}
