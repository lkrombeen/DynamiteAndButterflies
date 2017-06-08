package gui.sub_controllers;

import gui.GraphDrawer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;

import java.util.Observable;

/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController extends Observable {

    public static final int EXTEND_LEFT = 0;
    public static final int EXTEND_RIGHT = 1;

    private final ScrollBar scrollbar;
    private final GraphDrawer drawer;
    private boolean active;

    /**
     * Constructor.
     * @param scrollBar Scrollbar.
     * @param drawer The graphdrawer.
     */
    public PanningController(ScrollBar scrollBar, GraphDrawer drawer) {
        this.scrollbar = scrollBar;
        this.drawer = drawer;
        initialize();
    }

    /**
     * Initializes the scrollbar and adds a listener to it.
     * The listener is only active when it is manually changed, not by zooming.
     */
    private void initialize() {
        scrollbar.setMax(drawer.getZoomLevel());
        scrollbar.setVisibleAmount(drawer.getZoomLevel());
        scrollbar.setValue(scrollbar.getMax() / 2);
        scrollbar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number oldVal, Number newVal) {
                if (active) {
                    drawer.moveShapes(drawer.getxDifference()
                            + (newVal.doubleValue() - oldVal.doubleValue()));
                }
                if (drawer.getLeftbound() < 0) {
                    setChanged();
                    notifyObservers(EXTEND_LEFT);
                } else if (drawer.getRightbound() > drawer.getRange()) {
                    setChanged();
                    notifyObservers(EXTEND_RIGHT);
                }
            }
        });
    }

    /**
     * Change the scrollbar value and visible amount when zooming in by scrolling.
     * @param column Column that is the centre of the zooming.
     */
    public void setScrollbarSize(double column) {
        active = false;
        scrollbar.setValue(column);
        scrollbar.setVisibleAmount(drawer.getZoomLevel());
        active = true;
    }

}
