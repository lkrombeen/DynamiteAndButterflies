package gui.sub_controllers;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.GraphDrawer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;


/**
 * Created by Jasper van Tilburg on 29-5-2017.
 *
 * Controls the panning functionality.
 */
public class PanningController {

    public static final double PANN_FACTOR = 0.01;

    private GraphDrawer drawer;
    private Button rightPannButton;
    private Button leftPannButton;
    private Timeline timelineRight;
    private Timeline timelineLeft;
    private boolean updating;

    public PanningController(GraphDrawer drawer, Button leftPannButton, Button rightPannButton) {
        this.drawer = drawer;
        this.leftPannButton = leftPannButton;
        this.rightPannButton = rightPannButton;
        initializeTimer();
        initializeButtons();
    }

    public void initializeTimer() {
        timelineRight = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pannRight();
            }
        }));
        timelineRight.setCycleCount(Animation.INDEFINITE);

        timelineLeft = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pannLeft();
            }
        }));
        timelineLeft.setCycleCount(Animation.INDEFINITE);
    }

    public void initializeButtons() {
        rightPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineRight.play();
            }
        });
        rightPannButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineRight.pause();
            }
        });

        leftPannButton.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineLeft.play();
            }
        });
        leftPannButton.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                timelineLeft.pause();
            }
        });
    }

    public void initializeKeys(Node canvasPanel) {
        canvasPanel.requestFocus();
        canvasPanel.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    timelineRight.play();
                } else if (event.getCode() == KeyCode.LEFT) {
                    timelineLeft.play();
                }
                event.consume();
            }
        });
        canvasPanel.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.RIGHT) {
                    timelineRight.stop();
                } else if (event.getCode() == KeyCode.LEFT) {
                    timelineLeft.stop();
                }
                event.consume();
            }
        });
    }

    public void pannRight() {
        if (!updating) {
            if (drawer.getGraph().getRightBoundIndex() < drawer.getGraph().getFullGraphRightBoundIndex()) {
                if (drawer.getxDifference() + drawer.getZoomLevel() + 100 > drawer.getRange()) {
                    new Thread(new Task<Integer>() {
                        @Override
                        protected Integer call() throws Exception {
                            updating = true;
                            System.out.println("OLD: getRightBoundIndex: " + drawer.getGraph().getRightBoundIndex() + ", getFullGraphRightBoundIndex: " + drawer.getGraph().getFullGraphRightBoundIndex() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            int leftMostID = drawer.getMostLeftNode().getId();
                            SequenceGraph newGraph = drawer.getGraph().copy();
                            newGraph.createSubGraph(drawer.getGraph().getCenterNodeID() + 50, 50, drawer.getGraph().getPartPath());
                            drawer.setGraph(newGraph);
                            drawer.initGraph();
                            drawer.setxDifference(drawer.getColumnWidth(drawer.getGraph().getNode(leftMostID).getColumn()));
                            drawer.moveShapes(drawer.getColumnWidth(drawer.getMostLeftNode().getColumn()));
                            System.out.println("NEW: getRightBoundIndex: " + drawer.getGraph().getRightBoundIndex() + ", getFullGraphRightBoundIndex: " + drawer.getGraph().getFullGraphRightBoundIndex() + ", getCentreNodeID: " + drawer.getGraph().getCenterNodeID());
                            updating = false;
                            return null;
                        }
                    }).start();
                }
            }
        }
        if (drawer.getGraph().getNodes().containsKey(drawer.getGraph().getFullGraphRightBoundID())) {
            if (drawer.getxDifference() + drawer.getZoomLevel() > drawer.getColumnWidth(drawer.getGraph().getColumns().size())) {
                return;
            }
        }
        drawer.moveShapes(drawer.getxDifference() + drawer.getZoomLevel() * PANN_FACTOR);
    }

    public void pannLeft() {
        drawer.moveShapes(drawer.getxDifference() - drawer.getZoomLevel() * PANN_FACTOR);
    }

}
