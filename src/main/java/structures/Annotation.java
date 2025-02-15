package structures;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by lex_b on 13/06/2017.
 *
 * Saves the annotations in an annotation object.
 * Uses this setUp to ensure that it can be loaded into a tableView.
 */
public class Annotation implements Comparable<Annotation> {
    private SimpleIntegerProperty start = new SimpleIntegerProperty();
    private SimpleIntegerProperty end = new SimpleIntegerProperty();
    private SimpleStringProperty info = new SimpleStringProperty();
    private SimpleBooleanProperty selected  = new SimpleBooleanProperty();
    private int identifier;
    private boolean highlighted;
    /**
     * Constructor of the object.
     * @param identifier the ID of the annotation
     * @param startArg the start coördinate of the annotation
     * @param endArg the end coördinate of the annotation
     * @param infoArg the information with the annotation
     */
    public Annotation(int identifier, int startArg, int endArg, String infoArg) {
        this.identifier = identifier;
        start.set(startArg);
        end.set(endArg);
        info.set(infoArg);
        selected.set(false);
        this.highlighted = false;
    }

    public int getId() {
        return this.identifier;
    }

    public int getStart() {
        return start.get();
    }

    public int getEnd() {
        return end.get();
    }

    public String getInfo() {
        return info.get();
    }

    public SimpleBooleanProperty getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean getHighlighted() {
        return this.highlighted;
    }

    /**
     * Selects all the annotations.
     * @param allAnnotations Selects all these annotations
     */
    public static void selectAll(HashMap<Integer, TreeSet<Annotation>> allAnnotations) {
        TreeSet<Annotation> selectThese = new TreeSet<>();
        for (int i = 0; i <= allAnnotations.size(); i++) {
            TreeSet<Annotation> tempAnnotations = allAnnotations.get(i);
            if (tempAnnotations != null) {
                selectThese.addAll(tempAnnotations);
            }
        }
        //TODO make this a lambda
        for (Annotation annotation : selectThese) {
            annotation.setSelected(true);
        }
    }

    @Override
    public String toString() {
        String res = getInfo();
        res = res.replace("\t", "\n");
        res = res.replace("=", ":\t");
        return res;
    }

    @Override
    public int compareTo(@NotNull Annotation o) {
        if (this.getStart() < o.getStart()) {
            return -1;
        } else if (this.getStart() > o.getStart()) {
            return 1;
        } else {
            if (this.getEnd() > o.getEnd()) {
                return -1;
            } else if (this.getEnd() < o.getEnd()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}