package gui.subControllers;

import javafx.scene.control.*;
import java.util.prefs.Preferences;

/**
 * Created by Jip on 17-5-2017.
 * A BookmarkController class moving some logic from the MenuController into a different class.
 */
public class BookmarkController {

    private static Preferences prefs;
    private String stringFile;
    private Button bookmark1, bookmark2;

    /**
     * Constructor of the bookmark controller to handle the bookmarks.
     * @param bm1 The button with the first bookmark.
     * @param bm2 The button with the second bookmark.
     */
    public BookmarkController(Button bm1, Button bm2) {
        bookmark1 = bm1;
        bookmark2 = bm2;

        stringFile = "";
        prefs = Preferences.userRoot();
    }

    /**
     * Loads the bookmarks of the specific file stringFile.
     * @param stringOfFile The file that is being loaded whose bookmarks should be loaded.
     */
    public void loadBookmarks(String stringOfFile) {
        stringFile = stringOfFile;

        if (prefs.getInt("bookmarkNum" + stringFile, -1) == -1) {
            prefs.putInt("bookmarkNum" + stringFile, 0);
        }

        int largestIndex = prefs.getInt("bookmarkNum" + stringFile, -1);
        int i = -1;

        while (i <= largestIndex) {
            int newIndex = i;
            String realBM = prefs.get(stringFile + newIndex, "-");
            updateBookmarks(realBM);
            i++;
        }
    }

    /**
     * Saves the bookmarks when the user presses save.
     * @param nodes The centre node
     * @param radius The radius of nodes we should save/show
     */
    public void saving(int nodes, int radius) {

        String stringFile = prefs.get("file", "def");
        int newIndex = prefs.getInt("bookmarkNum" + stringFile, -1);
        newIndex++;
        prefs.put(stringFile + newIndex, nodes + "-" + radius);
        prefs.putInt("bookmarkNum" + stringFile, newIndex);

        updateBookmarks(nodes + "-" + radius);
    }

    /**
     * Uodates all bookmarks.
     * @param newBookmark The string that should be added.
     */
    private void updateBookmarks(String newBookmark) {
        //TODO Add more visuals to this update

        bookmark2.setText(bookmark1.getText());
        bookmark1.setText(newBookmark);
    }
}
