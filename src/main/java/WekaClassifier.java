/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Stanley, Andarias
 */

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class WekaClassifier
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        Scanner input=new Scanner (System.in);
        // Read input
        System.out.println("MENU");
        System.out.println("1. Input From Dataset");
        System.out.println("2. Load From Model");
        int menu;
        menu = input.nextInt();

        if (menu==1)
        {
            System.out.println("Please select your method");
            System.out.println("1. KNN");
            System.out.println("2. Naive-Bayes");
            System.out.println("3. ID3");
            System.out.println("4. Artificial Neural Network");
            int ans;
            ans=input.nextInt();

            System.out.println("Please select your technique");
            System.out.println("1. 10 fold cross validation");
            System.out.println("2. Full training");
            int ans2;
            ans2=input.nextInt();

            FromDataset(ans,ans2);
        }
        else if (menu==2)
        {
            System.out.println("Please select your type");
            System.out.println("1. KNN");
            System.out.println("2. Naive-Bayes");
            System.out.println("3. ID3");
            System.out.println("4. Artificial Neural Network");
            int ans;
            ans=input.nextInt();

            LoadModel(ans);
        }
        else {}
    }
    
    public static String path = "data/";
    public static String FileDataset = "weather.nominal.arff";
    public static String FileClassify = "unlabeled_weather.arff";
    public static String FileExternal = "tubesAI.arff";
    public static String FileOutputClassify = "OutputKlasifikasi.arff";
    public static String ModelKNN = "ModelKNN.model";
    public static String ModelNB = "ModelNB.model";
    public static String ModelID3 = "ModelID3.model";
    public static String ModelANN = "ModelANN.model";
    
    public static IBk knn = new IBk();
    public static NaiveBayes NB = new NaiveBayes();
    public static J48 id3 = new J48();
    public static MultilayerPerceptron ann = new MultilayerPerceptron();
    
    public static void LoadModel(int type) throws Exception
    {
        if (type==1)
        {
            knn = (IBk) SerializationHelper.read(path + ModelKNN);
            System.out.println(knn.toString());
        }
        else if (type==2)
        {
            NB = (NaiveBayes) SerializationHelper.read(path + ModelNB);
            System.out.println(NB.toString());
        }
        else if (type==3)
        {
            id3 = (J48) SerializationHelper.read(path + ModelID3);
            System.out.println(id3.toString());
        }
        else if (type==4)
        {
            ann = (MultilayerPerceptron) SerializationHelper.read(path + ModelANN);
            System.out.println(ann.toString());
        }
        Classify(type);
    }
    
    public static void Classify(int ans1) throws Exception
    {
        // Load unlabeled data and set class attribute
        Instances unlabeled = DataSource.read(path + FileClassify);
        unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
        // create copy
        Instances labeled = new Instances(unlabeled);
        // label instances
        double clsLabel = 0;
        for (int i = 0; i < unlabeled.numInstances(); i++) 
        {
            if (ans1==1)
                clsLabel = knn.classifyInstance(unlabeled.instance(i));
            else if (ans1==2)
                clsLabel = NB.classifyInstance(unlabeled.instance(i));
            else if (ans1==3)
                clsLabel = id3.classifyInstance(unlabeled.instance(i));
            else if (ans1==4)
                clsLabel = ann.classifyInstance(unlabeled.instance(i));
            labeled.instance(i).setClassValue(clsLabel);
        }        
        // save newly labeled data
        DataSink.write(path + FileOutputClassify, labeled);
    }
    
    public static void FromDataset(int ans1, int ans2) throws Exception
    {   
        // Read dataset
        BufferedReader breader;
        
        breader = new BufferedReader(new FileReader(path+FileDataset));
        Instances data1 = new Instances(breader);
        Instances test = DataSource.read(path + FileDataset);
        test.setClassIndex(test.numAttributes()-1);
        data1.setClassIndex(data1.numAttributes()-1);
        
        // Olah data
        Evaluation eval = new Evaluation(data1);
        
        if (ans1==1)    // KNN
        {
            knn.buildClassifier(data1);
            if (ans2==1)    // 10 fold cross validation
                eval.crossValidateModel(knn, data1, 10, new Random(1));
            else if (ans2==2)   // full training
            {
                eval.evaluateModel(knn, test);
            }
        }
        else if (ans1==2)   // Naive Bayes
        {
            NB.buildClassifier(data1);
            if (ans2==1)    // 10 fold cross validation
                eval.crossValidateModel(NB, data1, 10, new Random(1));
            else if (ans2==2)   // full training
            {
                eval.evaluateModel(NB, test);
            }
        }
        else if (ans1==3)   // ID3
        {
            id3.buildClassifier(data1);
            if (ans2==1)    // 10 fold cross validation
                eval.crossValidateModel(id3, data1, 10, new Random(1));
            else if (ans2==2)   // full training
            {
                eval.evaluateModel(id3, test);
            }
        }
        else if (ans1==4)   // ANN
        {
            ann.buildClassifier(data1);
            if (ans2==1)    // 10 fold cross validation
                eval.crossValidateModel(ann, data1, 10, new Random(1));
            else if (ans2==2)   // full training
            {
                eval.evaluateModel(ann, test);
            }
        }
        // Save to file external as ARFF
        DataSink.write(path + FileExternal, data1);
        
        SaveModel(ans1);
        Classify(ans1);
        // Print output
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        System.out.println(eval.toMatrixString());
        System.out.println(eval.toClassDetailsString());
    }


    
    public static void SaveModel(int ans1) throws Exception
    {
        // Save Model
        if (ans1==1)
            SerializationHelper.write(path + ModelKNN, knn);
        else if (ans1==2)
            SerializationHelper.write(path + ModelNB, NB);
        else if (ans1==3)
            SerializationHelper.write(path + ModelID3, id3);
        else if (ans1==4)
            SerializationHelper.write(path + ModelANN, ann);
    }

    public Evaluation getEvalWeka (int opt1, int opt2, String trainingSetPath) throws Exception {
        BufferedReader breader;

        breader = new BufferedReader(new FileReader(trainingSetPath));
        Instances data1 = new Instances(breader);
        Instances test = DataSource.read(trainingSetPath);
        test.setClassIndex(test.numAttributes()-1);
        data1.setClassIndex(data1.numAttributes()-1);

        // Olah data
        Evaluation eval = new Evaluation(data1);

        if (opt1==1)    // KNN
        {
            knn.buildClassifier(data1);
            if (opt2==1)   // full training
            {
                eval.evaluateModel(knn, test);
            }
            else if (opt2==2) {    // 10 fold cross validation
                eval.crossValidateModel(knn, data1, 10, new Random(1));
            }
        }
        else if (opt1==2)   // Naive Bayes
        {
            NB.buildClassifier(data1);
            if (opt2==1)   // full training
            {
                eval.evaluateModel(NB, test);
            }
            else if (opt2==2) {    // 10 fold cross validation
                eval.crossValidateModel(NB, data1, 10, new Random(1));
            }
        }
        return eval;
    }
}    