package GUI;

import graph.SequenceGraph;
import graph.SequenceNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import parser.GfaParser;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality in the main screen of the application.
 */
public class MenuController {

    public TextField nodeTextField;
    public TextField radiusTextField;
    private boolean flagView;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Canvas canvas;
    @FXML
    private AnchorPane canvasPanel;
    @FXML
    private Label numNodesLabel;
    @FXML
    private Label numEdgesLabel;
    private GraphicsContext gc;
    private GraphDrawer drawer;
    private SequenceGraph graph;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        flagView = false;
    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa can be selected and directly be visualised on the screen.
     */
    @FXML
    public void openFileClicked() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        //fileChooser.setInitialDirectory(this.getClass().getResource("/resources").toString());
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                openFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openFile(File file) throws IOException {
        GfaParser parser = new GfaParser();
        System.out.println("src/main/resources/" + file.getName());
        graph = parser.parse(file.getAbsolutePath());
        drawer = new GraphDrawer(graph, gc);
        drawer.drawShapes();
        displayInfo(graph);
    }

    public void displayInfo(SequenceGraph graph) {
        numNodesLabel.setText(graph.getNodes().size() + "");
        numEdgesLabel.setText(graph.getEdges().size() + "");
    }

    @FXML
    public void zoomInClicked() throws IOException {
        if(!nodeTextField.getText().equals("")) {
            int columnId = drawer.getColumnId(Integer.parseInt(nodeTextField.getText()));
        } else {
            drawer.zoomIn(0.8, drawer.getRealCentreNode().getColumn());
        }

    }

    @FXML
    public void zoomOutClicked() throws IOException {
        if(!nodeTextField.getText().equals("")) {
            drawer.zoomIn(1.2, drawer.getColumnId(Integer.parseInt(nodeTextField.getText())));
        } else {
            drawer.zoomIn(1.2, drawer.getRealCentreNode().getColumn());
        }
    }

    private double pressedX;

    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        pressedX = mouseEvent.getX();
    }

    @FXML
    public void dragMouse(MouseEvent mouseEvent) {
        double xDifference = pressedX - mouseEvent.getX() / 2;
        drawer.moveShapes(xDifference);
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        flagView = true;
        int radius = Integer.parseInt(radiusTextField.getText());

        drawer.changeZoom(radius * 2, getStartColumn());
    }

    /**
     * Gets the start column based on the text fields.
     * @return integer representing the starting column
     */
    private int getStartColumn() {
        String text = nodeTextField.getText();
        int centreNode = Integer.parseInt(nodeTextField.getText());
        int radius = Integer.parseInt(radiusTextField.getText());

        int startNode = centreNode - radius;
        if (startNode < 1) {
            startNode = 1;
        }
        return graph.getNode(startNode).getColumn();
    }

    private int getStartColumn(SequenceNode centreNode) {
        if (!nodeTextField.getText().equals("")) {
            return getStartColumn();
        } else {
            String text = nodeTextField.getText();
            int radius = 2000;
            int centreNum = centreNode.getId();

            int startNode = centreNum - radius;
            if (startNode < 1) {
                startNode = 1;
            }
            return graph.getNode(startNode).getColumn();
        }
    }
}
