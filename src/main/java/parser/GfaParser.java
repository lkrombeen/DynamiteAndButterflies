package parser;

import graph.SequenceNode;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.toIntExact;

/**
 * This class contains a parser to parse a .gfa file into our data structure.
 */
public class GfaParser extends Thread {
    private String header1;
    private String header2;
    private HTreeMap<Long, String> sequenceMap;
    private HTreeMap<Long, int[]> adjacencyHMap;
    public DB db;
    public DB db2;
    String filePath;

    public GfaParser(String absolutePath) {
        filePath = absolutePath;
    }

    @Override
    public void start() {
        try {
            parseGraph(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method parses the file specified in filepath into a sequence graph.
     * @param filePath A string specifying where the file is stored.
     * @return Returns a sequenceGraph
     * @throws IOException For instance when the file is not found
     */
    @SuppressWarnings("Since15")
    public HTreeMap<Long, int[]> parseGraph(String filePath) throws IOException {
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        String[] partPaths = filePath.split(pattern);
        String partPath = partPaths[partPaths.length - 1];
        System.out.println(partPath);


        db = DBMaker.fileDB(partPath +  ".sequence.db").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().make();
        db2 = DBMaker.fileDB(partPath +  ".adjacency.db").fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().make();
        if (db.get(partPath + ".sequence.db") != null) {
            adjacencyHMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            sequenceMap = db2.hashMap(partPath + ".adjacency.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            return adjacencyHMap;
        } else {
            adjacencyHMap = db.hashMap(partPath + ".sequence.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.INT_ARRAY).createOrOpen();
            sequenceMap = db2.hashMap(partPath + ".adjacency.db").keySerializer(Serializer.LONG).valueSerializer(Serializer.STRING).createOrOpen();
            return parseSpecific(filePath, false);
        }
    }

    /**
     * Getter for the sequenceHashMap.
     * @return The HashMap.
     */
    public HTreeMap<Long, String> getSequenceHashMap() {
        return sequenceMap;
    }

    /**
     * Getter for the AdjacencyHMap.
     * @return The HashMap.
     */
    public HTreeMap<Long, int[]> getAdjacencyHMap() {
        return this.adjacencyHMap;
    }

    /**
     * Parses the file with a boolean whether to create a db file or not. Creates the Graph
     * @param filePath The file to parse/
     * @param exists Does the db file already exist?
     * @return The sequenceGraph
     * @throws IOException Reader.
     */
    @SuppressWarnings("Since15")
    private HTreeMap<Long, int[]> parseSpecific(String filePath, Boolean exists) throws IOException {
        InputStream in = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        header1 = line.split("H")[1];
        line = br.readLine();
        header2 = line.split("H")[1];
        ArrayList<Integer> temp = new ArrayList<Integer>();
        int parentId = 0;
        while ((line = br.readLine()) != null) {
            int childId;
            if (line.startsWith("S")) {
                if(parentId > 0) {
                    if(!exists) {
                        adjacencyHMap.put((long) parentId, convertIntegers(temp));
                        temp = new ArrayList<Integer>();
                    }

                }
                String[] data = line.split(("\t"));
                int id = Integer.parseInt(data[1]);
                SequenceNode node = new SequenceNode(toIntExact(id));
                if (!exists) {
                    sequenceMap.put((long) (id), data[2]);
                }
            } else if (line.startsWith("L")) {
                String[] edgeDataString = line.split("\t");
                parentId = (Integer.parseInt(edgeDataString[1]));
                childId = Integer.parseInt(edgeDataString[3]);
                temp.add(childId);
                //addToList(parentId, childId);
            }
        }
        in.close();
        br.close();
        db.commit();
        return adjacencyHMap;
    }


    /**
     * Cretes an ArrayList of Strings specifying headers.
     * @return an arrayList containing all headers
     */
    public ArrayList<String> getHeaders() {
        ArrayList<String> headers = new ArrayList<String>();
        headers.add(header1);
        headers.add(header2);
        return headers;
    }

    /**
     * Converts an List<Integer> to int[]
     * @param integers list with integers
     * @return int[]
     */
    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}
