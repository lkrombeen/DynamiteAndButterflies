package gui.sub_controllers;

import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Created by Jip on 8-6-2017.
 * <p>
 * The class handles the colour of the nodes.
 * <p>
 * It takes a int[] representing the selected genomes.
 * If it's empty it will always return the base.
 * If it is small it will find the correct colours for the relevant genomes.
 * If it is large it will choose a colour red wich is intense or not based on overlap.
 * <p>
 * I suppress these warnings because of the large amount of magic number determining colours.
 */
@SuppressWarnings("MagicNumber")
public class ColourController {

    private static final Color EDGE_BASE_COLOUR = Color.BLACK;
    private static final Color NODE_BASE_COLOUR = Color.gray(0.5098);

    private int[] selectedGenomes;
    private boolean rainbowView;
    private int lowerPart;
    private int middlePart;
    private int higherPart;

    //The amount of splits we make when the selection is large
    private static final int SPLIT_WHEN_LARGE = 4;

    /**
     * Constructor of the colourController.
     *
     * @param allSelectedGenomes A int[] with all the selected genomes.
     * @param rainbowViewArg     A boolean with if rainbowView is turned on or off.
     */
    public ColourController(int[] allSelectedGenomes, boolean rainbowViewArg) {
        selectedGenomes = allSelectedGenomes;
        this.rainbowView = rainbowViewArg;
        initialize();
    }

    /**
     * Initializes the colourController.
     */
    private void initialize() {
        int size = selectedGenomes.length;

        if (size == 0) {
            return;
        }

        lowerPart = size / SPLIT_WHEN_LARGE;
        middlePart = lowerPart * 2;
        higherPart = lowerPart + middlePart;
    }

    /**
     * Returns the position of the genome in the checkSet.
     * <p>
     * The position from checkSet is really handy for the colours because
     * the getSingle colours is based on the index of selected genomes.
     * But because that is only the single ones you can only handily use this
     * for small selections.
     *
     * @param checkSet the int[] we will be looking through
     * @param genome   the int we are looking for in the checkset
     * @return returns the position of genome in the checkset.
     */
    public int containsPos(int[] checkSet, int genome) {
        if (checkSet != null) {
            for (int i = 0; i < checkSet.length; i++) {
                int check = checkSet[i];
                if (check == genome) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Gets a colour assigned to the selected genome.
     *
     * @param positionInSelection an integer representing it's position in the selectedGenomes array
     * @return A color.
     */
    private Color getSingle(int positionInSelection) {
        double hue = (360 / selectedGenomes.length) * positionInSelection;
        double brightness = 0.8 + (0.2 / selectedGenomes.length) * positionInSelection;
        return Color.hsb(hue, 1, brightness);
    }

    /**
     * With a large collection we only have difference in intensity of colours.
     * That is then based on the length of the genomes selected.
     *
     * @param length the length of the selected genomes.
     * @return a color red.
     */
    private Color notRainbowViewNode(int length) {
        if (length == 0) {
            return NODE_BASE_COLOUR;
        } else if (length < lowerPart) {
            return Color.color(0.9608, 0.8235, 0.8235);
        } else if (length < middlePart) {
            return Color.color(0.9216, 0.6157, 0.6157);
        } else if (length < higherPart) {
            return Color.color(0.8824, 0.4039, 0.4039);
        } else {
            return Color.color(0.8431, 0.2196, 0.2196);
        }
    }

    /**
     * This method gets an array of the different colours the node should be.
     *
     * @param genomes The genomes in the node.
     * @return The list of colours the node should be.
     */
    public ArrayList<Color> getNodeColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();
        //If there is no selection, it should only be the base colour.
        if (selectedGenomes.length == 0) {
            res.add(NODE_BASE_COLOUR);
            return res;
        }

        // In rainbowView we assign a colour to each genome
        if (rainbowView) {
            res = rainbowViewColours(genomes);
            if (res.isEmpty()) {
                res.add(NODE_BASE_COLOUR);
            }
            return res;
        } else {
            // Else we choose a colour Red.
            int length = getSizeContained(genomes);
            res.add(notRainbowViewNode(length));
            return res;
        }
    }

    /**
     * Gets the colour of an Annotation.
     *
     * @param startCorAnno Chooses one based on its start coordinate
     * @param stepSize     And how far it is in a setp.
     * @return The Color of the annotation
     */
    public Color getAnnotationColor(int startCorAnno, int stepSize) {
        double doubleStepSize = (double) stepSize;
        double hue = 360 - (360 * ((startCorAnno % doubleStepSize) / doubleStepSize));
        double brightness = 0.8 + (0.2 * ((startCorAnno % doubleStepSize) / doubleStepSize));
        return Color.hsb(hue, 1, brightness);
    }

    /**
     * A simple contains method.
     *
     * @param checkSet The set to check if it contains the genome.
     * @param genome   The genome to see if it is in the check set.
     * @return a boolean true if it is in the set or false if it is not.
     */
    public boolean contains(int[] checkSet, int genome) {
        for (int check : checkSet) {
            if (check == genome) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method gets an array of the different colours the edge should be.
     *
     * @param genomes The genomes through the edge.
     * @return The list of colours the edge should be.
     */
    public ArrayList<Color> getEdgeColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();
        //If there is no selection, it should only be the base colour.
        if (selectedGenomes.length == 0) {
            res.add(EDGE_BASE_COLOUR);
            return res;
        }

        // In rainbowView we assign a colour to each genome
        if (rainbowView) {
            res = rainbowViewColours(genomes);
        }
        if (res.isEmpty()) {
            res.add(EDGE_BASE_COLOUR);
        }

        return res;
    }

    /**
     * This method gets an array of the different colours the SNPEdge should be.
     *
     * @param genomes The genomes through the SNPEdge.
     * @return The list of colours the SNPEdge should be.
     */
    public ArrayList<Color> getSNPEdgeColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<Color>();
        //If there is no selection, it should not be drawn
        if (selectedGenomes.length == 0) {
            return res;
        }

        res = getNodeColours(genomes);
        if (res.contains(NODE_BASE_COLOUR)) {
            res = new ArrayList<>();
        }
        return res;
    }

    private int getSizeContained(int[] genomes) {
        int res = 0;
        for (int genome : genomes) {
            if (contains(selectedGenomes, genome)) {
                res++;
            }
        }
        return res;
    }

    /**
     * Gets all the single colours of the genomes.
     *
     * @param genomes the int[] genomes
     * @return all genomes that are selected their colours.
     */
    private ArrayList<Color> rainbowViewColours(int[] genomes) {
        ArrayList<Color> res = new ArrayList<>();
        for (int genome : genomes) {
            int check = containsPos(selectedGenomes, genome);
            if (check != -1) {
                res.add(getSingle(check));
            }
        }
        return res;
    }

    public void setSelectedGenomes(int[] selected) {
        this.selectedGenomes = selected;
        initialize();
    }

    public void setRainbowView(boolean rainbowView) {
        this.rainbowView = rainbowView;
    }

    public Color getSNPColour(String base) {
        if (base != null) {
            switch (base) {
                case "C":
                    return Color.color(0, 0, 0.6196);
                case "A":
                    return Color.color(0, 0.6196, 0);
                case "G":
                    return Color.color(0.6196, 0.6196, 0);
                case "T":
                    return Color.color(0.6196, 0, 0);
                default:
                    return Color.CHOCOLATE;
            }
        }
        return Color.CHOCOLATE;
    }

    public Color getEdgeBaseColour() {
        return EDGE_BASE_COLOUR;
    }
}
