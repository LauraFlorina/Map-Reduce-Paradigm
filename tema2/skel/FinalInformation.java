import java.util.Comparator;

public class FinalInformation implements Comparable {
    String documentName;
    int index;
    ReduceResult reduceResult;

    public FinalInformation(String documentName, int index, ReduceResult reduceResult) {
        this.documentName = documentName;
        this.index = index;
        this.reduceResult = reduceResult;
    }

    @Override
    public int compareTo(Object o) {
        FinalInformation object = (FinalInformation)o;
        if (this.reduceResult.rank > object.reduceResult.rank) {
            return -1;
        } else if (this.reduceResult.rank < object.reduceResult.rank) {
            return 1;
        } else {
            if (this.index < object.index) {
                return -1;
            } else if (this.index > object.index) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
