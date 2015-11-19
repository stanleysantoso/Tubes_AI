import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Stanley on 11/18/2015.
 */
public class kNN {
    public static List<String[]> dataset = new ArrayList<String[]>();
    public static List<String[]> instances = new ArrayList<String[]>();

    public static void main (String args[]){
        dataset = getData("data/weather.nominal.arff");
        instances = getData("data/unlabeled_weather.arff");
        System.out.print("Menu : \n" +
                "1. Classify Full Training \n" +
                "2. Classify 10-fold cross validation \n");
        Scanner input = new Scanner(System.in);
        int opt = new Integer(input.nextLine());
        if (opt == 1) {
            System.out.println("Insert k (integer)");
            int k = new Integer(input.nextLine());
            ClassifyFull(k);
        }
        else if (opt == 2) {
            System.out.println("Insert k (integer)");
            int k = new Integer(input.nextLine());
            Classify10fold(k);
        }
        else {
            System.out.println("Wrong input !");
        }
    }

    public static List<String[]> getData(String path){
        File file = new File(path);
        List<String[]> data = new ArrayList<String[]>();
        Scanner scnr = null;
        try {
            scnr = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found !");
        }
        do{
            String line = scnr.nextLine();
            if(!line.contains("@") && line.length()>0) {
                String[] elements = line.split(",");
                data.add(elements);
            }
        }while(scnr.hasNext());

        return data;
    }

    // currently full training
    public static List<String[]> Classify(int k, List<String[]> trainingSet, List<String[]> testSet){
        int distance;
        int index;
        List<String[]> results = new ArrayList<String[]>();
        for(String[] unlabeledRow : testSet) {
            index = 0;
            Map<Integer, Integer> distanceMap = new TreeMap<Integer, Integer>();
            for (String[] row : trainingSet) {
                distance = 0;
                for (int i = 0; i < row.length; i++) {
                    if (!row[i].equals(unlabeledRow[i])) {
                        distance++;
                    }
                }
                distanceMap.put(index, distance);
                index++;
            }

            // sort map of distance to get nearest neighbour
            distanceMap = sortByComparator(distanceMap);
            // get k nearest neighbour labels
            List<String> labels = new ArrayList<String>();
            for (int j = 0; j < k; j++) {
                int tempIndex = (int) distanceMap.keySet().toArray()[j];
                String tempLabel = trainingSet.get(tempIndex)[trainingSet.get(tempIndex).length - 1];
                labels.add(tempLabel);
            }

            //get most popular element in list of labels and assign label to unlabeled row
            unlabeledRow[unlabeledRow.length - 1] = getPopularElement(labels);
            results.add(unlabeledRow);
        }
        return results;
    }

    private static void writeToFile(String path, List<String[]> labeledSet){
        File file = new File(path);
        Scanner scnr = null;
        Writer writer = null;
        try {
            scnr = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found !");
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("data/labeled_weather.arff"), "utf-8"));
            String line = null;
            do{
                line = scnr.nextLine();
                writer.write(line+"\n");
            }while(!line.equals("@data"));
            for(String[] row : labeledSet){
                String output = "";
                for(String val : row){
                    output += val + ",";
                }
                output= output.substring(0,output.length()-1);
                writer.write(output+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {writer.close();} catch (Exception ex) {ex.printStackTrace();}
        }
    }

    private static Map<Integer, Integer> sortByComparator(Map<Integer, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
        for (Iterator<Map.Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<Integer, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private static String getPopularElement(List<String> list)
    {
        int count = 1, tempCount;
        String popular = list.get(0);
        String temp = null;
        for (int i = 0; i < (list.size() - 1); i++)
        {
            temp = list.get(i);
            tempCount = 0;
            for (int j = 1; j < list.size(); j++)
            {
                if (temp == list.get(j))
                    tempCount++;
            }
            if (tempCount > count)
            {
                popular = temp;
                count = tempCount;
            }
        }
        return popular;
    }

    private static void Classify10fold(int k){
        int length = new Integer(dataset.size());
        int testSize = length/10;
        int trainingSize = 1-testSize;
        List<String[]> trainingSet = dataset;
        List<String[]> testSet = new ArrayList<String[]>();
        //partitioning dataset into testset and trainingset
        for(int i=0;i<testSize;i++){
            //add random data from dataset into testSet
            //Note: ThreadLocalRandom can only be used with Java 1.7 or later
            int tempIndex = ThreadLocalRandom.current().nextInt(0, dataset.size() + 1);
            testSet.add(trainingSet.get(tempIndex));
            trainingSet.remove(tempIndex);
        }
        writeToFile("data/unlabeled_weather.arff", Classify(k,trainingSet,testSet));
    }

    private static void ClassifyFull(int k){
        writeToFile("data/unlabeled_weather.arff", Classify(k,dataset,instances));
    }


}


