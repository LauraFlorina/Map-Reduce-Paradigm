import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class Map extends RecursiveTask<MapResult> {
    String documentName;
    int offset;
    int fragmentDimension;

    public Map(String documentName, int offset, int fragmentDimension) {
        this.documentName = documentName;
        this.offset = offset;
        this.fragmentDimension = fragmentDimension;
    }

    // Aceasta metoda citeste fragmentul din fisier si returneaza un ArrayList continand
    // cuvintele corespunzatoare
    public ArrayList<String> getFragmentWords() {
        ArrayList<String> words = null;

        String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n";
        String fragmentString = null;
        int startsInTheMiddle = 0;
        int endsInTheMiddle = 0;
        int numberOfBytesReaded = 0;

        try {
            // Se citeste fragmentul corespunzator din fisier
            byte[] fragment = new byte[this.fragmentDimension];
            RandomAccessFile inFile = new RandomAccessFile(this.documentName, "r");
            inFile.seek(offset);
            numberOfBytesReaded = inFile.read(fragment);
            fragmentString = new String(fragment);

            // Se verifica daca fragmentul incepe in mijlocul unui cuvant
            if (! separators.contains(String.valueOf(fragmentString.charAt(0)))) {
                if (offset > 0) {
                    byte[] previousByte = new byte[1];
                    inFile.seek(offset - 1);
                    inFile.read(previousByte);
                    if (!separators.contains(new String(previousByte))) {
                        startsInTheMiddle = 1;
                    }
                }
            }

            // Se verifica daca fragmentul se termina in mijlocul unui cuvant
            String leftFromWord = "";
            if (! separators.contains(String.valueOf(fragmentString.charAt(numberOfBytesReaded - 1)))) {
                int newOffset = offset + fragmentDimension;
                File doc = new File(documentName);
                int totalBytes = (int) doc.length();

                while (newOffset <= totalBytes) {
                    byte[] nextByte = new byte[1];
                    inFile.seek(newOffset);
                    inFile.read(nextByte);
                    if (!separators.contains(new String(nextByte))) {
                        endsInTheMiddle = 1;
                        leftFromWord += new String(nextByte);
                    } else {
                        // cand intalnim un separator ne oprim din citirea caracterelor viitoare
                        break;
                    }
                    newOffset += 1;
                }
            }
            // Daca am intalnit un separator, si nu am citit niciun sufix al cuvantului
            // atunci fragmentul nu se termina in mijloc
            if (leftFromWord.equals("")) {
                endsInTheMiddle = 0;
            }

            words = new ArrayList<String>(Arrays.asList(fragmentString.split("[^a-zA-Z0-9]")));
            // Atunci cand se apeleaza split, in cazul in care avem 2 separatori apropiati, cuvantul
            // identificat va fi "", pe care trebuie sa il eliminam
            for (int i = 0; i < words.size();) {
                if (String.valueOf(words.get(i)).equals("")) {
                    words.remove(i);
                }
                i++;
            }

            // Daca fragmentul incepe in mijloc, se elimina primul cuvant
            // Daca fragmentul era compus dintr-un singur cuvant, atunci spatiul va ramane liber ""
            if (startsInTheMiddle == 1) {
                if (words.size() == 1) {
                    words.add("");
                }
                words.remove(0);
            }

            if (endsInTheMiddle == 1) {
                if (words.size() >= 1) {
                    String lastWord = words.get(words.size() - 1);
                    lastWord += leftFromWord;
                    words.set(words.size() - 1, lastWord);
                }
            }

            // Unele cuvinte pot avea la final '\0', care trebuie eliminat
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if (word.equals("")) {
                    continue;
                }
                if (word.charAt(word.length() - 1) == '\0') {
                    words.set(i, word.substring(0, word.length() - 1));
                }
            }

            inFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }


    @Override
    protected MapResult compute() {
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        ArrayList<String> maxLenWords = new ArrayList<String>();

        ArrayList<String> words = getFragmentWords();

        int maxLen = -1;
        for (String word : words) {
            if (hashMap.containsKey(word.length())) {
                hashMap.put(word.length(), hashMap.get(word.length()) + 1);
            } else {
                hashMap.put(word.length(), 1);
            }

            if (word.length() > maxLen) {
                maxLenWords.clear();
                maxLenWords.add(word);
                maxLen = word.length();
            } else if (word.length() == maxLen) {
                maxLenWords.add(word);
            }
        }

        MapResult mapResult = new MapResult(documentName, hashMap, maxLenWords);
        return mapResult;
    }
}
