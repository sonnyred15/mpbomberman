package org.amse.bomberman.util;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ImageUtilities {

    private ImageUtilities() {
    }

    public static Image initImage(String resourceName, int width, int heigth) throws IOException {
        URL url = ImageUtilities.class.getResource(resourceName);
        return initImage(url, width, heigth);
    }

    public static Image initImage(URL url, int width, int heigth) throws IOException {
        Image image = ImageIO.read(url);//returns BufferedImage
        image = rescaleImage(image, width, heigth);
        return image;
    }

    /**
     * Resizes an image.
     *
     * @param image source image to scale.
     * @param width desired width.
     * @param heigth desired height.
     * @return new resized image.
     */
    public static Image rescaleImage(Image image, int width, int heigth) {
        return image.getScaledInstance(width,
                                       heigth,
                                       Image.SCALE_SMOOTH);
    }
}
