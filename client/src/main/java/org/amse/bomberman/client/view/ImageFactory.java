package org.amse.bomberman.client.view;

import java.awt.Image;
import javax.swing.ImageIcon;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.ImageUtilities;

/**
 * Class that corresponds for creating Images that used in application.
 *
 * @author Kirilchuk V.E.
 */
public class ImageFactory {
    private static final String iconPathPrefix = "org/amse/bomberman/client/icons/";
    private static final String BOMB_ICON_PATH = iconPathPrefix + "bomb-48.png";
    private static final String WALL_ICON_PATH = iconPathPrefix + "wall-wood-48.png";
    private static final String PL1_ICON_PATH  = iconPathPrefix + "superman-48-1.png";
    private static final String PL2_ICON_PATH  = iconPathPrefix + "superman-48-2.png";
    private static final String PL3_ICON_PATH  = iconPathPrefix + "superman-48-3.png";
    private static final String PL4_ICON_PATH  = iconPathPrefix + "superman-48-4.png";
    private static final String EXPL_ICON_PATH = iconPathPrefix + "burn-48.png";
    private static final String LIFE_ICON_PATH = iconPathPrefix + "heart-48.png";

    private static final String B_RADIUS_ICON_PATH = iconPathPrefix + "b_radius-48.png";
    private static final String B_COUNT_ICON_PATH = iconPathPrefix + "b_count-48.png";

    private static ImageIcon ICON_PL1  = ImageUtilities.loadIcon(PL1_ICON_PATH);
    private static ImageIcon ICON_PL2  = ImageUtilities.loadIcon(PL2_ICON_PATH);
    private static ImageIcon ICON_PL3  = ImageUtilities.loadIcon(PL3_ICON_PATH);
    private static ImageIcon ICON_PL4  = ImageUtilities.loadIcon(PL4_ICON_PATH);

    private static ImageIcon ICON_WALL = ImageUtilities.loadIcon(WALL_ICON_PATH);
    private static ImageIcon ICON_BOMB = ImageUtilities.loadIcon(BOMB_ICON_PATH);
    private static ImageIcon ICON_EXPL = ImageUtilities.loadIcon(EXPL_ICON_PATH);

    private static ImageIcon ICON_BONUS_LIFE     = ImageUtilities.loadIcon(LIFE_ICON_PATH);
    private static ImageIcon ICON_BONUS_B_RADIUS = ImageUtilities.loadIcon(B_RADIUS_ICON_PATH);
    private static ImageIcon ICON_BONUS_B_COUNT  = ImageUtilities.loadIcon(B_COUNT_ICON_PATH);

    /**
     * Returnes image for specified id.
     *
     * @param imageId id of image to get.
     * @return image that is binded for given id or null if no such
     * image was founded.
     */
    public Image getImage(int imageId) {//TODO CLIENT use map not ugly switch
        Image icon = null;
        switch (imageId) {
            case Constants.MAP_EMPTY: {
                icon = null;
                break;
            }
            case Constants.MAP_BOMB: {
                icon = ICON_BOMB.getImage();
                break;
            }
            case Constants.MAP_DETONATED_BOMB: {
                icon = ICON_EXPL.getImage();
                break;
            }
            case Constants.MAP_EXPLOSION: {
                icon = ICON_EXPL.getImage();
                break;
            }
            case Constants.MAP_BONUS_LIFE: {
                icon = ICON_BONUS_LIFE.getImage();
                break;
            }
            case Constants.MAP_BONUS_BOMB_COUNT: {
                icon = ICON_BONUS_B_COUNT.getImage();
                break;
            }
            case Constants.MAP_BONUS_BOMB_RADIUS: {
                icon = ICON_BONUS_B_RADIUS.getImage();
                break;
            }
        }
        if (imageId < Constants.MAP_EMPTY && imageId >= Constants.MAP_PROOF_WALL) {
            icon = ICON_WALL.getImage();
        } else if (imageId > Constants.MAP_EMPTY && imageId <= Constants.MAX_PLAYERS) {
            icon = getPlayerIcon(imageId);
        }
        return icon;
    }

    private Image getPlayerIcon(int playerId) {
        switch (playerId) {
            case 1:
                return ICON_PL1.getImage();
            case 2:
                return ICON_PL2.getImage();
            case 3:
                return ICON_PL3.getImage();
            case 4:
                return ICON_PL4.getImage();
            default:
                return ICON_PL1.getImage();
        }
    }
}
