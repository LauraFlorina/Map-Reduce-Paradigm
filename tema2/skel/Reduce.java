import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Reduce extends RecursiveTask<ReduceResult> {
    String documentName;
    ArrayList<MapResult> mapResults;

    public Reduce(String documentName, ArrayList<MapResult> mapResults) {
        this.documentName = documentName;
        this.mapResults = mapResults;
    }


    // Unifica HashMap-urile corespunzatoare fragmentelor documentului
    // Pastreaza ArrayList ul cu cuvintele maximale
    // Returneaza prin efect lateral numarul cuvintelor maximale, precum si dimensiunea lor
    public MapResult Combine(AtomicInteger numberOfMaxLenWords, AtomicInteger dimOfMaxWords) {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        ArrayList<String> maxLenWords = new ArrayList<String>();
        int maxLen = -1;
        for (MapResult result : mapResults) {
            for (int key : result.map.keySet()) {
                if (hashMap.containsKey(key)) {
                    hashMap.put(key, hashMap.get(key) + result.map.get(key));
                } else {
                    hashMap.put(key, result.map.get(key));
                }
            }
            if (result.maxLenWords.get(0).length() > maxLen) {
                maxLenWords.clear();
                maxLenWords = result.maxLenWords;
                maxLen = result.maxLenWords.get(0).length();
            } else if (result.maxLenWords.get(0).length() == maxLen) {
                for (String word : result.maxLenWords) {
                    maxLenWords.add(word);
                }
            }
        }

        numberOfMaxLenWords.set(maxLenWords.size());
        dimOfMaxWords.set(maxLenWords.get(0).length());
        MapResult docResult = new MapResult(documentName, hashMap, maxLenWords);
        return docResult;
    }

    public int getFibonacciNumber(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return getFibonacciNumber(n - 1) + getFibonacciNumber(n - 2);
    }

    // Calcularea rangului documentului
    public float Process(MapResult docResult) {
        int sum = 0;
        int count = 0;
        for (int key : docResult.map.keySet()) {
            if (key == 0) {
                continue;
            }
            sum += getFibonacciNumber(key + 1) * docResult.map.get(key);
            count += docResult.map.get(key);
        }
        return (float)sum / count;
    }

    @Override
    protected ReduceResult compute() {
        AtomicInteger numberOfMaxLenWords = new AtomicInteger();
        AtomicInteger dimOfMaxLenWords = new AtomicInteger();
        MapResult docResult = Combine(numberOfMaxLenWords, dimOfMaxLenWords);
        float rank = Process(docResult);
        return new ReduceResult(rank, numberOfMaxLenWords.get(), dimOfMaxLenWords.get());
    }
}
