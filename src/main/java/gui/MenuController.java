package gui;

import graph.SequenceGraph;
import graph.SequenceNode;
import gui.subControllers.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.mapdb.HTreeMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.prefs.Preferences;

/**
 * Created by Jasper van Tilburg on 1-5-2017.
 *
 * Controller for the Menu scene. Used to run all functionality
 * in the main screen of the application.
 */
public class MenuController {

    @FXML
    private Button saveBookmark;
    @FXML
    private MenuItem file1;
    @FXML
    private MenuItem file2;
    @FXML
    private MenuItem file3;
    @FXML
    private Button bookmark1;
    @FXML
    private Button bookmark2;
    @FXML
    private Label sequenceInfo;
    @FXML
    private TextField nodeTextField;
    @FXML
    private TextField radiusTextField;
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
    @FXML
    private TextArea consoleArea;
    private PrintStream ps;

    private GraphicsContext gc;

    private Preferences prefs;

    private BookmarkController bookmarkController;
    private FileController fileController;
    private ZoomController zoomController;
    private InfoController infoController;
    private RecentController recentController;

    /**
     * Initializes the canvas.
     */
    @FXML
    public void initialize() {
        canvas.widthProperty().bind(canvasPanel.widthProperty());
        canvas.heightProperty().bind(canvasPanel.heightProperty());
        gc = canvas.getGraphicsContext2D();
        prefs = Preferences.userRoot();

        fileController = new FileController();
        infoController = new InfoController(numNodesLabel, numEdgesLabel, sequenceInfo);
        bookmarkController = new BookmarkController(bookmark1, bookmark2);
        recentController = new RecentController(file1, file2, file3);

        recentController.initialize(prefs);
        ps = new PrintStream(new Console(consoleArea));
        System.setErr(ps);
        System.setOut(ps);

    }

    /**
     * When 'open gfa file' is clicked this method opens a filechooser from which a gfa
     * can be selected and directly be visualised on the screen.
     * @throws IOException if there is no file specified.
     */
    @FXML
    public void openFileClicked() throws IOException {
        Stage stage = App.getStage();
        File file = fileController.chooseFile(stage);
        String filePath = fileController.openFileClicked(gc, file.getAbsolutePath());
        String fileName = fileController.fileNameFromPath(filePath);

        updateControllers(filePath, fileName);
        recentController.update(filePath, prefs);
    }

    public void openFileClicked(String filePath) throws IOException {
        fileController.openFileClicked(gc, filePath);
        String fileName = fileController.fileNameFromPath(filePath);

        updateControllers(filePath, fileName);
    }

    private void updateControllers(String filePath, String fileName) {
        Stage stage = App.getStage();
        String title = stage.getTitle();
        String split = "---";
        String[] parts = title.split(split);
        String offTitle = parts[0];
        stage.setTitle(offTitle + split + fileName);

        prefs.put("file", fileName);
        bookmarkController.loadBookmarks(fileName);
        zoomController = new ZoomController(fileController.getDrawer(),
                                nodeTextField, radiusTextField);

        displayInfo(fileController.getGraph());
    }

    private void displayInfo(SequenceGraph graph) {
        infoController.displayInfo(graph);
        zoomController.displayInfo();
    }

    /**
     * ZoomIn Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomInClicked() throws IOException {
        zoomController.zoomIn();
    }

    /**
     * ZoomOut Action Handler.
     * @throws IOException exception.
     */
    @FXML
    public void zoomOutClicked() throws IOException {
        zoomController.zoomOut();
    }

    @FXML
    public void scrollZoom(ScrollEvent scrollEvent) throws IOException {
        int column = fileController.getDrawer().mouseLocationColumn(scrollEvent.getX());
        if (scrollEvent.getDeltaY() > 0) {
            zoomController.zoomIn(column);
        } else {
            zoomController.zoomOut(column);
        }
    }

    /**
     * Get the X-Coordinate of the cursor on click.
     * @param mouseEvent the mouse event.
     */
    @FXML
    public void clickMouse(MouseEvent mouseEvent) {
        double pressedX = mouseEvent.getX();
        double pressedY = mouseEvent.getY();
        SequenceNode clicked = fileController.getDrawer().clickNode(pressedX, pressedY);
        if (clicked != null) {
            String newString = "Sequence: "
                    + fileController.getSequenceHashMap().get((long) clicked.getId());
            infoController.updateSeqLabel(newString);
        }
    }

    /**
     * Adds a button to traverse the graph with.
     */
    public void traverseGraphClicked() {
        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size());
        int centreNodeID = zoomController.getCentreNodeID();
        String newString = "Sequence: "
                            + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Adds a button to traverse the graph with.
     * @param centreNode specifies the centre node to be showed
     * @param radius specifies the radius to be showed
     */
    private void traverseGraphClicked(String centreNode, String radius) {
        int centreNodeID = Integer.parseInt(centreNode);
        int rad = Integer.parseInt(radius);

        zoomController.traverseGraphClicked(fileController.getGraph().getNodes().size(),
                                            centreNodeID, rad);
        String newString = "Sequence: "
                + fileController.getSequenceHashMap().get((long) centreNodeID);
        infoController.updateSeqLabel(newString);
    }

    /**
     * Updates and saves the bookmarks.
     */
    @FXML
    public void saveTheBookmarks() {
        bookmarkController.saving(zoomController.getCentreNode(), zoomController.getRadius());
    }

    /**
     * Pressed of the bookmark1 button.
     */
    @FXML
    public void pressBookmark1() {
        bookmarked(bookmark1);
    }

    /**
     * Pressed of the bookmark2 button.
     */
    @FXML
    public void pressBookmark2() {
        bookmarked(bookmark2);
    }

    /**
     * Method used to not duplicate code in working out bookmarks.
     * @param bookmark the button that specifies the bookmark
     */
    private void bookmarked(Button bookmark) {
        if (!bookmark.getText().equals("-")) {
            String string = bookmark.getText();
            String[] parts = string.split("-");
            String centre = parts[0];
            String radius = parts[1];
            traverseGraphClicked(centre, radius);
            zoomController.setNodeTextField(centre);
            zoomController.setRadiusTextField(radius);
        }
    }

    HTreeMap<Long, String> getSequenceHashMap() {
        return fileController.getSequenceHashMap();
    }

    /**
     * Button one of the File -> Recent menu.
     */
    @FXML
    public void file1Press() {
        pressedRecent(file1);
    }

    /**
     * Button two of the File -> Recent menu.
     */
    @FXML
    public void file2Press() {
        pressedRecent(file2);
    }

    /**
     * Button three of the File -> Recent menu.
     */
    @FXML
    public void file3Press() {
        pressedRecent(file3);
    }

    /**
     * Method used to not duplicate recentFile presses.
     * @param file the menuItem that has been pressed
     */
    private void pressedRecent(MenuItem file) {
        String filePath = recentController.pressedRecent(file);

        if (filePath == null) {
            System.out.println("Don't do that");
            try {
                openFileClicked();
            } catch (IOException e1) {
                System.out.println("Drukken op lege recent");
            }
        } else {
            try {
                openFileClicked(filePath);
            } catch (IOException e) {
                System.out.println("Semi-succesvol op recent gedrukt");
            }
        }
    }
}
