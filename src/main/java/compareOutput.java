import java.util.List;

/**
 * Created by Stanley on 11/20/2015.
 */
public class compareOutput {
    private static WekaClassifier wc = new WekaClassifier();
    private static kNN knearest = new kNN();
    private static NaiveBayes nb = new NaiveBayes();
    public static void main (String args[]) throws Exception {

        List<String[]> instances = knearest.getData("data/labeled_weather.arff");
        List<String[]> wekaResult = knearest.getData("data/OutputKlasifikasi.arff");
        int count = 0;
        for(int i=0; i<instances.size();i++){
            if(wekaResult.get(i)[wekaResult.get(i).length-1].equals(instances.get(i)[instances.get(i).length-1])){
                count++;
            }
        }
        System.out.println("Perbandingan : " + ((float)count/instances.size()) * 100 + "%");
    }
}
