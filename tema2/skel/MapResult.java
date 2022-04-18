import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapResult {
    String docName;
    HashMap<Integer, Integer> map;
    ArrayList<String> maxLenWords;

    public MapResult() {
    }

    public MapResult(String docName, HashMap<Integer, Integer> map, ArrayList<String> list) {
        this.docName = docName;
        this.map = map;
        this.maxLenWords = list;
    }


}
