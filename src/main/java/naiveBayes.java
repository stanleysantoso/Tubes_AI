import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Stanley on 11/19/2015.
 */
public class NaiveBayes {
    public static List<String[]> dataset = new ArrayList<String[]>();
    public static List<String[]> instances = new ArrayList<String[]>();
    public static Map<String,List<String>> attributes = new LinkedHashMap<String,List<String>>();
    public static void main (String args[]){
        dataset = getData("data/weather.nominal.arff");
        instances = getData("data/unlabeled_weather.arff");
        attributes = getAttributes("data/weather.nominal.arff");
        Map<String,List<Float>> model = buildModel(dataset);
        System.out.print("Naive Bayes Menu : \n" +
                "1. Classify Full Training \n" +
                "2. Classify 10-fold cross validation \n");
        Scanner input = new Scanner(System.in);
        int opt = new Integer(input.nextLine());
        if (opt == 1) {
            ClassifyFull("data/unlabeled_weather.arff");
        }
        else if (opt == 2) {
            Classify10fold("data/unlabeled_weather.arff");
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

    public static List<String[]> Classify(Map<String,List<Float>> model, List<String[]> testSet){
        List<String[]> results = new ArrayList<String[]>();
        List<String> keys = new ArrayList<>();
        Object[] keySet = attributes.keySet().toArray();
        //Memasukkan entrySet (keys) ke dalam ArrayList
        for(Object entry : keySet){
            keys.add(entry.toString());
        }
        List<String> labels = attributes.get(keys.get(keys.size()-1));
        // Perhitungan
        for(String[] unlabeledRow : testSet){
            List<Float> probContainer = new ArrayList<>();
            for(String label : labels) {
                int labelIndex = attributes.get(keys.get(keys.size()-1)).indexOf(label);
                float prob = 1;
                for (int i = 0; i < unlabeledRow.length - 1; i++) {
                    String attr = unlabeledRow[i];
                    prob = prob * model.get(attr).get(labelIndex);
                }
                probContainer.add(prob*model.get(keys.get(keys.size()-1)).get(labelIndex));
            }
            unlabeledRow[unlabeledRow.length-1] = attributes.get(keys.get(keys.size()-1)).get(getMaxIndex(probContainer));
            results.add(unlabeledRow);
        }
        return results;
    }

    // Membentuk model pembelajaran dari trainingSet
    public static Map<String,List<Float>> buildModel(List<String[]> trainingSet){
        Map<String,List<Float>> model = new LinkedHashMap<>();
        List<Integer> labelCount = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        Object[] keySet = attributes.keySet().toArray();
        //Memasukkan entrySet (keys) ke dalam ArrayList
        for(Object entry : keySet){
            keys.add(entry.toString());
        }

        for(String label : attributes.get(keys.get(keys.size()-1))){
            int countLabel = 0;
            for(String[] row : trainingSet){
                if(row[row.length-1].equals(label)){
                    countLabel++;
                }
            }
            labels.add(label);
            labelCount.add(countLabel);
        }

        for(String key : keys){ // every attribute type
            if(keys.get(keys.size()-1).equals(key)){
                List<Float> labelProb = new ArrayList<>();
                for(int tempcount : labelCount){
                    labelProb.add( ((float)tempcount/trainingSet.size()));
                }
                model.put(key,labelProb);
            }
            else {
                List<String> tempAttr = attributes.get(key); // get all attribute with same type
                for(String attr : tempAttr) {
                    List<Float> attrCount = new ArrayList<>();
                    for (String label : labels) {
                        int count = 0;
                        for (String[] row : trainingSet) {
                            // condition row in dataset contains same key with specific label
                            if (row[keys.indexOf(key)].equals(attr) && row[row.length - 1].equals(label)) {
                                count++;
                            }
                        }
                        attrCount.add((float) count / labelCount.get(labels.indexOf(label)));
                    }
                    model.put(attr, attrCount);
                }
            }
        }

        return model;
    }

    public static Map<String,List<String>> getAttributes(String path){
        Map<String,List<String>> attributesContainer = new LinkedHashMap<>();
        File file = new File(path);
        Scanner scnr = null;
        try {
            scnr = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found !");
        }
        do{
            String line = scnr.nextLine();
            if(line.contains("@attribute")) {
                String[] tokens = line.split(" ",3);
                String[] tempAttr = tokens[2].substring(1,tokens[2].length()-1).split("\\W\\s");

                attributesContainer.put(tokens[1],new ArrayList<String>(Arrays.asList(tempAttr)));
            }

        }while(scnr.hasNext());
        return attributesContainer;
    }

    public static int getMaxIndex(List<Float> probs){
        float max = 0;
        for(Float f : probs){
            if(f>max){
                max=f;
            }
        }
        return probs.indexOf(max);
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

    private static void Classify10fold(String path){
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
        writeToFile(path, Classify(buildModel(trainingSet),testSet));
    }

    private static void ClassifyFull(String path){
        writeToFile(path, Classify(buildModel(dataset),instances));
    }



}
