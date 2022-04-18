public class ReduceResult {
    float rank;
    int numberOfMaxLenWords;
    int dimOfMaxLenWords;

    public ReduceResult(float rank, int numberOfMaxLenWords, int dimOfMaxLenWords) {
        this.rank = rank;
        this.dimOfMaxLenWords = dimOfMaxLenWords;
        this.numberOfMaxLenWords = numberOfMaxLenWords;
    }
}
