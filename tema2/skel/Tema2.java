import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

//Nume: Țălină Laura-Florina
//Grupa: 334CB
public class Tema2 {

    // Metoda care returneaza un ArrayList continand dictionarele corespunzatoare fragmentelor
    // pentru documentul de la calea documentName
    public static ArrayList<MapResult> getMapResultsForDoc(String documentName, List<MapResult> mapResults) {
            ArrayList<MapResult> docResults = new ArrayList<MapResult>();
            for (MapResult result : mapResults) {
                if (result.docName.equals(documentName)) {
                    docResults.add(result);
                }
            }
            return docResults;
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        int workersNumber = Integer.valueOf(args[0]);
        String inFile = args[1];
        String outFile = args[2];

        // Valori citite din fisierul de intrare
        int fragmentDimension = 0;
        int numberOfFiles = 0;
        ArrayList<String> documentsName = new ArrayList<String>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            String line = reader.readLine();
            int lineCount = 0;
            while (line != null) {
                if (lineCount == 0) {
                    fragmentDimension = Integer.parseInt(line);
                } else if (lineCount == 1) {
                    numberOfFiles = Integer.parseInt(line);
                } else {
                    documentsName.add(line);
                }
                lineCount++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<MapResult> mapResults = Collections.synchronizedList(new ArrayList<MapResult>());

        // Creare ForkJoinPool pentru operatiile Map
        ForkJoinPool fjp = new ForkJoinPool(workersNumber);
        for (String docName : documentsName) {
            File doc = new File(docName);
            int bytes = (int)doc.length();
            int i;
            int offset = 0;
            for (i = 0; i < bytes/fragmentDimension; i+= 1) {
                Map mapTask = new Map(docName, offset, fragmentDimension);
                MapResult result = fjp.invoke(mapTask);
                mapResults.add(result);
                offset += fragmentDimension;
            }
            if ((bytes - offset) > 0) {
                Map mapTask = new Map(docName, offset, bytes - i);
                MapResult result = fjp.invoke(mapTask);
                mapResults.add(result);
            }
        }


        HashMap<String, ReduceResult> results = new HashMap<String, ReduceResult>();

        // Creare ForkJoinPool pentru operatiile Reduce
        ForkJoinPool fjp2 = new ForkJoinPool(workersNumber);
        for (String docName : documentsName) {
            Reduce reduceTask = new Reduce(docName, getMapResultsForDoc(docName, mapResults));
            ReduceResult reduceResult = fjp2.invoke(reduceTask);
            results.put(docName, reduceResult);
        }

        // Sortare fisiere in functie de rank si de ordinea in care apar in fisierul
        // de intrare
        // Pentru aceasta, se va folosi clasa FinalInformation, care are implementata
        // metoda compareTo
        ArrayList<FinalInformation> finalInfo = new ArrayList<FinalInformation>();
        int index = 0;
        for (String docName: documentsName) {
            FinalInformation info = new FinalInformation(docName, index, results.get(docName));
            finalInfo.add(info);
            index += 1;
        }
        Collections.sort(finalInfo);

        try {
            FileWriter fileWriter = new FileWriter(outFile);
            for (int i = 0; i < finalInfo.size(); i++) {
                String docPath = finalInfo.get(i).documentName;
                int lastIndex = docPath.lastIndexOf("/");
                fileWriter.write(docPath.substring(lastIndex + 1));
                fileWriter.write(",");
                float rank = finalInfo.get(i).reduceResult.rank;
                String rankString = String.format("%.2f", rank);
                fileWriter.write(rankString);
                fileWriter.write(",");
                fileWriter.write(String.valueOf(finalInfo.get(i).reduceResult.dimOfMaxLenWords));
                fileWriter.write(",");
                fileWriter.write(String.valueOf(finalInfo.get(i).reduceResult.numberOfMaxLenWords));
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
