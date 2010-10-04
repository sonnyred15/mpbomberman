package org.amse.bomberman.util;

//~--- non-JDK imports --------------------------------------------------------

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.amse.bomberman.server.gameservice.GameMap;

/**
 *
 * @author Kirilchuk V.E.
 */
public class GameMapXMLParser {//TODO TEST IT!
    private static DocumentBuilder docBuilder = null;

    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public GameMap parseAndCreate(File XMLFile) throws IOException,
                                                       SAXException,
                                                       IllegalArgumentException,
                                                       DOMException {
        String  gameMapName = null;
        int     dimension   = 0;
        int[][] field       = null;
        int     maxPlayers  = 0;

        Document xml = docBuilder.parse(XMLFile);

        /* First child of Document is root element which must be <map name="..."> tag. */
        Node root = xml.getFirstChild();

        if (!root.getNodeName().equals("map")) {
            throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                               "Wrong root.");
        }

        /* Attributes of root tag. */
        NamedNodeMap attributes = root.getAttributes();

        /* Taking gameMap name from attribute. */
        Node nameNode = attributes.getNamedItem("name");

        if (nameNode != null) {
            gameMapName = nameNode.getNodeValue();
        } else {
            throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                               "No gameMap name.");
        }

        /* Taking gameMap maxPlayers from attribute. */
        Node maxPlayersNode = attributes.getNamedItem("maxPlayers");

        if (maxPlayersNode != null) {
            try {
                maxPlayers = Integer.parseInt(maxPlayersNode.getNodeValue());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                                   "Wrong maxPlayers value.");
            }
        } else {
            throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                               "No maxPlayers attribute.");
        }

        /* Taking gameMap dimension from attribute. */
        Node dim = attributes.getNamedItem("dimension");

        if (dim != null) {
            try {
                dimension = Integer.parseInt(dim.getNodeValue());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                                   "Wrong dimension value.");
            }
        } else {
            throw new IllegalArgumentException("Wrong xml gameMap file. " +
                                               "No dimension attribute.");
        }

        /* Taking other info about gameMap. */
        NodeList childNodes = root.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node item = childNodes.item(i);

            /* Taking field. */
            if (item.getNodeName().equals("field")) {
                field = parseField(item, dimension);
            }
        }

        return new GameMap(gameMapName, field, dimension, maxPlayers);
    }

    private int[][] parseField(Node fieldNode, int dimension)
                                             throws DOMException,
                                                    IllegalArgumentException { //TODO what if rowsNum<dimension

        int[][]  result      = new int[dimension][dimension];
        NodeList fieldChilds = fieldNode.getChildNodes();
        int      row         = 0;

        for (int k = 0; k < fieldChilds.getLength(); ++k) {
            Node child = fieldChilds.item(k);

            if (child.getNodeName().equals("row")) {
                String str = child.getTextContent();
                String[] cells = str.split(" ");
                if(cells.length!=dimension){
                   throw new IllegalArgumentException("Wrong xml gameMap file. "
                                                    + "Wrong field data.");
                }
                try {
                    for (int column = 0; column < cells.length; ++column) {
                        result[row][column] = Integer.parseInt(cells[column]);
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Wrong xml gameMap file. "
                                                     + "Wrong field data.");
                } catch (IndexOutOfBoundsException ex) {
                    throw new IllegalArgumentException("Wrong xml gameMap file. "
                                                     + "Wrong field data.");
                }
                row++;
            }
        }

        return result;
    }
}
